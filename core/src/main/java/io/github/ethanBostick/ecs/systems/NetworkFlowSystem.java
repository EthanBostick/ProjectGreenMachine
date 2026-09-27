package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class NetworkFlowSystem extends IntervalIteratingSystem {
    private final int[][] hexDirections;

    public NetworkFlowSystem(float interval, int priority) {
        super(Family.all(Position.class, Nutrients.class, NutrientCapacity.class, NutrientFlow.class).get(), interval, priority);
        this.hexDirections = HexUtils.hexDirections;
    }

    @Override
    protected void processEntity(Entity entity) {
        try {
            Position pos = Mappers.positionCMap.get(entity);
            Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
            NutrientFlow flow = Mappers.nutrientFlowCMap.get(entity);

            // Channel 1: Minerals
            if (nutrients.minerals > 0 && flow.mineralDistance > 0) {
                routeMinerals(pos, nutrients, flow);
            }

            // Channel 2: Carbon
            if (nutrients.carbons > 0 && flow.carbonDistance > 0) {
                routeCarbons(pos, nutrients, flow);
            }
        } catch (Exception e) {
            System.out.println("NetworkFlowError: " + e.getMessage());
        }
    }

    private void routeMinerals(Position pos, Nutrients nutrients, NutrientFlow flow) {
        Entity bestNeighbor = null;
        int lowestDistance = flow.mineralDistance;
        int availableTransfer = Math.min(flow.maxThroughput, nutrients.minerals);

        for (int[] dir : hexDirections) {
            Entity neighbor = Map.instance().getEntityAt(pos.q + dir[0], pos.r + dir[1], TilePosition.MYCELIUM);
            if (neighbor == null) continue;

            NutrientFlow nFlow = Mappers.nutrientFlowCMap.get(neighbor);
            Nutrients nNutrients = Mappers.nutrientsCMap.get(neighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(neighbor);

            if (nFlow == null || nNutrients == null || nCap == null) continue;

            // Pick the neighbor strictly closer to the sink with storage headroom
            if (nFlow.mineralDistance < lowestDistance && nNutrients.minerals < nCap.mineralCapacity) {
                lowestDistance = nFlow.mineralDistance;
                bestNeighbor = neighbor;
            }
        }

        if (bestNeighbor != null) {
            Nutrients nNutrients = Mappers.nutrientsCMap.get(bestNeighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(bestNeighbor);

            int spaceLeft = nCap.mineralCapacity - nNutrients.minerals;
            int transferAmount = Math.min(availableTransfer, spaceLeft);

            nutrients.minerals -= transferAmount;
            nNutrients.minerals += transferAmount;
        }
    }

    private void routeCarbons(Position pos, Nutrients nutrients, NutrientFlow flow) {
        Entity bestNeighbor = null;
        int lowestDistance = flow.carbonDistance;
        int availableTransfer = Math.min(flow.maxThroughput, nutrients.carbons);

        for (int[] dir : hexDirections) {
            Entity neighbor = Map.instance().getEntityAt(pos.q + dir[0], pos.r + dir[1], TilePosition.MYCELIUM);
            if (neighbor == null) continue;

            NutrientFlow nFlow = Mappers.nutrientFlowCMap.get(neighbor);
            Nutrients nNutrients = Mappers.nutrientsCMap.get(neighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(neighbor);

            if (nFlow == null || nNutrients == null || nCap == null) continue;

            if (nFlow.carbonDistance < lowestDistance && nNutrients.carbons < nCap.carbonCapacity) {
                lowestDistance = nFlow.carbonDistance;
                bestNeighbor = neighbor;
            }
        }

        if (bestNeighbor != null) {
            Nutrients nNutrients = Mappers.nutrientsCMap.get(bestNeighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(bestNeighbor);

            int spaceLeft = nCap.carbonCapacity - nNutrients.carbons;
            int transferAmount = Math.min(availableTransfer, spaceLeft);

            nutrients.carbons -= transferAmount;
            nNutrients.carbons += transferAmount;
        }
    }
}