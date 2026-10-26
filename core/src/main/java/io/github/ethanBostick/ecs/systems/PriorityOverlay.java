package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Density;
import io.github.ethanBostick.ecs.components.Green;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class PriorityOverlay extends IteratingSystem{
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public PriorityOverlay(int priority) {
        super(Family.all(Position.class, NutrientFlow.class, Sprite.class).exclude(Dead.class).get(), priority);
        this.batch = Registry.spriteBatch;
        this.font = Registry.worldFont;
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
        NutrientFlow flow = Mappers.nutrientFlowCMap.get(entity);

        if (flow != null){
            float pixelX = HexUtils.getPixelX(position);
            float pixelY = HexUtils.getPixelY(position);
            int displayNum = (flow.carbonRoleOverride != null)? flow.carbonRoleOverride.index : flow.carbonRole.index;

            // Only draw if the sprite's bounding box intersects the camera's view
            if (camera.frustum.boundsInFrustum(pixelX + HexUtils.WIDTH/2f, pixelY + HexUtils.HEIGHT/2f, 0, HexUtils.WIDTH/2f, HexUtils.HEIGHT/2f, 0)) {
                font.setColor(Color.RED.r, Color.RED.g, Color.RED.b, 1);
                font.draw(batch,""+displayNum , pixelX+18, pixelY+30);
            }
        }
    }
}



