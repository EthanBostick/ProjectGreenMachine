package io.github.ethanBostick.ecs;

import io.github.ethanBostick.utils.HexUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSystem extends IteratingSystem{
    private final SpriteBatch batch;

    private ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private ComponentMapper<Sprite> sMap = ComponentMapper.getFor(Sprite.class);

    public RenderSystem(SpriteBatch batch) {
        super(Family.all(Position.class, Sprite.class).get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        // Set up the batch with the camera projection before iterating
        batch.begin();
        
        // IteratingSystem runs processEntity() on all matching entities
        super.update(deltaTime);
        
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Extract the data
        Position position = pMap.get(entity);
        Sprite sprite = sMap.get(entity);

        // Apply the logic
        if (sprite.texture != null){
            batch.draw(sprite.texture,HexUtils.getPixelX(position),HexUtils.getPixelY(position));
        }
    }
}
