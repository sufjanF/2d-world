package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.io.Serializable;

/**
 * Implements the player controlled avatar initialized at the start of the game.
 */
public class Avatar implements Serializable {
    private static final long serialVersionUID = 1L;
    // player position
    private int x, y;
    private World world;

    /**
     * Constructs an Avatar object with specified position and associated world.
     *
     * @param x     The initial x-coordinate of the avatar.
     * @param y     The initial y-coordinate of the avatar.
     * @param world The world instance associated with the avatar.
     */
    public Avatar(int x, int y, World world) {
        this.x = x;
        this.y = y;
        this.world = world;
    }

    /**
     * Returns the x-coordinate of the avatar.
     *
     * @return The x-coordinate of the avatar.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the avatar.
     *
     * @param x The new x-coordinate of the avatar.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of the avatar.
     *
     * @return The y-coordinate of the avatar.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the avatar.
     *
     * @param y The new y-coordinate of the avatar.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Moves the avatar based on the given input character.
     * The movement is controlled using WASD keys for directional movement and 'e' for interaction.
     *
     * @param input The character input for movement or interaction.
     * @param tileMap The tile map representing the game world.
     */
    public void move(char input, TETile[][] tileMap) {
        int width = tileMap.length;
        int height = tileMap[0].length;
        switch (input) {
            case 'w':
                if (y + 1 < height && tileMap[x][y + 1] == Tileset.GRASS) {
                    y += 1; // Move up
                }
                break;
            case 's':
                if (y - 1 >= 0 && tileMap[x][y - 1] == Tileset.GRASS) {
                    y -= 1; // Move down
                }
                break;
            case 'a':
                if (x - 1 >= 0 && tileMap[x - 1][y] == Tileset.GRASS) {
                    x -= 1; // Move left
                }
                break;
            case 'd':
                if (x + 1 < width && tileMap[x + 1][y] == Tileset.GRASS) {
                    x += 1; // Move right
                }
                break;
            case 'e':
                this.world.pickUpItem(tileMap);
                this.world.interactWithOski(tileMap); // Add this line for dialogue interaction
                return;
            default:
                System.out.println("Invalid input");
                break;
        }
    }
}
