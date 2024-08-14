package core;

import java.io.Serializable;

/**
 * Oski the bear, an interactive NPC with dialogue and status.
 */
public class Oski implements Serializable {

    private static final long serialVersionUID = 1L;

    // Position variables
    private int x, y;

    /**
     * Constructs an Oski object with specified initial position.
     *
     * @param x The x-coordinate of Oski's initial position.
     * @param y The y-coordinate of Oski's initial position.
     */
    public Oski(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of Oski's position.
     *
     * @return The x-coordinate of Oski's position.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of Oski's position.
     *
     * @param x The new x-coordinate of Oski's position.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of Oski's position.
     *
     * @return The y-coordinate of Oski's position.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of Oski's position.
     *
     * @param y The new y-coordinate of Oski's position.
     */
    public void setY(int y) {
        this.y = y;
    }
}
