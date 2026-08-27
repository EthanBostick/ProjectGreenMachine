package io.github.ethanBostick.utils;

import io.github.ethanBostick.ecs.Position;
import com.badlogic.gdx.utils.IntArray;

public class HexUtils {
    private static double SIZE = 30; //dist between center to each corner (should be equal across each corner) 
    
    //cube coords to pixel coords
    public static int getPixelX(Position p){
        return (int)(SIZE * ((3.0/2.0) *p.q));
    }
    public static int getPixelY(Position p){
        return (int) (SIZE * (Math.sqrt(3.0)*(p.r + (p.q/2.0))));
    }

    public static IntArray getNeighborsPositions(Position p){
        IntArray neighbors = new IntArray(12); 
        neighbors.add(p.q+1);
        neighbors.add(p.r);
        neighbors.add(p.q+1);
        neighbors.add(p.r-1);
        neighbors.add(p.q-1);
        neighbors.add(p.r);
        neighbors.add(p.q);
        neighbors.add(p.r-1);
        neighbors.add(p.q-1);
        neighbors.add(p.r+1);
        neighbors.add(p.q);
        neighbors.add(p.r+1);
        return neighbors;
    }
    public static IntArray getNeighborsPositions(int q, int r){
        IntArray neighbors = new IntArray(12); 
        neighbors.add(q+1);
        neighbors.add(r);
        neighbors.add(q+1);
        neighbors.add(r-1);
        neighbors.add(q-1);
        neighbors.add(r);
        neighbors.add(q);
        neighbors.add(r-1);
        neighbors.add(q-1);
        neighbors.add(r+1);
        neighbors.add(q);
        neighbors.add(r+1);
        return neighbors;
    }
}
