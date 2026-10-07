package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.ecs.components.Depth;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.utils.HexUtils;

import java.util.Comparator;

import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class RenderSystem extends SortedIteratingSystem{
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    //inline class to sort entities by layer
    private static class LayerComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity e1, Entity e2) {
            //invert the layer values so lowest renders first
            int layer1 = -Mappers.positionCMap.get(e1).layer;
            int layer2 = -Mappers.positionCMap.get(e2).layer;
            
            //primary sort by layer
            int layerComparison = Integer.compare(layer2, layer1);
            if (layerComparison != 0){
                return layerComparison;
            }

            //secondary sort by y-value
            //inverted so lower sprites render first
            float groundY1 = -HexUtils.getPixelY(Mappers.positionCMap.get(e1));
            float groundY2 = -HexUtils.getPixelY(Mappers.positionCMap.get(e2));

            int yComparison = Float.compare(groundY1, groundY2);
            if(yComparison != 0){
                return yComparison;
            }

            //tertiary row sort
            float groundX1 = HexUtils.getPixelX(Mappers.positionCMap.get(e1));
            float groundX2 = HexUtils.getPixelX(Mappers.positionCMap.get(e2));

            int xComparison = Float.compare(groundX1, groundX2);
            if(xComparison != 0){
                return xComparison;
            }

            //fallback
            return Integer.compare(e1.hashCode(), e2.hashCode());
        }
    }

    public RenderSystem(int priority) {
        super(Family.all(Position.class, Sprite.class).get(), new LayerComparator(), priority);
        this.batch = Registry.spriteBatch;
        this.camera = Registry.camera;
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
        if (Mappers.deadCMap.get(entity) != null) return;

        Depth depth = Mappers.depthCMap.get(entity);
        if(depth != null && depth.depth != Mappers.depthCMap.get(Registry.player).depth){
            return;
        }

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
