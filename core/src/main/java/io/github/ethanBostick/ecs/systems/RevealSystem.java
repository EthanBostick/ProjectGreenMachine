package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Vision;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.utils.HexUtils;

public class RevealSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(2*(int)CommonValues.SIGHT_RADIUS.value);
    
    public RevealSystem(float interval, int priority) {
        super(Family.all(Vision.class).exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity cleanser) {
        try{
            Position position = Mappers.positionCMap.get(cleanser);

            if(position == null) return;

            //get radius vector
            int radius = (int)CommonValues.SIGHT_RADIUS.value;
            this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);

            for (int i = 0; i < this.processingArray.size; i += 2) {
                Map.instance().setRevealed(this.processingArray.get(i),this.processingArray.get(i+1));
            }

        }
        catch(Exception e){
            System.out.println("RevealError: " + e.getMessage());
        }
        finally{
            this.processingArray.clear();
        }
    }
}

