package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Depth;
import io.github.ethanBostick.ecs.components.FloatingText;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Registry;

public class FloatingTextSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public FloatingTextSystem(int priority) {
        super(Family.all(FloatingText.class).get(), priority); // Priority 26
        this.batch = Registry.spriteBatch;
        this.font = Registry.worldFont;
        this.camera = Registry.camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Depth depth = Mappers.depthCMap.get(entity);
        if(depth != null && depth.depth != Mappers.depthCMap.get(Registry.player).depth){
            return;
        }

        FloatingText ft = Mappers.floatingTextCMap.get(entity);
        ft.elapsed += deltaTime;

        if (ft.elapsed >= ft.duration) {
            EntityBuilder.instance().dead(entity);
            return;
        }

        // 1. Float upward smoothly at 60 FPS
        ft.worldY += ft.velocityY * deltaTime;

        // 2. Fade alpha out over the second half of its life
        float progress = ft.elapsed / ft.duration;
        float alpha = (progress > 0.5f) ? 1f - ((progress - 0.5f) * 2f) : 1f;

        font.setColor(ft.color.r, ft.color.g, ft.color.b, alpha);
        font.draw(batch, ft.text, ft.worldX, ft.worldY);
    }
}