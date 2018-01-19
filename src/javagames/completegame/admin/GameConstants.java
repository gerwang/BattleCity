package javagames.completegame.admin;

import javagames.util.Vector2f;

import java.awt.Color;

public class GameConstants {

    public static final Color APP_BORDER = Color.DARK_GRAY;
    public static final int APP_WIDTH = 1000;
    public static final int APP_HEIGHT = 1000;
    public static final long APP_SLEEP = 5L;
    public static final String APP_TITLE = "Java Battle City";
    public static final float WORLD_WIDTH = 13f;
    public static final float WORLD_HEIGHT = 13f;
    public static final float BORDER_SCALE = 0.95f;
    public static final boolean DISABLE_CURSOR = false;
    public static final boolean MAINTAIN_RATIO = true;

    public static boolean hasLeftWorld(Vector2f topLeft, Vector2f bottomRight) {
        return topLeft.x < 0 || topLeft.y > WORLD_HEIGHT || bottomRight.x > WORLD_WIDTH || bottomRight.y < 0;
    }
}