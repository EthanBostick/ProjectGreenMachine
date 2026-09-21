package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.graphics.Texture;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

public class MyceliumSystem extends IntervalIteratingSystem {
    public final float interval; //half seconds
    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public MyceliumSystem(float interval) {
        super(Family.all(Nutrients.class, Density.class,Position.class).get(), interval*10);
        this.interval = interval * 10;
    }

    private void updateDensityData(Sprite targetSprite, NutrientCapacity nutrientCapacity, int newDensity){
        //sprite update
        switch (newDensity){
            case 1:
                targetSprite.texture = TextureUtils.pathToTexture("myceliumD1.png");
                break;
            case 2:
                targetSprite.texture = TextureUtils.pathToTexture("myceliumD2.png");
                break;
            case 3:
                targetSprite.texture = TextureUtils.pathToTexture("myceliumD3.png");
                break;
        }
        //more hold
		nutrientCapacity.carbonCapacity = 10*newDensity;
		nutrientCapacity.mineralCapacity = 5*newDensity;
    }

    @Override
    protected void processEntity(Entity entity) {
        Density density = Mappers.densityCMap.get(entity);
        Position position = Mappers.positionCMap.get(entity);
        Direction direction = Mappers.directionCMap.get(entity);
        Sprite sprite = Mappers.spriteCMap.get(entity);
        Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
        NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(entity);

        //density sprite updates
        this.updateDensityData(sprite, nutrientCapacity, density.density);

        //mycelium growth, prob temp, testing rn
        if (Map.instance().getEntityAt(position.q + direction.directionVector[0], position.r + direction.directionVector[1], TilePosition.MYCELIUM) == null){
            int newQ = position.q + direction.directionVector[0];
            int newR = position.r + direction.directionVector[1];
            Entity newMycelium = EntityBuilder.instance().createMycelium(newQ,newR,1,0,0);
            //inherit direction vector from parent
            Mappers.directionCMap.get(newMycelium).directionVector[0] = direction.directionVector[0];
            Mappers.directionCMap.get(newMycelium).directionVector[1] = direction.directionVector[1];

            EntityBuilder.instance().addToEngine(newMycelium);
            Map.instance().setEntityAt(newQ, newR, newMycelium, TilePosition.MYCELIUM);       
        }
        else{
            int newQ = position.q + direction.directionVector[0];
            int newR = position.r + direction.directionVector[1];
            if (Math.abs(newQ) + Math.abs(newR) !=0){
                Entity oldMycelium = Map.instance().getEntityAt(newQ, newR, TilePosition.MYCELIUM);
                Density oldDensity = Mappers.densityCMap.get(oldMycelium);
                NutrientCapacity oldNutrientCapacity = Mappers.nutrientCapacityCMap.get(oldMycelium);
                oldDensity.density = (oldDensity.density >= 3)? 3 : oldDensity.density + 1;
                Sprite oldSprite = Mappers.spriteCMap.get(oldMycelium);

                //update density sprite
                this.updateDensityData(oldSprite,oldNutrientCapacity, oldDensity.density);
            }
        }
    }
}