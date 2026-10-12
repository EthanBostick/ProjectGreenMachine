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

            if (flow.needsMinerals && nutrients != null && cap != null && nutrients.minerals < cap.mineralCapacity) {
                // Tier 1 Priority: Active consumers needing minerals
                flow.mineralDistance = 0;
                queue.add(pos.q);
                queue.add(pos.r);
            } else if (flow.isStorageNode && nutrients != null && cap != null && nutrients.minerals < cap.mineralCapacity) {
                // Tier 2 Priority: Storage buffer absorbing surplus
                flow.mineralDistance = 100;
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

            if (flow.needsCarbons && nutrients != null && cap != null && nutrients.carbons < cap.carbonCapacity) {
                flow.carbonDistance = 0;
                queue.add(pos.q);
                queue.add(pos.r);
            } else if (flow.isStorageNode && nutrients != null && cap != null && nutrients.carbons < cap.carbonCapacity) {
                flow.carbonDistance = 100;
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
