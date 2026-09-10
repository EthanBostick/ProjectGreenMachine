package io.github.ethanBostick.ecs;

import io.github.ethanBostick.utils.HexUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ActionSystem extends IteratingSystem{
    private final SpriteBatch batch;

    private static ComponentMapper<MouseState> mMap = ComponentMapper.getFor(MouseState.class);
    private static ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Sprite> sMap = ComponentMapper.getFor(Sprite.class);

    public ActionSystem(SpriteBatch batch) {
        super(Family.all(MouseState.class, Position.class, Sprite.class).get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        this.batch.begin();
        
        // IteratingSystem runs processEntity() on all matching entities
        super.update(deltaTime);
        
        this.batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = pMap.get(entity);
        Sprite sprite = sMap.get(entity);

        if (sprite.texture != null){
            float pixelX = HexUtils.getPixelX(position);
            float pixelY = HexUtils.getPixelY(position);
            float width = 64;
            float height = 52;

            batch.draw(sprite.texture, pixelX, pixelY);
        }
    }
}

