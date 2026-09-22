package io.github.ethanBostick.map;

import java.util.Random;

public class MapGenerator {
    private Random random = null;
    private final int radius;
    private final int width;
    private int[] grid; //holds temperatures -1 = Blank Tile.
    private final double thresh;

    private final int[][] hexDirections = {
        {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}
    };

    // FastNoiseLite instances for procedural resources
    private FastNoiseLite simplex;
    private FastNoiseLite cellular;

    public MapGenerator(int radius, double thresh, int[] initialNoiseGrid, Random rand, int seed) {
        this.radius = radius;
        this.width = (radius * 2) + 1;
        this.thresh = thresh;
        this.grid = initialNoiseGrid; 
        this.random = rand;
        
        // Initialize noise specifically for resources
        this.simplex = new FastNoiseLite();
        this.simplex.SetSeed(seed);
        this.simplex.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        this.simplex.SetFrequency(0.08f);

        this.cellular = new FastNoiseLite();
        this.cellular.SetSeed(seed + 1); // Offset seed for variation
        this.cellular.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        this.cellular.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
        this.cellular.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
        this.cellular.SetFrequency(0.08f);
    }

    // returns a 2d int map of the two map layers (i = 0 -> temp biome map, i = 1 -> resource map)
    // ex: rtn[which map][index]
    public int[][] generate(int maxSchellingIterations, int maxSmoothingIterations) {
        for (int i = 0; i < maxSchellingIterations; i++) {
            boolean converged = runSchellingPass();
            if (converged) break;
        }
        for (int i = 0; i < maxSmoothingIterations; i++) {
            this.grid = runSmoothingPass();
        }

        int[] resourceGrid = runResourcePass();
        return new int[][] { this.grid, resourceGrid };
    }

    private int[] runResourcePass() {
        int[] resourceGrid = new int[this.grid.length];

        for (int q = -this.radius; q <= this.radius; q++) {
            int r1 = Math.max(-this.radius, -q - this.radius);
            int r2 = Math.min(this.radius, -q + this.radius);
            for (int r = r1; r <= r2; r++) {
                
                int index = getIndex(q, r);
                int biomeTemp = this.grid[index];

                // Ignore empty structural tiles completely
                if (biomeTemp == -1) {
                    resourceGrid[index] = 0;
                    continue; 
                }

                // 1. Simplex (Macro Regions): Maps output from roughly -1 to 1 into 0 to 1
                float macroNoise = (simplex.GetNoise(q, r) + 1f) / 2f; 
                
                // 2. Voronoi (Vein Epicenters): Distance to nearest cell center
                float veinDistance = cellular.GetNoise(q, r); 
                
                // Invert distance so the cell center is a spike (1.0) and edges are 0.0
                float veinIntensity = 1f - Math.min(1f, Math.abs(veinDistance) * 2f); 

                // 3. Combine raw shapes
                float rawConcentration = macroNoise + (veinIntensity * 1.5f);

                // 4. Apply Biome Logics (The Scalar)
                float biomeScalar = getBiomeResourceScalar(biomeTemp); 

                // 5. Calculate Final Integer Concentration (0 to 100 max)
                int finalConcentration = Math.round(rawConcentration * 20f * biomeScalar);
                
                // 6. Prune weak tiles to keep resources strictly as "hotspots"
                if (finalConcentration >= 15) {
                    resourceGrid[index] = Math.min(100, finalConcentration);
                } else {
                    resourceGrid[index] = 0;
                }
            }
        }
        return resourceGrid;
    }

    private float getBiomeResourceScalar(int biomeTemp) {
        if (biomeTemp == BiomeType.FOREST.tempValue()){
            return BiomeType.FOREST.resourceScalar();
        } else if (biomeTemp == BiomeType.DESERT.tempValue()){
            return BiomeType.DESERT.resourceScalar();
        } else if (biomeTemp == BiomeType.GRASS_LAND.tempValue()){
            return BiomeType.GRASS_LAND.resourceScalar();
        } else if (biomeTemp == BiomeType.MOUNTAIN.tempValue()){
            return BiomeType.MOUNTAIN.resourceScalar();
        } else if (biomeTemp == BiomeType.TAIGA.tempValue()){
            return BiomeType.TAIGA.resourceScalar();
        } else if (biomeTemp == BiomeType.BOREAL.tempValue()){
            return BiomeType.BOREAL.resourceScalar();
        } else{
            return 1.0f;
        }
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
                if (random.nextInt(validCount) == 0) {
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