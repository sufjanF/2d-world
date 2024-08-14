package core;

import tileengine.TETile;
import tileengine.Tileset;
import tileengine.TERenderer;
import edu.princeton.cs.algs4.StdDraw;
import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The world engine for the game.
 * Handles and redirects to all relevant classes for executions.
 */
public class World implements Serializable {
    private static final long serialVersionUID = 1L;
    private Random random;
    private final ArrayList<int[]> hallwayCoords = new ArrayList<>();
    private Avatar avatar;
    private Oski oski;
    private UserInterface ui = new UserInterface(this);
    public static final int WIDTH = 70;
    public static final int HEIGHT = 45;
    public static final int HUD_HEIGHT = 5;
    private TETile[][] serializableTileMap;
    private List<int[]> alcoholPositions;
    private List<int[]> clipperCardPositions;
    private Set<String> pickedUpItems; // Track items
    private long seed;
    private Boolean playing;
    private boolean isInitialGeneration;

    public World() {
        this.ui = new UserInterface(this);
        this.alcoholPositions = new ArrayList<>();
        this.clipperCardPositions = new ArrayList<>();
        this.pickedUpItems = new HashSet<>(); // Initialize the set
        this.playing = true;
        this.isInitialGeneration = true;
    }

    /**
     * Get the avatar.
     * @return avatar in our scene.
     */
    public Avatar getAvatar() {
        return avatar;
    }

    /**
     * Get Oski.
     * @return current Oski.
     */
    public Oski getOski() {
        return oski;
    }

    /**
     * Get the serializable tile map.
     * @return tilemap that is serializable.
     */
    public TETile[][] getSerializableTileMap() {
        return serializableTileMap;
    }

    /**
     * Set the serializable tile map.
     * @param serializableTileMap instance of serializableTileMap.
     */
    public void setSerializableTileMap(TETile[][] serializableTileMap) {
        this.serializableTileMap = serializableTileMap;
    }

    /**
     * Start the game in main menu.
     */
    public void run() {
        ui.setMainMenu();
    }

