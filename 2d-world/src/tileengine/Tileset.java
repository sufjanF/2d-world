package tileengine;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */


import java.awt.Color;

public class Tileset {

    public static final TETile WALL = new TETile('▓', new Color(60, 30, 15), new Color(0, 100, 0), "wall",1 ); // Darker Brown and Dark Plant Green

    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black, "floor", 2);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 3);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11);

    public static final TETile CELL = new TETile('█', Color.white, Color.black, "cell", 12);

    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "avatar", "photos/avatar_optimized.png", 13);
    public static final TETile OSKI = new TETile('O', Color.white, Color.black, "Oski", "photos/oski_optimized.png", 14);

    public static final TETile BEER = new TETile('B', Color.yellow, Color.black, "beer", "photos/beer.png", 15);
    public static final TETile CLIPPER_CARD = new TETile('C', Color.blue, Color.black, "card", "photos/card.png", 16);

    public static final TETile AVATAR_D = new TETile('@', Color.white, Color.black, "avatar", "photos/avatar_D.png", 17);
    public static final TETile OSKI_D = new TETile('@', Color.white, Color.black, "avatar", "photos/oski_D.png", 18);


}



