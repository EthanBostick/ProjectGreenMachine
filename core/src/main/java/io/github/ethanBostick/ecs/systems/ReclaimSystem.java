package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Reclaim;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class ReclaimSystem extends IntervalIteratingSystem {
    private int[][] hexDirections;
    private final float interval;
    
    public ReclaimSystem(float interval, int priority) {
        super(Family.all(Reclaim.class).get(),interval, priority);
        this.hexDirections = HexUtils.hexDirections;
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        try{
            Nutrients nutrientsHeld = Mappers.nutrientsCMap.get(entity);
            Position lastPosition = Mappers.positionCMap.get(entity);
            if(nutrientsHeld == null || lastPosition == null) throw new Exception();

            int totalCarbonReclaim = nutrientsHeld.carbons + nutrientsHeld.carbonReclaim;
            int totalMineralReclaim = nutrientsHeld.minerals + nutrientsHeld.mineralReclaim;

            //check if being replaced
            Entity replacementNode = Map.instance().getEntityAt(lastPosition.q, lastPosition.r, TilePosition.MYCELIUM);
            if(replacementNode != null){
                Nutrients repNutrients = Mappers.nutrientsCMap.get(replacementNode);
                NutrientCapacity repCap = Mappers.nutrientCapacityCMap.get(replacementNode);

                if (repNutrients != null && repCap != null) {
                    int cSpace = Math.max(0, repCap.carbonCapacity - repNutrients.carbons);
                    int mSpace = Math.max(0, repCap.mineralCapacity - repNutrients.minerals);

                    int cTaken = Math.min(totalCarbonReclaim, cSpace);
                    int mTaken = Math.min(totalMineralReclaim, mSpace);

                    repNutrients.carbonDelta += cTaken;
                    repNutrients.mineralDelta += mTaken;

                    totalCarbonReclaim -= cTaken;
                    totalMineralReclaim -= mTaken;
                }
            }

            //distribute to neighbors
            if (totalCarbonReclaim > 0 || totalMineralReclaim > 0) {
            // Collect valid neighbors
                for (int[] dir : hexDirections) {
                    if (totalCarbonReclaim <= 0 && totalMineralReclaim <= 0) break;

                    Entity neighbor = Map.instance().getEntityAt(lastPosition.q + dir[0], lastPosition.r + dir[1], TilePosition.MYCELIUM);
                    if (neighbor == null || neighbor == entity) continue;

                    Nutrients nNutrients = Mappers.nutrientsCMap.get(neighbor);
                    NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(neighbor);

                    if (nNutrients == null || nCap == null) continue;

                    // Check projected headroom accounting for pending deltas
                    int cSpace = Math.max(0, nCap.carbonCapacity - (nNutrients.carbons + nNutrients.carbonDelta));
                    int mSpace = Math.max(0, nCap.mineralCapacity - (nNutrients.minerals + nNutrients.mineralDelta));

                    int cDeposit = Math.min(totalCarbonReclaim, cSpace);
                    int mDeposit = Math.min(totalMineralReclaim, mSpace);

                    nNutrients.carbonDelta += cDeposit;
                    nNutrients.mineralDelta += mDeposit;

                    totalCarbonReclaim -= cDeposit;
                    totalMineralReclaim -= mDeposit;
                }
            }

            //overflow
            if (totalCarbonReclaim > 0 || totalMineralReclaim > 0) {
                // Option A: Drop on ground as an interactable entity
                // Option B: Hard loss to simulate biomass degradation during severing
            }
        }
        catch(Exception e){
            System.out.println("ReclaimError: " + e.getMessage());
        }
        finally{
            EntityBuilder.instance().dead(entity);
        }
    }
}