    /**
     * Method prompts user to enter a seed and then starts game loop.
     */
    public void newGame() {
        Font font = new Font("Monospaced", Font.BOLD, 25);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(400, 300, "Enter Seed:");
        StringBuilder seedInput = new StringBuilder();
        StdDraw.show();
        boolean seedValidity = true;

        while (seedValidity) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (Character.isDigit(key)) {
                    seedInput.append(key);
                    StdDraw.clear(StdDraw.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.text(400, 300, "Enter Seed:");
                    StdDraw.text(400, 250, seedInput.toString());
                    StdDraw.show();
                } else if ((key == 'S' || key == 's') && seedInput.length() > 0) {
                    long seedValue = Long.parseLong(seedInput.toString());
                    TETile[][] world = new TETile[WIDTH][HEIGHT];
                    generateWorld(world, seedValue);
                    avatar = spawnAvatar(world, seedValue, avatar);
                    seedValidity = false;

                    // Initialize TERenderer, which also sets up StdDraw settings
                    TERenderer ter = new TERenderer();
                    ter.initialize(WIDTH, HEIGHT + HUD_HEIGHT);

                    // Render the initial frame
                    ter.renderFrame(world);

                    gameLoop(world, avatar, ter);
                }
            }
        }
    }

    /**
     * Generates the world with a seed. It uses helper functions that build walls, rooms, hallways, and place entities.
     * @param world the array of tiles that we want to draw onto.
     * @param worldSeed the seed we use for random generation.
     */
    public void generateWorld(TETile[][] world, long worldSeed) {
        int width = WIDTH;
        int height = HEIGHT;
        this.seed = worldSeed; // Save the seed
        random = new Random(worldSeed);
        serializableTileMap = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
                serializableTileMap[x][y] = Tileset.NOTHING;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                serializableTileMap[x][y] = world[x][y];
            }
        }
        drawRooms(world, 6, 14);
        drawLines(world);
        drawWalls(world);
        placeItems(world, worldSeed);
        spawnOski(world, worldSeed);

    }


    /**
     * Spawns the avatar at a random location and if that location is invalid, it increases its seed and tries again
     * @param world the tilemap array representing our world.
     * @param spawnSeed the seed.
     * @param spawnedAvatar the avatar we want to spawn.
     * @return the spanwed avatar.
     */
    public Avatar spawnAvatar(TETile[][] world, long spawnSeed, Avatar spawnedAvatar) {
        boolean validSpawn = false;
        int spawnX = 0;
        int spawnY = 0;
        int width = WIDTH;
        int height = HEIGHT;

        while (!validSpawn) {
            Random spawnRandom = new Random(spawnSeed);
            spawnX = spawnRandom.nextInt(width);
            spawnY = spawnRandom.nextInt(height);

            if (world[spawnX][spawnY] == Tileset.GRASS) {
                validSpawn = true;
                world[spawnX][spawnY] = Tileset.AVATAR;
                spawnedAvatar = new Avatar(spawnX, spawnY, this);
            } else {
                spawnSeed++;
            }
        }
        return spawnedAvatar;
    }

    /**
     * Ends game.
     */
    public void endGame() {
        playing = false;
    }


    /**
     * Acts as an update method handling gameplay.
     * @param world the tilemap for world.
     * @param currentAvatar the avatar.
     * @param ter the TERenderer we use for rendering.
     */
    public void gameLoop(TETile[][] world, Avatar currentAvatar, TERenderer ter) {
        int oldX = currentAvatar.getX();
        int oldY = currentAvatar.getY();
        StringBuilder keySequence = new StringBuilder();

        while (playing) {
            StdDraw.clear(Color.BLACK); // Clear the screen before rendering

            // Draw the game world without showing it yet
            ter.drawTiles(world);

            // Render the HUD on top of the game world
            ui.renderHUD();

            // Show the frame
            StdDraw.show();

            // Control the frame rate
            StdDraw.pause(20);

            // Handle keyboard input
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                currentAvatar.move(key, world);

                // Add key to the sequence
                keySequence.append(key);

                // Keep only the last 2 characters in the sequence
                if (keySequence.length() > 2) {
                    keySequence.deleteCharAt(0);
                }

                // Save and exit if ":q" or ":Q" is pressed
                if (keySequence.toString().equals(":q") || keySequence.toString().equals(":Q")) {
                    SaveState.saveGame(this);
                    System.exit(0);
                }
            }

            // Update the position of the avatar
            int newX = currentAvatar.getX();
            int newY = currentAvatar.getY();

            // Check if the avatar has moved
            if (oldX != newX || oldY != newY) {
                world[oldX][oldY] = Tileset.GRASS;
                world[newX][newY] = Tileset.AVATAR;
                serializableTileMap[oldX][oldY] = Tileset.GRASS;
                serializableTileMap[newX][newY] = Tileset.AVATAR;
                oldX = newX;
                oldY = newY;
            }

            // Update tile information based on mouse position
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            if (isValid(world, (int) mouseX, (int) mouseY)) {
                String tileDescription = world[(int) mouseX][(int) mouseY].description();
                ui.updateTileInfo(tileDescription);
            }
        }
    }




    /**
     * Helper method Converts xy-coordinates to string.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @return a string of the coordinates.
     */
    private String positionToString(int x, int y) {
        return x + "," + y;
    }

    /**
     * Draws hallways between coordinates stored in hallwayCoords which we made in the room generation.
     * @param world the tilemap for world.
     */
    public void drawLines(TETile[][] world) {
        ArrayList<int[]> coords = hallwayCoords;
        for (int i = 0; i < coords.size() - 1; i++) {
            int[] start = coords.get(i);
            int[] end = coords.get(i + 1);

            if (start[0] != end[0]) {
                int direction = (start[0] < end[0]) ? 3 : 2;
                int length = Math.abs(end[0] - start[0]);
                singleLine(world, start[0], start[1], length, direction);
            }

            if (start[1] != end[1]) {
                int direction = (start[1] < end[1]) ? 1 : 0;
                int length = Math.abs(end[1] - start[1]);
                singleLine(world, end[0], start[1], length, direction);
            }
        }
    }

    /**
     * Draws a line of a specified length in a given direction in world.
     *
     * @param world the world where the line will be drawn.
     * @param startX the starting x-coordinate of our line.
     * @param startY the starting y-coordinate of our line.
     * @param length the length of the line.
     * @param direction the direction which the line goes in:
     *                    0: up
     *                    1: down
     *                    2: left
     *                    3: right
     */
    private void singleLine(TETile[][] world, int startX, int startY, int length, int direction) {
        switch (direction) {
            case 0:
                for (int i = 0; i < length; i++) {
                    if (isValid(world, startX, startY - i)) {
                        world[startX][startY - i] = Tileset.GRASS;
                    }
                }
                break;
            case 1:
                for (int i = 0; i < length; i++) {
                    if (isValid(world, startX, startY + i)) {
                        world[startX][startY + i] = Tileset.GRASS;
                    }
                }
                break;
            case 2:
                for (int i = 0; i < length; i++) {
                    if (isValid(world, startX - i, startY)) {
                        world[startX - i][startY] = Tileset.GRASS;
                    }
                }
                break;
            case 3:
                for (int i = 0; i < length; i++) {
                    if (isValid(world, startX + i, startY)) {
                        world[startX + i][startY] = Tileset.GRASS;
                    }
                }
                break;
            default:
                System.out.println("invalid input");
                break;
        }
    }

    /**
     * Draws walls around rooms.
     * @param world the tilemap for world.
     */
    public void drawWalls(TETile[][] world) {
        int width = WIDTH;
        int height = HEIGHT;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (world[x][y] == Tileset.GRASS) {
                    if (isValid(world, x + 1, y) && world[x + 1][y] == Tileset.NOTHING) {
                        world[x + 1][y] = Tileset.WALL;
                    }
                    if (isValid(world, x - 1, y) && world[x - 1][y] == Tileset.NOTHING) {
                        world[x - 1][y] = Tileset.WALL;
                    }
                    if (isValid(world, x, y + 1) && world[x][y + 1] == Tileset.NOTHING) {
                        world[x][y + 1] = Tileset.WALL;
                    }
                    if (isValid(world, x, y - 1) && world[x][y - 1] == Tileset.NOTHING) {
                        world[x][y - 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

    /**
     * Checks whether cooardinates are within worlds boundaries.
     * @param world the tilemap for world.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @return true if valid.
     */
    private boolean isValid(TETile[][] world, int x, int y) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }

    /**
     * Draws rooms.
     * @param world the tilemap for world.
     * @param max the minimum size of a room.
     * @param max the maximum size of a room.
     */
    public void drawRooms(TETile[][] world, int min, int max) {
        int width = WIDTH;
        int height = HEIGHT;
        int roomCount = random.nextInt(3) + 10;
        int i = 0;

        while (i < roomCount) {
            int roomWidth = random.nextInt(max - min) + min;
            int roomHeight = random.nextInt(max - min) + min;
            int startX = random.nextInt(width - roomWidth);
            int startY = random.nextInt(height - roomHeight);

            int hallwayX = startX + random.nextInt(roomWidth);
            int hallwayY = startY + random.nextInt(roomHeight);

            // Check if the room is within two tiles of width and height
            if (startX <= 2 || startX + roomWidth >= width - 2 || startY <= 2 || startY + roomHeight >= height - 2) {
                // Move back in loop and retry with new seed
                random.setSeed(random.nextLong() + 1);
                continue;
            }

            hallwayCoords.add(new int[]{hallwayX, hallwayY});

            for (int x = startX; x < startX + roomWidth; x++) {
                for (int y = startY + 1; y < startY + roomHeight; y++) {
                    world[x][y] = Tileset.GRASS;
                    serializableTileMap[x][y] = Tileset.GRASS;
                }
            }
            i++;
        }
    }


    /**
     * Places items randomly in the world.
     * @param world the tilemap for world.
     * @param itemSeed the seed.
     */
    public void placeItems(TETile[][] world, long itemSeed) {
        Random itemRandom = new Random(itemSeed);

        if (isInitialGeneration) {
            int beerCount = itemRandom.nextInt(3) + 1; // 1 to 3 beers
            int clipperCardCount = 1; // 1 to 3 clipper cards

            // Place beers
            for (int i = 0; i < beerCount; i++) {
                placeRandomItem(world, itemRandom, Tileset.BEER);
            }

            // Place clipper cards
            for (int i = 0; i < clipperCardCount; i++) {
                placeRandomItem(world, itemRandom, Tileset.CLIPPER_CARD);
            }

            isInitialGeneration = false; // Set to false after initial generation
        } else {
            // Existing logic for placing items when loading a game
            for (int[] pos : alcoholPositions) {
                if (!pickedUpItems.contains(positionToString(pos[0], pos[1]))) {
                    world[pos[0]][pos[1]] = Tileset.BEER;
                }
            }
            for (int[] pos : clipperCardPositions) {
                if (!pickedUpItems.contains(positionToString(pos[0], pos[1]))) {
                    world[pos[0]][pos[1]] = Tileset.CLIPPER_CARD;
                }
            }
        }
    }

    /**
     * Helper method used by placeItems, to place items randomly.
     *
     * @param world the tilemap for world.
     * @param rand our already in use random object.
     * @param itemType the type of item to be placed, which should be Tileset#BEER or Tileset#CLIPPER_CARD.
     *
     * The method ensures that the item is placed only on a tile that has not been previously picked up,
     * and avoids placing items on tiles that are not of type {@link Tileset#GRASS}. If a valid position is found,
     * the item is placed and the method exits. If no valid position is found in 100 attempts, the method will stop.
     */
    private void placeRandomItem(TETile[][] world, Random rand, TETile itemType) {
        int attempts = 0;
        while (attempts < 100) { // Limit attempts to prevent infinite loop
            int itemX = rand.nextInt(WIDTH);
            int itemY = rand.nextInt(HEIGHT);
            if (world[itemX][itemY] == Tileset.GRASS && !pickedUpItems.contains(positionToString(itemX, itemY))) {
                world[itemX][itemY] = itemType;
                if (itemType == Tileset.BEER) {
                    alcoholPositions.add(new int[]{itemX, itemY});
                } else if (itemType == Tileset.CLIPPER_CARD) {
                    clipperCardPositions.add(new int[]{itemX, itemY});
                }
                break;
            }
            attempts++;
        }
    }


    /**
     * Allows the avatar to pick up items, placing them in the player inventory and removing from scene.
     *
     * @param world the tilemap for world.
     */
    public void pickUpItem(TETile[][] world) {
        int avatarX = avatar.getX();
        int avatarY = avatar.getY();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = avatarX + dx;
                int y = avatarY + dy;
                if (isValid(world, x, y)) {
                    if (world[x][y] == Tileset.BEER) {
                        world[x][y] = Tileset.GRASS;
                        System.out.println("Picked up beer bottle.");
                        pickedUpItems.add(x + "," + y);  // Add this line
                        ui.addItem(Tileset.BEER);
                    } else if (world[x][y] == Tileset.CLIPPER_CARD) {
                        world[x][y] = Tileset.GRASS;
                        System.out.println("Picked up a clipper card.");
                        pickedUpItems.add(x + "," + y);  // Add this line
                        ui.addItem(Tileset.CLIPPER_CARD);
                    }
                }
            }
        }
    }

    /**
     * Allows the avatar to converse with Oski, if he is within range.
     *
     * @param world the tilemap for world.
     */
    public void interactWithOski(TETile[][] world) {
        int avatarX = avatar.getX();
        int avatarY = avatar.getY();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = avatarX + dx;
                int y = avatarY + dy;
                if (isValid(world, x, y)) {
                    if (world[x][y] == Tileset.OSKI) {
                        System.out.println("Interacted with Oski."); // Debug statement
                        ui.showDialogueBox();
                        ui.renderHUD();
                    }
                }
            }
        }
    }



    /**
     * Spawns Oski at a random location. If the location is invalid we increase seed by 1
     * and we try again.
     *
     * @param world the tilemap for world.
     * @param oskiSeed the seed.
     */
    public void spawnOski(TETile[][] world, long oskiSeed) {
        Random itemRandom = new Random(oskiSeed);
        int width = WIDTH;
        int height = HEIGHT;
        boolean oskiSpawned = false;

        while (!oskiSpawned) {
            int oskiX = itemRandom.nextInt(width);
            int oskiY = itemRandom.nextInt(height);
            if (world[oskiX][oskiY] == Tileset.GRASS) {
                world[oskiX][oskiY] = Tileset.OSKI;
                oski = new Oski(oskiX, oskiY);
                oskiSpawned = true;
            } else {
                oskiSeed++;
                itemRandom = new Random(oskiSeed);
            }
        }
    }



    /**
     * Reloads a saved game object.
     */
    public void loadGame() {
        World loadedWorld = SaveState.loadGame();
        if (loadedWorld != null) {
            this.random = loadedWorld.random;
            this.hallwayCoords.clear();
            this.hallwayCoords.addAll(loadedWorld.hallwayCoords);
            this.avatar = loadedWorld.avatar;
            this.oski = loadedWorld.oski;
            this.serializableTileMap = loadedWorld.serializableTileMap;
            this.alcoholPositions = loadedWorld.alcoholPositions;
            this.clipperCardPositions = loadedWorld.clipperCardPositions;
            this.pickedUpItems = loadedWorld.pickedUpItems;
            this.seed = loadedWorld.seed;
            this.isInitialGeneration = false;
            TETile[][] world = new TETile[WIDTH][HEIGHT];
            this.ui = new UserInterface(this);
            this.ui.setPlayerInventory(loadedWorld.ui.getPlayerInventory());

            regenerateWorld(world);  // Regenerate the world based on the saved seed

            // Initialize rendering settings
            StdDraw.setCanvasSize(WIDTH * 16, (HEIGHT + HUD_HEIGHT) * 16);
            StdDraw.setXscale(0, WIDTH);
            StdDraw.setYscale(0, HEIGHT + HUD_HEIGHT);
            StdDraw.enableDoubleBuffering();

            // Render the initial frame
            TERenderer ter = new TERenderer();
            ter.initialize(WIDTH, HEIGHT + HUD_HEIGHT);
            ter.renderFrame(world);

            System.out.println("Starting game loop after loading");
            gameLoop(world, avatar, ter);
        } else {
            System.out.println("Failed to load game state.");
        }
    }


    /**
     * Ensures that picked up items aren't spawned in, oski and the avatar are in the correct location.
     *
     * @param world the tilemap for world.
     */
    public void regenerateWorld(TETile[][] world) {

        generateWorld(world, this.seed);  // Use the saved seed to regenerate the world

        // Ensure the avatar, items, and Oski are placed correctly after loading the state
        if (this.avatar == null) {
            avatar = spawnAvatar(world, seed, avatar);
        }
        world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
        world[oski.getX()][oski.getY()] = Tileset.OSKI;

        // Remove items that were picked up
        for (String posStr : pickedUpItems) {
            String[] posArr = posStr.split(",");
            int x = Integer.parseInt(posArr[0]);
            int y = Integer.parseInt(posArr[1]);
            world[x][y] = Tileset.GRASS;
        }

        // Place the remaining items back
        for (int[] pos : alcoholPositions) {
            if (!pickedUpItems.contains(positionToString(pos[0], pos[1]))) {
                world[pos[0]][pos[1]] = Tileset.BEER;
            }
        }
        for (int[] pos : clipperCardPositions) {
            if (!pickedUpItems.contains(positionToString(pos[0], pos[1]))) {
                world[pos[0]][pos[1]] = Tileset.CLIPPER_CARD;
            }
        }

        for (String posStr : pickedUpItems) {
            String[] posArr = posStr.split(",");
            int x = Integer.parseInt(posArr[0]);
            int y = Integer.parseInt(posArr[1]);
            world[x][y] = Tileset.GRASS;
        }
    }
}
