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

    // returns a 2d int map of the two map layers (i = 0 -> temp region map, i = 1 -> resource map)
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
                int regionTemp = this.grid[index];

                // Ignore empty structural tiles completely
                if (regionTemp == -1) {
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

                // 4. Apply Region Logics (The Scalar)
                float regionScalar = getRegionResourceScalar(regionTemp); 

                // 5. Calculate Final Integer Concentration (0 to 100 max)
                int finalConcentration = Math.round(rawConcentration * 20f * regionScalar);
                
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

    private float getRegionResourceScalar(int regionTemp) {
        if (regionTemp == RegionType.BARREN.tempValue()){
            return RegionType.BARREN.resourceScalar();
        } else if (regionTemp == RegionType.POLLUTED.tempValue()){
            return RegionType.POLLUTED.resourceScalar();
        } else if (regionTemp == RegionType.RADIOACTIVE.tempValue()){
            return RegionType.RADIOACTIVE.resourceScalar();
        } else if (regionTemp == RegionType.MOUNTAIN.tempValue()){
            return RegionType.MOUNTAIN.resourceScalar();
        } else if (regionTemp == RegionType.RESTORED.tempValue()){
            return RegionType.RESTORED.resourceScalar();
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
        int[] validTemps = {
            RegionType.BARREN.tempValue(), 
            RegionType.MOUNTAIN.tempValue(),
            RegionType.POLLUTED.tempValue(), 
            RegionType.RADIOACTIVE.tempValue(), 
            RegionType.RESTORED.tempValue()
        };

        for (int q = -this.radius; q <= this.radius; q++) {
            int r1 = Math.max(-this.radius, -q - this.radius);
            int r2 = Math.min(this.radius, -q + this.radius);
            for (int r = r1; r <= r2; r++) {
                
                int index = getIndex(q, r);
                int selfTemp = this.grid[index];
                
                int activeNeighbors = 0;
                
                // Tally array matching the indices of validTemps
                int[] typeCounts = new int[validTemps.length];

                for (int[] dir : hexDirections) {
                    int nq = q + dir[0];
                    int nr = r + dir[1];
                    
                    if (Math.abs(nq) > this.radius || Math.abs(nr) > this.radius || Math.abs(nq + nr) > this.radius) continue;

                    int neighborTemp = this.grid[getIndex(nq, nr)];
                    if (neighborTemp != -1) {
                        activeNeighbors++;
                        // Tally the specific neighbor type
                        for (int i = 0; i < validTemps.length; i++) {
                            if (neighborTemp == validTemps[i]) {
                                typeCounts[i]++;
                                break;
                            }
                        }
                    }
                }

                // Find the most frequent neighbor type
                int highestFrequency = 0;
                int mostCommonTemp = selfTemp;
                for (int i = 0; i < validTemps.length; i++) {
                    if (typeCounts[i] > highestFrequency) {
                        highestFrequency = typeCounts[i];
                        mostCommonTemp = validTemps[i];
                    }
                }

                // Apply categorical smoothing rules
                if (selfTemp == -1) {
                    // Fill in empty structural gaps if heavily surrounded
                    if (activeNeighbors >= 3) {
                        writeGrid[index] = mostCommonTemp;
                    } else {
                        writeGrid[index] = -1;
                    }
                } 
                else {
                    // Smooth existing tiles
                    if (activeNeighbors <= 1) {
                        // Prune orphaned/isolated tiles
                        writeGrid[index] = -1;
                    } 
                    else if (activeNeighbors >= 4 && highestFrequency >= 3) {
                        // If surrounded by a strong majority of a specific condition, assimilate to it
                        writeGrid[index] = mostCommonTemp;
                    } 
                    else {
                        // Otherwise, hold your ground
                        writeGrid[index] = selfTemp;
                    }
                }
            }
        }
        return writeGrid;
    }

    private int getIndex(int q, int r) {
        return (q + this.radius) + ((r + this.radius) * width);
    }
}