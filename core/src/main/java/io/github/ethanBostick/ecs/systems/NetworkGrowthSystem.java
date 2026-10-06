package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Density;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

public class NetworkGrowthSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public NetworkGrowthSystem(float interval,int priority) {
        super(Family.all(Nutrients.class,NutrientCapacity.class,NutrientFlow.class, Direction.class,Position.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        if (Mappers.deadCMap.get(entity) != null) return;
    
        Position position = Mappers.positionCMap.get(entity);
        Direction direction = Mappers.directionCMap.get(entity);
        // Nutrients nutrients = Mappers.nutrientsCMap.get(entity);

        if (Math.abs(direction.directionVector[0]) + Math.abs(direction.directionVector[1]) ==0) return; //if magnitude is 0 return

        int newQ = position.q + direction.directionVector[0];
        int newR = position.r + direction.directionVector[1];

        //mycelium growth, prob temp, testing rn
        if (Map.instance().getEntityAt(position.q + direction.directionVector[0], position.r + direction.directionVector[1], TilePosition.MYCELIUM) == null){
            Entity newBuild = EntityBuilder.instance().createBuild(BuildType.MYCELIUM_D1,newQ,newR,3,1);
            EntityBuilder.instance().addToEngine(newBuild);
            //add to map so none may build there
            Map.instance().setEntityAt(newQ, newR, newBuild, TilePosition.MYCELIUM);       
        }
        else{
            Entity oldMycelium = Map.instance().getEntityAt(newQ, newR, TilePosition.MYCELIUM);
            Density oldDensity = Mappers.densityCMap.get(oldMycelium);
            if (oldDensity == null) return;
            if (oldDensity.density == 3) return;

            Entity upgrade;
            if (oldDensity.density +1 == 3){
                    upgrade = EntityBuilder.instance().createBuild(BuildType.MYCELIUM_D3,newQ,newR,3,1);
            }
            else{
                    upgrade = EntityBuilder.instance().createBuild(BuildType.MYCELIUM_D2,newQ,newR,3,1);
            }

            EntityBuilder.instance().addToEngine(upgrade);
            Map.instance().setEntityAt(newQ, newR, upgrade, TilePosition.MYCELIUM);       
            //clear old mycelium
            EntityBuilder.instance().dead(entity);
        }
    }
}