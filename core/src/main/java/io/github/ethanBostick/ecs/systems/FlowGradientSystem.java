package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class FlowGradientSystem extends IntervalSystem {
    private final IntArray queue = new IntArray(false, 128);

    private final int[][] hexDirections;

    public FlowGradientSystem(float interval, int priority) {
        super(interval, priority);
        this.hexDirections = HexUtils.hexDirections;
    }

    @Override
    protected void updateInterval() {
        ImmutableArray<Entity> nodes = getEngine().getEntitiesFor(
            Family.all(NutrientFlow.class, Position.class)
            .exclude(Dead.class).get()
        );

        computeMineralGradients(nodes);
        computeCarbonGradients(nodes);
    }

    private void computeMineralGradients(ImmutableArray<Entity> nodes) {
        queue.clear();

        // 1. Seed the BFS
        for (int i = 0; i < nodes.size(); i++) {
            Entity e = nodes.get(i);
            NutrientFlow flow = Mappers.nutrientFlowCMap.get(e);
            Nutrients nutrients = Mappers.nutrientsCMap.get(e);
            NutrientCapacity cap = Mappers.nutrientCapacityCMap.get(e);
            Position pos = Mappers.positionCMap.get(e);

            flow.mineralDistance = (flow.mineralRoleOverride != null)? flow.mineralRoleOverride.value : flow.mineralRole.value;
            int projectedMinerals = (nutrients != null) ? nutrients.minerals + nutrients.mineralDelta : 0;
            if (flow.mineralDistance < Integer.MAX_VALUE && nutrients != null && cap != null && projectedMinerals < cap.mineralCapacity) {
                queue.add(pos.q);
                queue.add(pos.r);
            } else {
                flow.mineralDistance = Integer.MAX_VALUE;
            }
        }

        // 2. Propagate distances outward
        int head = 0;
        while (head < queue.size) {
            int curQ = queue.get(head++);
            int curR = queue.get(head++);

            Entity current = Map.instance().getEntityAt(curQ, curR, TilePosition.MYCELIUM);
            if (current == null) continue;
            NutrientFlow curFlow = Mappers.nutrientFlowCMap.get(current);

            for (int[] dir : hexDirections) {
                int nq = curQ + dir[0];
                int nr = curR + dir[1];

                Entity neighbor = Map.instance().getEntityAt(nq, nr, TilePosition.MYCELIUM);
                if (neighbor == null) continue;

                NutrientFlow nFlow = Mappers.nutrientFlowCMap.get(neighbor);
                if (curFlow.mineralDistance != Integer.MAX_VALUE && nFlow.mineralDistance > curFlow.mineralDistance + 1) {
                    nFlow.mineralDistance = curFlow.mineralDistance + 1;
                    queue.add(nq);
                    queue.add(nr);
                }
            }
        }
    }

    private void computeCarbonGradients(ImmutableArray<Entity> nodes) {
        queue.clear();

        for (int i = 0; i < nodes.size(); i++) {
            Entity e = nodes.get(i);
            NutrientFlow flow = Mappers.nutrientFlowCMap.get(e);
            Nutrients nutrients = Mappers.nutrientsCMap.get(e);
            NutrientCapacity cap = Mappers.nutrientCapacityCMap.get(e);
            Position pos = Mappers.positionCMap.get(e);

            flow.carbonDistance = (flow.carbonRoleOverride != null)? flow.carbonRoleOverride.value : flow.carbonRole.value;
            int projectedCarbons = (nutrients != null) ? nutrients.carbons + nutrients.carbonDelta : 0;
            if (flow.carbonDistance < Integer.MAX_VALUE && nutrients != null && cap != null && projectedCarbons < cap.carbonCapacity) {
                queue.add(pos.q);
                queue.add(pos.r);
            } else {
                flow.carbonDistance = Integer.MAX_VALUE;
            }
        }

        int head = 0;
        while (head < queue.size) {
            int curQ = queue.get(head++);
            int curR = queue.get(head++);

            Entity current = Map.instance().getEntityAt(curQ, curR, TilePosition.MYCELIUM);
            if (current == null) continue;
            NutrientFlow curFlow = Mappers.nutrientFlowCMap.get(current);

            for (int[] dir : hexDirections) {
                int nq = curQ + dir[0];
                int nr = curR + dir[1];

                Entity neighbor = Map.instance().getEntityAt(nq, nr, TilePosition.MYCELIUM);
                if (neighbor == null) continue;

                NutrientFlow nFlow = Mappers.nutrientFlowCMap.get(neighbor);
                if (curFlow.carbonDistance != Integer.MAX_VALUE && nFlow.carbonDistance > curFlow.carbonDistance + 1) {
                    nFlow.carbonDistance = curFlow.carbonDistance + 1;
                    queue.add(nq);
                    queue.add(nr);
                }
            }
        }
    }
}
