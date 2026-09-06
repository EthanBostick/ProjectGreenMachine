package io.github.ethanBostick.ecs;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;


public class MapGenerationSystem extends IteratingSystem{
    private int thresh;

    private boolean converged = false;
    private static ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Biome> bMap = ComponentMapper.getFor(Biome.class);

    public MapGenerationSystem(int thresh) {
        super(Family.all(Position.class, Biome.class).get());
        this.thresh = thresh;
    }

    @Override
    public void update(float deltaTime) {
        if(converged){
            // send event to shut off this system
        }

        // IteratingSystem runs processEntity() on all matching entities
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = pMap.get(entity);
        Biome biome = bMap.get(entity);

    }
}
