package game.a2048;

import android.graphics.Color;

/**
 * Created by wb on 2018/1/4.
 */

public class Util {
    public static int getColor(int num) {
        if (num == 0) {
            return Color.argb(120, 0xF1, 0xDB, 0x6C);
        }
        num *= 0xDB;
        return Color.argb(200, 85, (int)(num * 0.95) % 255, (int)(num * 0.98) % 255);
    }
}

