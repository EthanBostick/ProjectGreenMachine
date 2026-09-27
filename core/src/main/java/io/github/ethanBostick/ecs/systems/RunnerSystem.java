package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import java.lang.Math;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Complete;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

public class RunnerSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public RunnerSystem(float interval, int priority) {
        super(Family.all(MultiTilePosition.class, Position.class, Sprite.class,Direction.class,Complete.class).get(), interval,priority);
        this.interval = interval;
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
            System.out.println("REACHED TARGET");
            targetSprite.texture = null; //stop displaying runner target
            //update growth
            direction.directionVector[0] = 0;
            direction.directionVector[1] = 0;
            Mappers.directionCMap.get(currentPosition).directionVector[0] = direction.directionVector[0];
            Mappers.directionCMap.get(currentPosition).directionVector[1] = direction.directionVector[1];
            EntityBuilder.instance().addDead(runner); //schedule the entity for removal
            return;
        }

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
            int dq = targetQ - currentQ;
            int dr = targetR - currentR;
            int distance = (Math.abs(dq) + Math.abs(dr) + Math.abs(dq + dr)) / 2;
            if (distance == 0) return;

            float t = (1.0f / distance) + 1e-6f; //snall extra amount prevents rounding ties
    
            float fracQ = currentQ + (dq * t);
            float fracR = currentR + (dr * t);
            float fracS = -fracQ - fracR; 

            int q = Math.round(fracQ);
            int r = Math.round(fracR);
            int s = Math.round(fracS);

            float qDiff = Math.abs(q - fracQ);
            float rDiff = Math.abs(r - fracR);
            float sDiff = Math.abs(s - fracS);

            if (qDiff > rDiff && qDiff > sDiff) {
                q = -r - s;
            } else if (rDiff > sDiff) {
                r = -q - s;
            }

            direction.directionVector[0] = q-currentQ;
            direction.directionVector[1] = r-currentR;
        }

        //update current position to get growth 
        Mappers.directionCMap.get(currentPosition).directionVector[0] = direction.directionVector[0];
        Mappers.directionCMap.get(currentPosition).directionVector[1] = direction.directionVector[1];

    }
}