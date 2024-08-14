package core;
import tileengine.TERenderer;
import tileengine.TETile;

/* 2D World Engine: Oski's Intervention
 * @author Sufjan Fana
 *
 * An engine for generating an explorable, interactive, 2D world in which the user
 * controls an avatar. The world is generated pseudo-randomly through the input seed.
 */

public class Main {
    public static void main(String[] args) {
        World world = new World();
            world.run();
    }
}
