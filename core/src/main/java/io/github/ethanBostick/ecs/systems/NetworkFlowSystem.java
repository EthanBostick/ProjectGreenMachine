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
        // Available to push out = base nutrients plus negative deltas (outgoing), ignoring incoming positive deltas
        int transferable = nutrients.minerals + Math.min(0, nutrients.mineralDelta);
        int availableTransfer = Math.min(flow.maxThroughput, Math.max(0, transferable));

        for (int[] dir : hexDirections) {
            Entity neighbor = Map.instance().getEntityAt(pos.q + dir[0], pos.r + dir[1], TilePosition.MYCELIUM);
            if (neighbor == null) continue;

            NutrientFlow nFlow = Mappers.nutrientFlowCMap.get(neighbor);
            Nutrients nNutrients = Mappers.nutrientsCMap.get(neighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(neighbor);

            if (nFlow == null || nNutrients == null || nCap == null) continue;

            // Pick the neighbor strictly closer to the sink with storage headroom
            if (nFlow.mineralDistance < lowestDistance && nNutrients.minerals + nNutrients.mineralDelta < nCap.mineralCapacity) {
                lowestDistance = nFlow.mineralDistance;
                bestNeighbor = neighbor;
            }
        }

        if (bestNeighbor != null) {
            Nutrients nNutrients = Mappers.nutrientsCMap.get(bestNeighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(bestNeighbor);

            int projectedMinerals = nNutrients.minerals + nNutrients.mineralDelta;
            int trueSpaceLeft = Math.max(0, nCap.mineralCapacity - projectedMinerals);
            int transferAmount = Math.min(availableTransfer, trueSpaceLeft);

            nutrients.mineralDelta -= transferAmount;
            nNutrients.mineralDelta += transferAmount;
        }
    }

    private void routeCarbons(Position pos, Nutrients nutrients, NutrientFlow flow) {
        Entity bestNeighbor = null;
        int lowestDistance = flow.carbonDistance;
        // Available to push out = base nutrients plus negative deltas (outgoing), ignoring incoming positive deltas
        int transferable = nutrients.carbons + Math.min(0, nutrients.carbonDelta);
        int availableTransfer = Math.min(flow.maxThroughput, Math.max(0, transferable));

        for (int[] dir : hexDirections) {
            Entity neighbor = Map.instance().getEntityAt(pos.q + dir[0], pos.r + dir[1], TilePosition.MYCELIUM);
            if (neighbor == null) continue;

            NutrientFlow nFlow = Mappers.nutrientFlowCMap.get(neighbor);
            Nutrients nNutrients = Mappers.nutrientsCMap.get(neighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(neighbor);

            if (nFlow == null || nNutrients == null || nCap == null) continue;

            if (nFlow.carbonDistance < lowestDistance && nNutrients.carbons+nNutrients.carbonDelta < nCap.carbonCapacity) {
                lowestDistance = nFlow.carbonDistance;
                bestNeighbor = neighbor;
            }
        }

        if (bestNeighbor != null) {
            Nutrients nNutrients = Mappers.nutrientsCMap.get(bestNeighbor);
            NutrientCapacity nCap = Mappers.nutrientCapacityCMap.get(bestNeighbor);

            int projectedcarbons = nNutrients.carbons + nNutrients.carbonDelta;
            int trueSpaceLeft = Math.max(0, nCap.carbonCapacity - projectedcarbons);
            int transferAmount = Math.min(availableTransfer, trueSpaceLeft);

            nutrients.carbonDelta -= transferAmount;
            nNutrients.carbonDelta += transferAmount;
        }
    }
}