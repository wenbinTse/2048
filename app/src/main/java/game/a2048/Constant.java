package game.a2048;

import android.graphics.Color;

/**
 * Created by Jeany on 2018/1/3.
 */
public class Constant {
    public final static int ROWS = 4;
    public final static int COLUMNS = 4;
    public final static String MAX_SCORE = "maxScore";
    public final static int MIN_MOVE = 120;
    public final static int MIN_VELOCITY = 0;
    public enum Direction {
        up,
        right,
        down,
        left
    }
    public static int Directions[][] = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
}
