package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Depth;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Name;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Region;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class RegionOverlay extends IteratingSystem{
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    public RegionOverlay(int priority) {
        super(Family.all(Position.class, Region.class).exclude(Dead.class).get(), priority);
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

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        Position position = Mappers.positionCMap.get(entity);
        Sprite sprite = Mappers.spriteCMap.get(entity);
        Name tileName = Mappers.nameCMap.get(entity);
        Texture overlayTexture = (tileName != null)? TextureUtils.pathToTexture(tileName.name +"Overlay.png") :null;

        // switch(regionType){
        //     case RESTORED:
        //         break;
        //     case POLLUTED:
        //         break;
        //     case RADIOACTIVE:
        //         break;
        //     case BARREN:
        //         break;
        //     case MOUNTAIN:
        //         break;
        //     default:
        //         overlayTexture = null;
        // }


        Texture targetDrawing = (Map.instance().getRevealState(position) == 0 && !sprite.revealed)?
            null:
            overlayTexture;

        if (targetDrawing != null){
            float pixelX = HexUtils.getPixelX(position);
            float pixelY = HexUtils.getPixelY(position);

            // Only draw if the sprite's bounding box intersects the camera's view
            if (camera.frustum.boundsInFrustum(pixelX + HexUtils.WIDTH/2f, pixelY + HexUtils.HEIGHT/2f, 0, HexUtils.WIDTH/2f, HexUtils.HEIGHT/2f, 0)) {
                batch.draw(targetDrawing, pixelX, pixelY);
            }
        }
    }
}

