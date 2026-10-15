package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
        this.font = Registry.uiSkin.getFont("default-font");
        this.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        //this.font.getData().setScale(0.75f); // 75% size
        this.font.setUseIntegerPositions(false); //use non int pixel pos
        this.camera = Registry.camera;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        this.font.getData().setScale(0.75f);
        super.update(deltaTime);
        font.setColor(Color.WHITE); //references the same Texture regions so color must be reset
        this.font.getData().setScale(1f);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FloatingText ft = Mappers.floatingTextCMap.get(entity);
        ft.elapsed += deltaTime;

        if (ft.elapsed >= ft.duration) {
            getEngine().removeEntity(entity); // PooledEngine automatically frees components
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