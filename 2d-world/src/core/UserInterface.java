package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Note: Entire file checked for coding errors and clarity with the use of an LLM.
 * The UserInterface class handles the graphical user interface for the game, including
 * displaying the main menu and initiating game actions.
 */
public class UserInterface implements Serializable {
    private String tileInfo;  // Add this line to store tile info
    private static final long serialVersionUID = 1L; // ID for serialization
    private ArrayList<TETile> playerInventory;
    private transient World world;  // Reference to the world instance
    private Map<String, String> dialogueOptions;
    private Map<String, String> oskiResponses;
    private Map<String, String> secondDialogueOptions;
    private Map<String, String> secondOskiResponses;
    private boolean oskiLimit;

    // Constructor to initialize UserInterface with the world instance
    public UserInterface(World world) {
        this.world = world;
        this.tileInfo = "";
        this.playerInventory = new ArrayList<>();
        this.dialogueOptions = new HashMap<>();
        this.oskiResponses = new HashMap<>();
        this.secondDialogueOptions = new HashMap<>();
        this.secondOskiResponses = new HashMap<>();
        oskiLimit = false;
        fillDialogueTree();
    }

    /**
     * Returns a copy of the player's inventory.
     *
     * @return A copy of the player's inventory.
     */
    public ArrayList<TETile> getPlayerInventory() {
        return new ArrayList<>(playerInventory);
    }

    /**
     * Sets the player's inventory to the given list.
     *
     * @param inventory The list of items to set as the player's inventory.
     */
    public void setPlayerInventory(ArrayList<TETile> inventory) {
        this.playerInventory = new ArrayList<>(inventory);
    }


    /**
     * Displays the main menu with options to start a new game, load a game,
     * or quit the game.
     * The title "Oski's Intervention" is displayed with bear and beer emojis.
     */
    public void setMainMenu() {
        setStartScreen();
        Font titleFont = new Font("Papyrus", Font.BOLD, 60);
        StdDraw.setFont(titleFont);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.text(400, 375, "🐻 Oski's Intervention 🍺");

        // Set font for menu options
        Font optionsFont = new Font("Papyrus", Font.PLAIN, 23);
        StdDraw.setFont(optionsFont);
        StdDraw.setPenColor(Color.WHITE); // Reset pen color for options
        StdDraw.text(400, 275, "New Game (N)");
        StdDraw.text(400, 225, "Load Game (L)");
        StdDraw.text(400, 175, "Quit (Q)");

        boolean menuStatus = true;

        while (menuStatus) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (handleKeyPress(key)) {
                    menuStatus = false;
                }
            }

