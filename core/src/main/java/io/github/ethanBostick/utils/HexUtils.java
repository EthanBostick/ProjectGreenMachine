package io.github.ethanBostick.utils;

import io.github.ethanBostick.ecs.Position;
import java.util.ArrayList;

public class HexUtils {
    private static double SIZE = 30.0; //adjust this later, dist between center to each corner 
    
    //cube coords to pixel coords
    public static int getPixelX(Position p){
        return (int)(SIZE * ((3.0/2.0) *p.x));
    }
    public static int getPixelY(Position p){
        return (int) (SIZE * (Math.sqrt(3.0)*(p.z + (p.x/2.0))));
    }

    public static ArrayList<int[]> getNeighborsPositions(Position p){
        ArrayList<int[]> neighbors = new ArrayList<int[]>(); 
        neighbors.add(new int[] {p.x+1,p.y-1,p.z});
        neighbors.add(new int[] {p.x+1,p.y,p.z-1});
        neighbors.add(new int[] {p.x-1,p.y+1,p.z});
        neighbors.add(new int[] {p.x,p.y+1,p.z-1});
        neighbors.add(new int[] {p.x-1,p.y,p.z+1});
        neighbors.add(new int[] {p.x,p.y-1,p.z+1});
        return neighbors;
    }
}
