package io.github.ethanBostick.core;

import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.ZoomEvent;
import io.github.ethanBostick.events.EventBus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraController implements Observer{
    private final OrthographicCamera camera;
    private final Viewport viewport;

    // Movement speeds & limits
    private float moveSpeed = 400f; // pixels per second
    private float minZoom = 0.5f;
    private float maxZoom = 2.0f;
    private final Vector2 targetPosition = new Vector2();

    public CameraController(float virtualWidth, float virtualHeight, OrthographicCamera camera) {
        this.camera = camera;
        viewport = new ExtendViewport(virtualWidth, virtualHeight, camera);
        viewport.apply();
        
        // Center the camera on startup
        camera.position.set(virtualWidth / 2f, virtualHeight / 2f, 0);
        targetPosition.set(camera.position.x, camera.position.y);

		EventBus.instance().subscribe(EventType.ZOOM,this);
    }

    public void resize(int width, int height){
        this.viewport.update(width, height,true);
    }

    public void update(float delta) {
        // Clamp zoom bounds
        camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);

        // Crucial: recalculate matrices
        camera.update();
    }

    public void handleKeyboardInput(float delta) {
        float effectiveSpeed = moveSpeed * camera.zoom * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += effectiveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= effectiveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= effectiveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += effectiveSpeed;
        }
    }

    public void zoom(float amount) {
        // e.g. amount from scrolled() in InputProcessor
        camera.zoom += amount * 0.1f;
    }

    public void setTarget(float x, float y) {
        this.targetPosition.set(x, y);
    }

    public void setBounds(float minX, float minY, float maxX, float maxY) {
        float halfWidth = (camera.viewportWidth * camera.zoom) / 2f;
        float halfHeight = (camera.viewportHeight * camera.zoom) / 2f;

        camera.position.x = MathUtils.clamp(camera.position.x, minX + halfWidth, maxX - halfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, minY + halfHeight, maxY - halfHeight);
    }

    @Override
    public void onEvent(Event event){
		switch (event.getType()){
			case ZOOM:
                ZoomEvent zm = (ZoomEvent) event;
                this.zoom(zm.amount);
				break;
			default:
				System.out.println("unknown event");
		}
    }

    public OrthographicCamera getCamera() { return camera; }
    public Viewport getViewport() { return viewport; }
}
