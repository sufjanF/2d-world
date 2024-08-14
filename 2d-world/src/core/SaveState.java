package core;

import utils.FileUtils;
import java.io.*;
import java.util.Base64;

/* Allows a game state to be saved and reloaded from the main menu. */
public class SaveState {

    private static final String SAVE_FILE = "save-file.txt";

    /**
     * Makes save file from current game state.
     *
     * @param world the current world object we want to save.
     */
    public static void saveGame(World world) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteStream)) {
            out.writeObject(world);
            out.flush();
            String gameState = Base64.getEncoder().encodeToString(byteStream.toByteArray());
            FileUtils.writeFile(SAVE_FILE, gameState);
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the game from our save file.
     *
     * @return loaded game file or error.
     */
    public static World loadGame() {
        if (FileUtils.fileExists(SAVE_FILE)) {
            try {
                String gameState = FileUtils.readFile(SAVE_FILE);
                byte[] data = Base64.getDecoder().decode(gameState);
                try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
                     ObjectInputStream in = new ObjectInputStream(byteStream)) {
                    World world = (World) in.readObject();
                    System.out.println("Game loaded successfully.");
                    return world;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
