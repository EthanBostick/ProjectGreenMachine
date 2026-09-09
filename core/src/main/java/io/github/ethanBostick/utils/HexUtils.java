package io.github.ethanBostick.utils;

import io.github.ethanBostick.ecs.Position;
import com.badlogic.gdx.utils.IntArray;

public final class HexUtils {
    private HexUtils() {}

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

