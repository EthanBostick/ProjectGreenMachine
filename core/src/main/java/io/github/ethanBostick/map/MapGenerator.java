package io.github.ethanBostick.map;

import com.badlogic.gdx.math.MathUtils;

public class MapGenerator {
    private final int radius;
    private final int width;
    private int[] grid; //holds temperatures -1 = Blank Tile.
    private final double thresh;

    private final int[][] hexDirections = {
        {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}
    };

    public MapGenerator(int radius, double thresh, int[] initialNoiseGrid) {
        this.radius = radius;
        this.width = (radius * 2) + 1;
        this.thresh = thresh;
        this.grid = initialNoiseGrid; 
    }

    public int[] generate(int maxSchellingIterations, int maxSmoothingIterations) {
        for (int i = 0; i < maxSchellingIterations; i++) {
            boolean converged = runSchellingPass();
            if (converged) break;
        }
        for (int i = 0; i < maxSmoothingIterations; i++) {
            this.grid = runSmoothingPass();
        }

        return this.grid;
    }

    private boolean runSchellingPass() {
        boolean converged = true;
        
        for (int q = -this.radius; q <= this.radius; q++) {
            int r1 = Math.max(-this.radius, -q - this.radius);
            int r2 = Math.min(this.radius, -q + this.radius);
            for (int r = r1; r <= r2; r++) {
                
                int index = getIndex(q, r);
                int selfTemp = this.grid[index];
                
                if (selfTemp == -1) continue; 

                if (getHappiness(q, r, selfTemp) < this.thresh) {
                    converged = false;
                    int blankIndex = getRandomBlankIndex();
                    
                    if (blankIndex != -1) {
                        this.grid[blankIndex] = selfTemp;
                        this.grid[index] = -1;
                    }
                }
            }
        }
        return converged;
    }

    private double getHappiness(int q, int r, int selfTemp) {
        int numNeighbors = 0;
        double similaritySum = 0;

        for (int[] dir : this.hexDirections) {
            int nq = q + dir[0];
            int nr = r + dir[1];
            
            if (Math.abs(nq) > this.radius || Math.abs(nr) > this.radius || Math.abs(nq + nr) > this.radius) continue;

            int neighborTemp = this.grid[getIndex(nq, nr)];
            if (neighborTemp != -1) {
                double diff = Math.abs(selfTemp - neighborTemp);
                similaritySum += Math.max(0.0, 1.0 - (diff / 100.0));
                numNeighbors++;
            }
        }
        return (numNeighbors == 0) ? 0 : similaritySum / numNeighbors;
    }

    private int getRandomBlankIndex() {
        int selected = -1;
        int validCount = 0;

        for (int i = 0; i < this.grid.length; i++) {
            if (this.grid[i] == -1) {
                validCount++;
                if (MathUtils.random(validCount - 1) == 0) {
                    selected = i;
                }
            }
        }
        return selected;
    }

    private int[] runSmoothingPass() {
        int[] writeGrid = new int[this.grid.length];
        int[] validTemps = {15, 35, 50, 65, 75, 100};

        for (int q = -this.radius; q <= this.radius; q++) {
            int r1 = Math.max(-this.radius, -q - this.radius);
            int r2 = Math.min(this.radius, -q + this.radius);
            for (int r = r1; r <= r2; r++) {
                
                int index = getIndex(q, r);
                int selfTemp = this.grid[index];
                
                int activeNeighbors = 0;
                int tempSum = 0;
                int maxNeighbor = 0; //highest neighbor

                for (int[] dir : hexDirections) {
                    int nq = q + dir[0];
                    int nr = r + dir[1];
                    
                    if (Math.abs(nq) > this.radius || Math.abs(nr) > this.radius || Math.abs(nq + nr) > this.radius) continue;

                    int neighborTemp = this.grid[getIndex(nq, nr)];
                    if (neighborTemp != -1) {
                        activeNeighbors++;
                        tempSum += neighborTemp;
                        if (neighborTemp > maxNeighbor) {
                            maxNeighbor = neighborTemp; 
                        }
                    }
                }

                if (selfTemp == -1 && activeNeighbors >= 3) {
                    if (maxNeighbor >= 75) {
                        writeGrid[index] = maxNeighbor;
                    } else {
                        writeGrid[index] = snapToClosestTemp(tempSum / activeNeighbors, validTemps);
                    }
                    
                } 
                else if (selfTemp != -1 && activeNeighbors <= 1) {
                    writeGrid[index] = -1;
                    
                } 
                else if (selfTemp != -1 && activeNeighbors >= 4) {
                    if (selfTemp >= 75 && maxNeighbor >= 75) {
                        writeGrid[index] = selfTemp;
                    } 
                    else {
                        int avgTemp = (selfTemp + tempSum) / (activeNeighbors + 1);
                        writeGrid[index] = snapToClosestTemp(avgTemp, validTemps);
                    }
                    
                } 
                else {
                    writeGrid[index] = selfTemp;
                }
            }
        }
        return writeGrid;
    }

    // Helper method to ensure we only use your specific temperature buckets
    private int snapToClosestTemp(int target, int[] validTemps) {
        int closest = validTemps[0];
        int minDiff = Math.abs(target - closest);
        
        for (int temp : validTemps) {
            int diff = Math.abs(target - temp);
            if (diff < minDiff) {
                minDiff = diff;
                closest = temp;
            }
        }
        return closest;
    }

    private int getIndex(int q, int r) {
        return (q + this.radius) + ((r + this.radius) * width);
    }
}