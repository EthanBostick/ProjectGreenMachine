package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;

public class StateCommitSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    
    public StateCommitSystem(float interval, int priority) {
        super(Family.all(Nutrients.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        try{
            //nutrient deltas
            Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(entity);

            if (nutrients != null){
                if (nutrientCapacity != null){
                    nutrients.minerals = MathUtils.clamp(
                        nutrients.minerals + nutrients.mineralDelta, 
                        0, 
                        nutrientCapacity.mineralCapacity
                    );
                    nutrients.carbons = MathUtils.clamp(
                        nutrients.carbons + nutrients.carbonDelta, 
                        0, 
                        nutrientCapacity.carbonCapacity
                    );
                }
                else{
                    nutrients.carbons += nutrients.carbonDelta;
                    nutrients.minerals += nutrients.mineralDelta;
                }
                nutrients.carbonDelta = 0;
                nutrients.mineralDelta = 0;
            }
        }
        catch(Exception e){
            System.out.println("StateCommitError: " + e.getMessage());
        }
    }
}
