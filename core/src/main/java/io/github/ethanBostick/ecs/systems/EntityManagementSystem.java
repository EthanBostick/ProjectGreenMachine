package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.systems.IteratingSystem;

import io.github.ethanBostick.ecs.components.Child;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class EntityManagementSystem extends IteratingSystem{

    public EntityManagementSystem(int priority) {
        super(Family.all(Dead.class).get(), priority);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        try{
            //will clear the map position
            //Note: Should still clear position elsewhere if that map placement may be accessed by something else
            Position position = Mappers.positionCMap.get(entity);
            if (position != null && position.tilePosition != null){
                Entity targetDelete = Map.instance().getEntityAt(position.q, position.r, position.tilePosition);
                if(targetDelete == entity){
                    Map.instance().setEntityAt(position.q, position.r, null, position.tilePosition);
                }
            }

            this.getEngine().removeEntity(entity); //calls reset on all components and returns them to pool + removes entity
        }
        catch(Exception e){
            System.out.println("EntityManagementError: "+ e.getMessage());
        }
        finally{

        }
    }
}
