package io.github.ethanBostick.ecs;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class ActionSystem extends IteratingSystem{

    public ActionSystem() {
        super(Family.all(MouseState.class, Position.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position mousePosition = Mappers.positionCMap.get(entity);
        MouseState mouseState = Mappers.mouseStateCMap.get(entity);
        MultiTilePosition mouseDragPosition = Mappers.multiTilePositionCMap.get(entity);

        if (mouseState.heldDown && !Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r)){
            if (mouseDragPosition.points.size > 0){
                int prevQ = mouseDragPosition.points.items[mouseDragPosition.points.size - 2];
                int prevR = mouseDragPosition.points.items[mouseDragPosition.points.size -1];
                if(mousePosition.q != prevQ || mousePosition.r != prevR){
                    Entity mycelium = Map.instance().getEntityAt(prevQ, prevR, TilePosition.MYCELIUM);
                    if (mycelium != null){
                        Mappers.directionCMap.get(mycelium).directionVector[0] = mousePosition.q - prevQ;
                        Mappers.directionCMap.get(mycelium).directionVector[1] = mousePosition.r - prevR;
                    }
                }
            }
        }
    }
}

