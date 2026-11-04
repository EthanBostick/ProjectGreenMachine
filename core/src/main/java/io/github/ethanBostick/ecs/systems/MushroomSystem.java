package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Mushroom;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.utils.HexUtils;

public class MushroomSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(14);
    
    public MushroomSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Mushroom.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity mushroomNode) {
        try{
            Nutrients nutrients = Mappers.nutrientsCMap.get(mushroomNode);
            NutrientCapacity capacity = Mappers.nutrientCapacityCMap.get(mushroomNode);

            //skip if not yet reached capacity
            if (nutrients.carbons < capacity.carbonCapacity || nutrients.minerals < capacity.mineralCapacity){
                return;
            }

            //launch spores (find landing spot)
            Position position = Mappers.positionCMap.get(mushroomNode);
            HexUtils.getAreaAround(position, (int)CommonValues.SPORE_RADIUS.value, processingArray);
            
        }
        catch(Exception e){
            System.out.println("MushroomError: " + e.getMessage());
        }
        finally{
            this.processingArray.clear();
        }
    }
}

