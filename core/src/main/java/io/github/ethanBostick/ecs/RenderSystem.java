package io.github.ethanBostick.ecs;

import io.github.ethanBostick.utils.HexUtils;

import java.util.Comparator;

import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class RenderSystem extends SortedIteratingSystem{
    private final SpriteBatch batch;
    private OrthographicCamera camera = null;

    //inline class to sort entities by layer
    private static class LayerComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity e1, Entity e2) {
            //invert the layer values so lowest renders first
            int layer1 = -Mappers.positionCMap.get(e1).layer;
            int layer2 = -Mappers.positionCMap.get(e2).layer;
            
            return Integer.compare(layer2, layer1);
        }
    }

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Family.all(Position.class, Sprite.class).get(), new LayerComparator());
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {

        //resorts the entities, only needs to be done if an entity moves layers
        //forceSort();

        if (this.camera != null){
            this.batch.setProjectionMatrix(this.camera.combined);
        }
        this.batch.begin();
        
        // IteratingSystem runs processEntity() on all matching entities
        super.update(deltaTime);
        
        this.batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = Mappers.positionCMap.get(entity);
        Sprite sprite = Mappers.spriteCMap.get(entity);

        if (sprite.texture != null){
            float pixelX = HexUtils.getPixelX(position);
            float pixelY = HexUtils.getPixelY(position);

            // Only draw if the sprite's bounding box intersects the camera's view
            if (camera.frustum.boundsInFrustum(pixelX + HexUtils.WIDTH/2f, pixelY + HexUtils.HEIGHT/2f, 0, HexUtils.WIDTH/2f, HexUtils.HEIGHT/2f, 0)) {
                batch.draw(sprite.texture, pixelX, pixelY);
            }
        }
    }
}
