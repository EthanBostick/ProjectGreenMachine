package io.github.ethanBostick.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FloatingText implements Component, Poolable {
    public String text = "";
    public float worldX, worldY;
    public float velocityY = 25f; // Pixels per second rising upward
    public float elapsed = 0f;
    public float duration = 1.2f; // Total lifespan in seconds
    public Color color = new Color(Color.WHITE);

    @Override
    public void reset() {
        text = "";
        worldX = 0f;
        worldY = 0f;
        elapsed = 0f;
        color.set(Color.WHITE);
    }
}
