package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.core.ColorSettings;
import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Synthesizer;
import io.github.ethanBostick.map.ResourceType;

public class SynthesisSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    
    public SynthesisSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, Synthesizer.class, NutrientCapacity.class)
        .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        try{
            Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(entity);
            Synthesizer synthesizer = Mappers.synthesizerCMap.get(entity);
            Position position = Mappers.positionCMap.get(entity);

            float currentCarbon = nutrients.carbons + nutrients.carbonDelta + synthesizer.resourceBuildUp;
            float currentMineral = nutrients.minerals + nutrients.mineralDelta + synthesizer.resourceBuildUp;
            float carbonRoom = Math.max(0, nutrientCapacity.carbonCapacity - currentCarbon);
            float mineralRoom = Math.max(0, nutrientCapacity.mineralCapacity - currentMineral);

            if(synthesizer.resourceType == ResourceType.CARBON && carbonRoom > 0){
                float desiredExtract = Math.min(CommonValues.CARBON_SYNTHESIS_RATE.value, carbonRoom);

                synthesizer.resourceBuildUp += desiredExtract;
                nutrients.carbonDelta += (int)synthesizer.resourceBuildUp;
                if ((int)synthesizer.resourceBuildUp > 0) {
                    EntityBuilder.instance().createFloatingText(
                        position.q, 
                        position.r, 
                        -1,
                        "+" + (int)synthesizer.resourceBuildUp + "C", 
                        ColorSettings.CARBON_GAINS.color,
                        true
                    );
                }
                synthesizer.resourceBuildUp -= (int)synthesizer.resourceBuildUp;
            } 
            else if (synthesizer.resourceType == ResourceType.MINERAL && mineralRoom > 0){
                float desiredExtract = Math.min(CommonValues.MINERAL_SYNTHESIS_RATE.value, mineralRoom);

                synthesizer.resourceBuildUp += desiredExtract;
                nutrients.mineralDelta += (int)synthesizer.resourceBuildUp;
                if ((int)synthesizer.resourceBuildUp > 0) {
                    EntityBuilder.instance().createFloatingText(
                        position.q, 
                        position.r, 
                        -1,
                        "+" + (int)synthesizer.resourceBuildUp + "M", 
                        ColorSettings.MINERAL_GAINS.color,
                        true
                    );
                }
                synthesizer.resourceBuildUp -= (int)synthesizer.resourceBuildUp;
            }
        }
        catch(Exception e){
            System.out.println("SynthesisError: " + e.getMessage());
        }
    }
}

