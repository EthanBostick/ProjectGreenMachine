package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Complete;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class RunnerSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(4);
    private final int[][] hexDirections;

    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public RunnerSystem(float interval, int priority) {
        super(Family.all(MultiTilePosition.class, Position.class, Sprite.class,Direction.class,Complete.class).get(), interval,priority);
        this.interval = interval;
        this.hexDirections = HexUtils.hexDirections;
    }

    @Override
    protected void processEntity(Entity runner) {
        if (Mappers.deadCMap.get(runner) != null) return;

        MultiTilePosition startAndEndPosition = Mappers.multiTilePositionCMap.get(runner);
        Direction direction = Mappers.directionCMap.get(runner);
        Sprite targetSprite = Mappers.spriteCMap.get(runner);

        int currentQ = startAndEndPosition.points.get(0);
        int currentR = startAndEndPosition.points.get(1);
        int targetQ = startAndEndPosition.points.get(2);
        int targetR = startAndEndPosition.points.get(3);

        Entity currentPosition = Map.instance().getEntityAt(currentQ, currentR, TilePosition.MYCELIUM);

        //no mycelium in current start, do not continue runner
        if(currentPosition == null) return; 

        //check if reached target
        if(currentQ == targetQ && currentR == targetR){
            targetSprite.texture = null; //stop displaying runner target
            //update growth
            direction.directionVector[0] = 0;
            direction.directionVector[1] = 0;
            Mappers.directionCMap.get(currentPosition).directionVector[0] = direction.directionVector[0];
            Mappers.directionCMap.get(currentPosition).directionVector[1] = direction.directionVector[1];
            EntityBuilder.instance().dead(runner); //schedule the entity for removal
            return;
        }

        //loops to check the next step, if its been filled it checks the next, until untaken step
        for(;;){
            //check if reached next step && its been built
            Entity nextStepPosition = Map.instance().getEntityAt(currentQ+direction.directionVector[0], currentR+direction.directionVector[1], TilePosition.MYCELIUM);
            if (nextStepPosition != null && Mappers.buildCMap.get(nextStepPosition) == null){
                //update last steps direction to stop growth
                Mappers.directionCMap.get(currentPosition).directionVector[0] = 0;
                Mappers.directionCMap.get(currentPosition).directionVector[1] = 0;

                //update starting position
                startAndEndPosition.points.set(0, currentQ + direction.directionVector[0]);
                startAndEndPosition.points.set(1, currentR + direction.directionVector[1]);

                //update currentPosition
                currentQ = currentQ+direction.directionVector[0];
                currentR = currentR+direction.directionVector[1];
                currentPosition = nextStepPosition;

                //create direction for next step
                this.processingArray.clear();

                int dq = targetQ - currentQ;
                int dr = targetR - currentR;
                int distance = (Math.abs(dq) + Math.abs(dr) + Math.abs(dq + dr)) / 2;

                if (distance == 0) {
                    return;
                }

                int minRemainingDist = Integer.MAX_VALUE;

                for (int[] dir : hexDirections) {
                    int nextQ = currentQ + dir[0];
                    int nextR = currentR + dir[1];

                    // Distance from this step to the target
                    int ndq = targetQ - nextQ;
                    int ndr = targetR - nextR;
                    int remDist = (Math.abs(ndq) + Math.abs(ndr) + Math.abs(ndq + ndr)) / 2;

                    if (remDist < minRemainingDist) {
                        // Found a strictly better step: clear older ones and record this
                        minRemainingDist = remDist;
                        this.processingArray.clear();
                        this.processingArray.add(dir[0]);
                        this.processingArray.add(dir[1]);
                    } else if (remDist == minRemainingDist) {
                        // Exact tie with the current best: add the second alternative
                        this.processingArray.add(dir[0]);
                        this.processingArray.add(dir[1]);
                    }
                }

                boolean moved = false;

                for (int i = 0; i < this.processingArray.size; i += 2) {
                    int stepQ = this.processingArray.get(i);
                    int stepR = this.processingArray.get(i + 1);

                    Entity obstacle = Map.instance().getEntityAt(
                        currentQ + stepQ, 
                        currentR + stepR, 
                        TilePosition.UNDERGROUND_TERRAIN
                    );

                    // Take the first unblocked valid vector
                    if (obstacle == null) {
                        direction.directionVector[0] = stepQ;
                        direction.directionVector[1] = stepR;
                        moved = true;
                        break;
                    }
                }

                // All available steps were blocked
                if (!moved) {
                    targetSprite.texture = null; // stop displaying runner target
                    direction.directionVector[0] = 0;
                    direction.directionVector[1] = 0;
                    Mappers.directionCMap.get(currentPosition).directionVector[0] = 0;
                    Mappers.directionCMap.get(currentPosition).directionVector[1] = 0;
                    EntityBuilder.instance().dead(runner); // schedule entity removal
                    return;
                }
            }
            else{
                break;
            }
        }

        //update current position to get growth 
        Mappers.directionCMap.get(currentPosition).directionVector[0] = direction.directionVector[0];
        Mappers.directionCMap.get(currentPosition).directionVector[1] = direction.directionVector[1];

    }
}