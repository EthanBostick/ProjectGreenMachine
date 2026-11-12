package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Highlighter;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class SelectionOverlay extends IteratingSystem{
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    public SelectionOverlay(int priority) {
        super(Family.all(Position.class, Highlighter.class, Sprite.class).exclude(Dead.class).get(), priority);
        this.batch = Registry.spriteBatch;
        this.camera = Registry.camera;
    }

    @Override
    public void update(float deltaTime) {

        if (this.camera != null){
            this.batch.setProjectionMatrix(this.camera.combined);
        }
        this.batch.begin();
        
        // IteratingSystem runs processEntity() on all matching entities
        super.update(deltaTime);
        
        this.batch.end();
    }

    private void drawArea(Position p, int area, Texture display){
		for (int q = -area ; q <= area ; q ++){
			for (int r = Math.max(-area, -q - area); r <= Math.min(area, -q + area); r ++){
                int pixelX = HexUtils.getPixelX(q + p.q);
                int pixelY = HexUtils.getPixelY(q + p.q, r + p.r);
                if (camera.frustum.boundsInFrustum(pixelX + HexUtils.WIDTH/2f, pixelY + HexUtils.HEIGHT/2f, 0, HexUtils.WIDTH/2f, HexUtils.HEIGHT/2f, 0)) {
                    batch.draw(display, pixelX, pixelY);
                }
            }
        }
    }

    private void displayArea(Entity entity){
        int playerDepth = Mappers.depthCMap.get(Registry.player).depth;
        Position position = Mappers.positionCMap.get(entity);
        if (Mappers.extractionCMap.get(entity) != null && playerDepth == 1){
            drawArea(position, (int)CommonValues.EXTRACTION_RADIUS.value, TextureUtils.pathToTexture("areaOverlay.png"));
        }
        else if (Mappers.cleanserCMap.get(entity) != null){
            drawArea(position, Mappers.cleanserCMap.get(entity).radius, TextureUtils.pathToTexture("areaOverlay.png"));
        }
        else if (Mappers.cultivatorCMap.get(entity) != null){
            drawArea(position, Mappers.cultivatorCMap.get(entity).radius, TextureUtils.pathToTexture("areaOverlay.png"));
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        Position position = Mappers.positionCMap.get(entity);
        Sprite sprite = Mappers.spriteCMap.get(entity);

        if (sprite.texture != null){
            float pixelX = HexUtils.getPixelX(position);
            float pixelY = HexUtils.getPixelY(position);

            //selected mycelium has area
            Entity mycelium = Map.instance().getEntityAt(position.q, position.r, TilePosition.MYCELIUM); 
            if(mycelium != null){
                displayArea(mycelium);
            }

            // Only draw if the sprite's bounding box intersects the camera's view
            if (camera.frustum.boundsInFrustum(pixelX + HexUtils.WIDTH/2f, pixelY + HexUtils.HEIGHT/2f, 0, HexUtils.WIDTH/2f, HexUtils.HEIGHT/2f, 0)) {
                batch.draw(sprite.texture, pixelX, pixelY);
            }
        }
    }
}



