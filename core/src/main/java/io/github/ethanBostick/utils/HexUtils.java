package io.github.ethanBostick.utils;

import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.ecs.Position;

public final class HexUtils {
    private HexUtils() {}

    public static final float HEIGHT = 52f;
    public static final float WIDTH = 64f;
    public static final float SIZE = 30f;
    private static final float SQRT_3 = 1.7320508f;
    private static final float X_SCALE = 1.5f * SIZE;
    private static final float Y_SCALE = SQRT_3 * SIZE;

    public static int getPixelX(Position p) {
        return Math.round(X_SCALE * p.q);
    }

    public static int getPixelY(Position p) {
        return Math.round(Y_SCALE * (p.r + 0.5f * p.q));
    }

    // translate a pixelCoord to Axial, used for mouse clicks
    public static void getAxialFromPixel(Position p) {
        // 1. Calculate fractional axial coordinates
        float fracQ = (p.x - (WIDTH/2f)) / X_SCALE;
        float fracR = ((p.y - (HEIGHT/2f)) / Y_SCALE) - (0.5f * fracQ);
        
        // 2. Derive the missing third cube coordinate (s)
        float fracS = -fracQ - fracR;
        
        // 3. Round all three independently
        int q = Math.round(fracQ);
        int r = Math.round(fracR);
        int s = Math.round(fracS);
        
        // 4. Calculate the absolute rounding differences
        float qDiff = Math.abs(q - fracQ);
        float rDiff = Math.abs(r - fracR);
        float sDiff = Math.abs(s - fracS);
        
        // 5. Adjust the coordinate with the largest error to force q + r + s = 0
        if (qDiff > rDiff && qDiff > sDiff) {
            q = -r - s;
        } else if (rDiff > sDiff) {
            r = -q - s;
        }
        
        p.q = q;
        p.r = r;
    }

    public static IntArray getAreaAround(Position p,int size, IntArray output) {
        int centerQ = p.q;
        int centerR = p.r;
        output.clear();

		for (int q = -size ; q < size ; q ++){
			for (int r = Math.max(-size, -q - size); r < Math.min(size, -q + size); r ++){
                output.add(q+centerQ);
                output.add(r+centerR);
            }
        }

        return output;
    }
    public static IntArray getAreaAround(int centerQ, int centerR, int size, IntArray output) {
        output.clear();

		for (int q = -size ; q <= size ; q ++){
			for (int r = Math.max(-size, -q - size); r <= Math.min(size, -q + size); r ++){
                output.add(q+centerQ);
                output.add(r+centerR);
            }
        }

        return output;
    }

    public static IntArray getNeighborsPositions(Position p, IntArray output) {
        int q = p.q;
        int r = p.r;
        output.clear();

        output.add(q + 1);
        output.add(r);

        output.add(q + 1);
        output.add(r - 1);

        output.add(q - 1);
        output.add(r);

        output.add(q);
        output.add(r - 1);

        output.add(q - 1);
        output.add(r + 1);

        output.add(q);
        output.add(r + 1);

        return output;
    }
    public static IntArray getNeighborsPositions(int q, int r, IntArray output) {
        output.clear();

        output.add(q + 1);
        output.add(r);

        output.add(q + 1);
        output.add(r - 1);

        output.add(q - 1);
        output.add(r);

        output.add(q);
        output.add(r - 1);

        output.add(q - 1);
        output.add(r + 1);

        output.add(q);
        output.add(r + 1);

        return output;
    }
}

