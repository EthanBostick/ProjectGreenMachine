package io.github.ethanBostick.ecs;

import io.github.ethanBostick.utils.HexUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;

public class RenderSystem extends IteratingSystem{
    private ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private ComponentMapper<Sprite> sMap = ComponentMapper.getFor(Sprite.class);

    public RenderSystem() {
        super(Family.all(Position.class, Sprite.class).get());
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Extract the data
        Position position = pMap.get(entity);
        Sprite sprite = sMap.get(entity);

        // Apply the logic
    }
}