            if (StdDraw.isMousePressed()) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                if (handleMouseClick(mouseX, mouseY)) {
                    menuStatus = false;
                }
            }
            StdDraw.show();
        }
    }

    /**
     * Fills the dialogue tree with options and responses for Oski.
     */
    private void fillDialogueTree() {
        dialogueOptions.put("1", "Oski, you look terrible! Are you okay? (1)");
        dialogueOptions.put("2", "Goodbye! (2)");

        oskiResponses.put("1", "I'm dying... please get me more beer...");
        oskiResponses.put("2", "Goodbye...");

        secondDialogueOptions.put("1", "No Oski, you need help. Let's get you treatment. (1)");
        secondDialogueOptions.put("2", "No way! No more beer for you! Stupid bear. (2)");

        secondOskiResponses.put("1", "Thank you, I knew I could count on you...");
        secondOskiResponses.put("2", "Grrrrrr...");
    }

    /**
     * Draws the background for the dialogue box.
     */
    private static void drawDialogueBackground() {
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(World.WIDTH / 2, World.HEIGHT / 2, 20, 10); // Adjust size as needed
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.rectangle(World.WIDTH / 2, World.HEIGHT / 2, 20, 10);
        Tileset.AVATAR_D.draw((World.WIDTH / 2) - 15, World.HEIGHT / 2);
        Tileset.OSKI_D.draw((World.WIDTH / 2) + 15, World.HEIGHT / 2);
    }

    /**
     * Displays an option to give Oski the clipper card if it is in the player's inventory.
     */
    private void oskiHasCard() {
        if (playerInventory.contains(Tileset.CLIPPER_CARD)) {
            StdDraw.text(World.WIDTH / 2, (World.HEIGHT / 2) - 4, "*Give Oski the clipper card* (3)");
        }
    }

    /**
     * Shows the current frame and pauses for a short duration.
     */
    private void showThenPause() {
        StdDraw.show();
        StdDraw.pause(2500);
    }

    /**
     * Displays a message indicating Oski is happy.
     */
    private void oskiHappy() {
        StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 20));
        StdDraw.text(World.WIDTH / 2, (World.HEIGHT / 2) + 2, "Thank you! Off to newer beginnings!");
    }

    /**
     * Displays a sequence showing Oski's happiness and ends the game.
     */
    private void oskiHappySequence() {
        drawDialogueBackground();
        oskiHappy();
        showThenPause();
        renderEndScreen(false);
    }

    /**
     * Displays the dialogue box for interacting with Oski.
     */
    public void showDialogueBox() {
        int centerX = World.WIDTH / 2;
        int centerY = World.HEIGHT / 2;
        boolean inDialogue = true;
        boolean firstDialogue = true;
        while (inDialogue) {
            drawDialogueBackground();
            StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 24));
            StdDraw.text(centerX, centerY + 8, "Talk to Oski the Bear:");
            StdDraw.setFont(new Font("Futura", Font.PLAIN, 16));
            if (firstDialogue) {
                StdDraw.text(centerX, centerY + 2, dialogueOptions.get("1"));
                StdDraw.text(centerX, centerY - 1, dialogueOptions.get("2"));
            } else {
                StdDraw.text(centerX, centerY + 2, secondDialogueOptions.get("1"));
                StdDraw.text(centerX, centerY - 1, secondDialogueOptions.get("2"));
            }
            oskiHasCard();
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                boolean hasKey = dialogueOptions.containsKey(Character.toString(key));
                boolean secondKey = secondDialogueOptions.containsKey(Character.toString(key));
                boolean hasCard = playerInventory.contains(Tileset.CLIPPER_CARD);
                if (firstDialogue && hasKey || (key == '3' && playerInventory.contains(Tileset.CLIPPER_CARD))) {
                    if (key == '3') {
                        oskiHappySequence();
                        inDialogue = false;
                    } else {
                        String oskiResponse = oskiResponses.get(Character.toString(key));
                        drawDialogueBackground();
                        StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 20));
                        StdDraw.text(centerX, centerY + 2, oskiResponse);
                        showThenPause();
                        if (key == '2') {
                            inDialogue = false;  // Exit dialogue on "Goodbye"
                        } else {
                            firstDialogue = false; // Move to the second dialogue tree
                        }
                    }
                } else if (!firstDialogue && secondKey || (key == '3' && hasCard)) {
                    if (key == '3') {
                        oskiHappySequence();
                        inDialogue = false;
                    } else {
                        String oskiResponse = secondOskiResponses.get(Character.toString(key));
                        drawDialogueBackground();
                        StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 20));
                        StdDraw.text(centerX, centerY + 2, oskiResponse);
                        showThenPause();
                        if (key == '1') {
                            StdDraw.text(centerX, centerY, "Find me a BART card so I can get treatment.");
                            showThenPause();
                            inDialogue = false;  // Exit dialogue after agreeing to help
                        } else if (key == '2') {
                            if (!oskiLimit) {
                                StdDraw.text(centerX, centerY, "Go get me some beer... or else...");
                                showThenPause();
                                oskiLimit = true;
                            } else {
                                StdDraw.text(centerX, centerY, "I warned you... time for lunch!");
                                showThenPause();
                                drawDialogueBackground();
                                StdDraw.setFont(new Font("Futura", Font.PLAIN, 16));
                                StdDraw.text(centerX, centerY + 2, "No Oski, don't do it!!! Stop!!!");
                                showThenPause();
                                StdDraw.text(centerX, centerY, "AAAAAAAAAAAAHHHHH!!!!!");
                                showThenPause();
                                renderEndScreen(true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles key presses for the main menu options.
     *
     * @param key The key pressed.
     * @return True if an action was taken that should exit the menu loop, false otherwise.
     */
    private boolean handleKeyPress(char key) {
        if (key == 'Q' || key == 'q') {
            System.exit(0);
        }
        if (key == 'N' || key == 'n') {
            StdDraw.clear(StdDraw.BLACK);
            world.newGame();
            return true;
        } else if (key == 'L' || key == 'l') {
            world.loadGame();
            return true;
        }
        return false;
    }

    /**
     * Handles mouse clicks for the main menu options.
     *
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @return True if an action was taken that should exit the menu loop, false otherwise.
     */
    private boolean handleMouseClick(double mouseX, double mouseY) {
        if (isWithinBounds(mouseX, mouseY, 250, 550, 255, 295)) { // New Game
            StdDraw.clear(StdDraw.BLACK);
            world.newGame();
            return true;
        } else if (isWithinBounds(mouseX, mouseY, 250, 550, 205, 245)) { // Load Game
            world.loadGame();
            return true;
        } else if (isWithinBounds(mouseX, mouseY, 250, 550, 155, 195)) { // Quit
            System.exit(0);
        }
        return false;
    }

    /**
     * Renders the HUD overlay, showing the title and task.
     * Displays tile information and updates the HUD elements.
     */
    public void renderHUD() {
        StdDraw.setPenColor(Color.WHITE);
        Font hudFont = new Font("Futura", Font.PLAIN, 17);
        StdDraw.setFont(hudFont);

        StdDraw.setPenColor(Color.ORANGE);
        int startX = 1;
        StdDraw.setFont(new Font("Papyrus", Font.BOLD, 22));
        int thisY = World.HEIGHT + World.HUD_HEIGHT - 1;
        StdDraw.textLeft(startX, thisY, "\uD83D\uDC3B Oski's Intervention \uD83C\uDF7A\n");

        StdDraw.setPenColor(new Color(135, 206, 250));
        int centerX = (World.WIDTH / 2);
        StdDraw.setFont(new Font("Futura", Font.PLAIN, 20));
        StdDraw.text(centerX, World.HEIGHT + World.HUD_HEIGHT - 1.5, "Save Oski from Himself!");

        StdDraw.setFont(hudFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textRight(World.WIDTH - 1, World.HEIGHT + World.HUD_HEIGHT - 1, "Tile: " + tileInfo);
    }

    /**
     * Renders the end screen of the game.
     * Displays a message based on whether the player was eaten by Oski or won the game.
     * @param eaten Indicates if the player was eaten by Oski.
     */
    public void renderEndScreen(Boolean eaten) {
        StdDraw.clear(Color.BLACK); // Clear the screen
        StdDraw.setPenColor(Color.WHITE); // Set pen color to white
        int centerX = World.WIDTH / 2;
        int centerY = (World.HEIGHT / 2) + 6;

        // Draw the end screen message
        StdDraw.setFont(new Font("Papyrus", Font.BOLD, 40));
        TETile oskiTile = Tileset.OSKI_D;
        oskiTile.draw(centerX, centerY - 2);

        if (eaten) {
            StdDraw.text(centerX, centerY + 7, "You were eaten by Oski");
            StdDraw.text(centerX, centerY - 8, "GAME OVER");
        } else {
            StdDraw.text(centerX, centerY + 7, "You won! Oski is saved");
            StdDraw.text(centerX, centerY - 8, "Thank you for playing Oski's Intervention!");
        }
        // Show the final frame
        StdDraw.show();
        StdDraw.pause(6000);
        System.exit(0);
    }

    /**
     * Sets up the start screen with a canvas size and scales for the X and Y axes.
     * Draws a gradient background.
     */
    private void setStartScreen() {
        StdDraw.setCanvasSize(800, 600);
        StdDraw.setXscale(0, 800);
        StdDraw.setYscale(0, 600);
        StdDraw.clear(Color.BLACK);
        // Draw gradient background
        drawGradientBackground();
    }

    /**
     * Implemented by an LLM.
     * Draws a gradient background from light blue at the top to midnight blue at the bottom.
     */
    private void drawGradientBackground() {
        // Define the colors for the gradient
        Color topColor = new Color(135, 206, 250); // Light Blue
        Color bottomColor = new Color(25, 25, 112); // Midnight Blue

        for (int y = 0; y < 600; y++) {
            double blend = (double) y / 600;
            Color blendedColor = blendColors(topColor, bottomColor, blend);
            StdDraw.setPenColor(blendedColor);
            StdDraw.line(0, y, 800, y);
        }
    }

    /**
     * Implemented by an LLM.
     * Blends two colors based on a given ratio.
     * @param c1    The first color.
     * @param c2    The second color.
     * @param ratio The ratio for blending the two colors.
     * @return The blended color.
     */
    private Color blendColors(Color c1, Color c2, double ratio) {
        int red = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int green = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int blue = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(red, green, blue);
    }

    /**
     * Updates the tile information displayed in the HUD.
     *
     * @param info The information about the tile to be displayed.
     */
    public void updateTileInfo(String info) {
        this.tileInfo = info;
    }
    /**
     * Adds an item to the player's inventory.
     *
     * @param item The item to be added to the inventory.
     */
    public void addItem(TETile item) {
        playerInventory.add(item);
    }

    /**
     * Returns a copy of the player's inventory.
     *
     * @return A copy of the player's inventory.
     */
    public ArrayList<TETile> getInventory() {
        ArrayList<TETile> result = new ArrayList<>();
        for (TETile sTile : playerInventory) {
            result.add(sTile);
        }
        return result;
    }

    /**
     * Implemented by an LLM.
     * Checks if the mouse click is within the specified bounds.
     *
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param xMin   The minimum x bound.
     * @param xMax   The maximum x bound.
     * @param yMin   The minimum y bound.
     * @param yMax   The maximum y bound.
     * @return True if the mouse click is within the bounds, false otherwise.
     */
    private boolean isWithinBounds(double mouseX, double mouseY, double xMin, double xMax, double yMin, double yMax) {
        return mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax;
    }
}
