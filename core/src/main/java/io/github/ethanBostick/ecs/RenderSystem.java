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

    private static ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Sprite> sMap = ComponentMapper.getFor(Sprite.class);

    //inline class to sort entities by layer
    private static class LayerComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity e1, Entity e2) {
            //invert the layer values so lowest renders first
            int layer1 = -pMap.get(e1).layer;
            int layer2 = -pMap.get(e2).layer;
            
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
        Position position = pMap.get(entity);
        Sprite sprite = sMap.get(entity);

        if (sprite.texture != null){
            float pixelX = HexUtils.getPixelX(position);
            float pixelY = HexUtils.getPixelY(position);
            float width = 64;
            float height = 52;

            // Only draw if the sprite's bounding box intersects the camera's view
            if (camera.frustum.boundsInFrustum(pixelX + width/2f, pixelY + height/2f, 0, width/2f, height/2f, 0)) {
                batch.draw(sprite.texture, pixelX, pixelY);
            }
        }
    }
}
