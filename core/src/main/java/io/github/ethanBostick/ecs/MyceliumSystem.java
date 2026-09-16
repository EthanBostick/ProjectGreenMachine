package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.graphics.Texture;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

public class MyceliumSystem extends IntervalIteratingSystem {
    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public MyceliumSystem(float interval) {
        super(Family.all(Nutrients.class, Density.class,Position.class).get(), interval);
    }

    @Override
    protected void processEntity(Entity entity) {
        Density density = Mappers.densityCMap.get(entity);
        Position position = Mappers.positionCMap.get(entity);
        Direction direction = Mappers.directionCMap.get(entity);
        Sprite sprite = Mappers.spriteCMap.get(entity);

        if (Map.instance().getEntityAt(position.q + direction.directionVector[0], position.r + direction.directionVector[1], TilePosition.MYCELIUM) == null){
            int newQ = position.q + direction.directionVector[0];
            int newR = position.r + direction.directionVector[1];
            Entity newMycelium = EntityBuilder.instance().createMycelium(newQ,newR,1);
            EntityBuilder.instance().addToEngine(newMycelium);
            Map.instance().setEntityAt(newQ, newR, newMycelium, TilePosition.MYCELIUM);       
        }
        else{
            int newQ = position.q + direction.directionVector[0];
            int newR = position.r + direction.directionVector[1];
            Entity oldMycelium = Map.instance().getEntityAt(newQ, newR, TilePosition.MYCELIUM);
            Density oldDensity = Mappers.densityCMap.get(oldMycelium);
            oldDensity.density = (oldDensity.density >= 3)? 3 : oldDensity.density + 1;
        }

        switch (density.density){
            case 1:
                sprite.texture = TextureUtils.pathToTexture("myceliumD1.png");
                break;
            case 2:
                sprite.texture = TextureUtils.pathToTexture("myceliumD2.png");
                break;
            case 3:
                sprite.texture = TextureUtils.pathToTexture("myceliumD3.png");
                break;
        }

    }
}