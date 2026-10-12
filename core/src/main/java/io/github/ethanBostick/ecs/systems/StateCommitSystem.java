package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import io.github.ethanBostick.ecs.components.ActiveTool;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Nutrients;

public class StateCommitSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private final Entity thePlayer;
    
    public StateCommitSystem(float interval, int priority) {
        //excludes the player entity
        super(Family.all(Nutrients.class)
            .exclude(ActiveTool.class, Dead.class).get(), interval, priority);
        this.interval = interval;
        this.thePlayer = Registry.player;
    }

    @Override
    public void updateInterval() {
        Nutrients playerNutrients = Mappers.nutrientsCMap.get(this.thePlayer);
        NutrientCapacity networkMax = Mappers.nutrientCapacityCMap.get(this.thePlayer);
        playerNutrients.carbons = 0;
        playerNutrients.minerals = 0;
        playerNutrients.carbonDelta = 0;
        playerNutrients.mineralDelta = 0;
        networkMax.carbonCapacity = 0;
        networkMax.mineralCapacity = 0;

        super.updateInterval();
    }

    @Override
    protected void processEntity(Entity entity) {
        Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
        try{
            Nutrients networkNutrients = Mappers.nutrientsCMap.get(this.thePlayer);
            NutrientCapacity networkMax = Mappers.nutrientCapacityCMap.get(this.thePlayer);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(entity);
            NutrientFlow nutrientFlow = Mappers.nutrientFlowCMap.get(entity);

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

            //check if its part of the network and update player metrics
            if (nutrientFlow != null && nutrientCapacity != null){
                //nutrients usage metrics
                networkNutrients.carbonDelta += nutrients.carbonDelta;
                networkNutrients.mineralDelta += nutrients.mineralDelta;

                //current nutrient metrics
                networkNutrients.carbons += nutrients.carbons;
                networkMax.carbonCapacity += nutrientCapacity.carbonCapacity;
                networkNutrients.minerals += nutrients.minerals;
                networkMax.mineralCapacity += nutrientCapacity.mineralCapacity;
            }
        }
        catch(Exception e){
            System.out.println("StateCommitError: " + e.getMessage());
        }
        finally{
            //reset deltas
            nutrients.carbonDelta = 0;
            nutrients.mineralDelta = 0;
        }
    }
}
