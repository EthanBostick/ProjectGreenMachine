package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

public class NetworkFlowSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private final int[][] hexDirections = {
        {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}
    };
    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public NetworkFlowSystem(float interval) {
        super(Family.all(Nutrients.class, NutrientDraw.class, NutrientCapacity.class, Position.class).get(), interval);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        try {
            Position position = Mappers.positionCMap.get(entity);
            Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(entity);
            NutrientDraw nutrientDraw = Mappers.nutrientDrawCMap.get(entity);

            int remainingMineralDraw = (nutrientDraw.mineralDraw > 0)
            ? Math.min(nutrientDraw.mineralDraw, nutrientCapacity.mineralCapacity - nutrients.minerals)
            : 0;

            int remainingCarbonDraw = (nutrientDraw.carbonDraw > 0)
            ? Math.min(nutrientDraw.carbonDraw, nutrientCapacity.carbonCapacity - nutrients.carbons)
            : 0;

            for (int i = 0; i < 6; i++) {
                Entity neighbor = Map.instance().getEntityAt(
                    position.q + hexDirections[i][0], 
                    position.r + hexDirections[i][1], 
                    TilePosition.MYCELIUM
                );
                if (neighbor == null) continue;

                Nutrients neighborNutrients = Mappers.nutrientsCMap.get(neighbor);
                NutrientCapacity neighborCapacity = Mappers.nutrientCapacityCMap.get(neighbor);

                // Mineral Channel
                if (remainingMineralDraw > 0) {
                    int transfer = Math.min(remainingMineralDraw, neighborNutrients.minerals);
                    neighborNutrients.minerals -= transfer;
                    nutrients.minerals += transfer;
                    remainingMineralDraw -= transfer;
                } else if (nutrientDraw.mineralDraw == 0 
                        && nutrients.minerals - neighborNutrients.minerals > 1 
                        && neighborNutrients.minerals < neighborCapacity.mineralCapacity) {
                    nutrients.minerals--;
                    neighborNutrients.minerals++;
                }

                // Carbon Channel
                if (remainingCarbonDraw > 0) {
                    int transfer = Math.min(remainingCarbonDraw, neighborNutrients.carbons);
                    neighborNutrients.carbons -= transfer;
                    nutrients.carbons += transfer;
                    remainingCarbonDraw -= transfer;
                } else if (nutrientDraw.carbonDraw == 0 
                        && nutrients.carbons - neighborNutrients.carbons > 1 
                        && neighborNutrients.carbons < neighborCapacity.carbonCapacity) {
                    nutrients.carbons--;
                    neighborNutrients.carbons++;
                }
            }
        } catch (Exception e) {
            System.out.println("NetworkFlowError: " + e.getMessage());
        }
    }
}
