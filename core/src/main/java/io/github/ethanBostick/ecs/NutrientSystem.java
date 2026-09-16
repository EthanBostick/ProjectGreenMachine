package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

public class NutrientSystem extends IntervalIteratingSystem {
    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public NutrientSystem(float interval) {
        super(Family.all(Nutrients.class).get(), interval);
    }

    @Override
    protected void processEntity(Entity entity) {

    }
}
