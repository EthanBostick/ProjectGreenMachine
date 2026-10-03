package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.ecs.components.Health;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class DegenerationSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    
    public DegenerationSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Health.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        try{
            Position position = Mappers.positionCMap.get(entity);
            Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(entity);
            Health health = Mappers.healthCMap.get(entity);

            //if node health < 50 request nutrient

            // if has 0 nutrinets, degrade health
            // if has nutrients, use some 
        }
        catch(Exception e){
            System.out.println("DegenerationError: " + e.getMessage());
        }
        finally{
        }
    }
}