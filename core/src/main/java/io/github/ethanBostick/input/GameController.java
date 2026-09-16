package io.github.ethanBostick.input;

import io.github.ethanBostick.ecs.Mappers;
import io.github.ethanBostick.ecs.Registry;
import io.github.ethanBostick.utils.HexUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.ashley.core.Entity;

public class GameController extends InputAdapter{
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Entity mouse;
    private Vector3 touchVector = new Vector3(0,0,0);

    // Movement speeds & limits
    private float moveSpeed = 400f; // pixels per second
    private float minZoom = 0.5f;
    private float maxZoom = 4.0f;
    private final Vector2 targetPosition = new Vector2();

    public GameController(float virtualWidth, float virtualHeight) {
        this.camera = Registry.camera;
        this.mouse = Registry.mouse;

        viewport = new ExtendViewport(virtualWidth, virtualHeight, camera);
        viewport.apply();
        
        // Center the camera on startup
        camera.position.set(virtualWidth / 2f, virtualHeight / 2f, 0);
        targetPosition.set(camera.position.x, camera.position.y);
    }

    public void resize(int width, int height){
        this.viewport.update(width, height,true);
    }

    public void update(float delta) {
        // Clamp zoom bounds
        camera.zoom = MathUtils.clamp(camera.zoom, minZoom, maxZoom);
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


    public void setTarget(float x, float y) {
        this.targetPosition.set(x, y);
    }

    public void setBounds(float minX, float minY, float maxX, float maxY) {
        float halfWidth = (camera.viewportWidth * camera.zoom) / 2f;
        float halfHeight = (camera.viewportHeight * camera.zoom) / 2f;

        this.camera.position.x = MathUtils.clamp(camera.position.x, minX + halfWidth, maxX - halfWidth);
        this.camera.position.y = MathUtils.clamp(camera.position.y, minY + halfHeight, maxY - halfHeight);
    }

	//reads mouse clicks
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.touchVector.x = screenX;
        this.touchVector.y = screenY;
        this.touchVector = this.camera.unproject(this.touchVector);

        Mappers.positionCMap.get(this.mouse).x = this.touchVector.x;
        Mappers.positionCMap.get(this.mouse).y = this.touchVector.y;
        Mappers.mouseStateCMap.get(this.mouse).button = button;
        Mappers.mouseStateCMap.get(this.mouse).pressedDown = true;
        HexUtils.getAxialFromPixel(Mappers.positionCMap.get(this.mouse));
		return true;
	}

    @Override 
    public boolean touchDragged(int screenX, int screenY, int pointer){
        this.touchVector.x = screenX;
        this.touchVector.y = screenY;
        this.touchVector = this.camera.unproject(this.touchVector);

        Mappers.positionCMap.get(this.mouse).x = this.touchVector.x;
        Mappers.positionCMap.get(this.mouse).y = this.touchVector.y;
        HexUtils.getAxialFromPixel(Mappers.positionCMap.get(this.mouse));
        Mappers.mouseStateCMap.get(this.mouse).heldDown = true;
        return true;
    }

    @Override 
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        Mappers.mouseStateCMap.get(this.mouse).pressedUp = true;
        Mappers.mouseStateCMap.get(this.mouse).heldDown = false;
        Mappers.mouseStateCMap.get(this.mouse).pressedDown = false;
        return true;
    }

	//key read method
	@Override
	public boolean keyDown(int keycode){

		switch(keycode){
			case Input.Keys.ESCAPE:
                System.out.println("clearing selected");
                Mappers.mouseStateCMap.get(this.mouse).clearSelect = true;
		        return true;
		}
		return false;
	}

	//scroll wheel reading
	@Override
	public boolean scrolled(float amountX, float amountY){

		if (amountY != 0){
            camera.zoom += amountY * 0.1f;
			return true;
		}
		return false;
	}

    public OrthographicCamera getCamera() { return camera; }
    public Viewport getViewport() { return viewport; }
}
