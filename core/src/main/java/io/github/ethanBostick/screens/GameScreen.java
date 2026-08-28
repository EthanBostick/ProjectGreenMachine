package io.github.ethanBostick.screens;

import io.github.ethanBostick.ui.GameHUD;
import io.github.ethanBostick.input.MapInputAdapter;
import io.github.ethanBostick.input.InputManager;
import io.github.ethanBostick.ecs.RenderSystem;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

//testing
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.core.HexFactory;
//end testing

public class GameScreen implements Screen {

	private GameHUD gameHUD = null;
	private InputManager inputManager = null;
	public Engine engine;

	public GameScreen(){}

	@Override
	public void show() {
		this.gameHUD = new GameHUD();
		this.inputManager = new InputManager(this.gameHUD.stage); //ui stage index 0
		this.inputManager.addProcessor(new MapInputAdapter());
        OrthographicCamera camera = new OrthographicCamera();
        this.inputManager.setupCameraController(1000, 1000, camera);

		//init Ashley ECS
		this.engine = new Engine();
		this.engine.addSystem(new RenderSystem(new SpriteBatch(),camera));
		HexFactory hexFactory = HexFactory.instance(engine);
		Map map = Map.instance();
		map.generateMap(15);

		Gdx.input.setInputProcessor(this.inputManager.getMultiplexer());
	}

    @Override
    public void render(float delta) {
        this.inputManager.poll(delta);
		this.engine.update(delta);
		this.gameHUD.stage.act(delta);
		this.gameHUD.stage.draw();
    }
	@Override
    public void resize(int width, int height) {
        this.gameHUD.resize(width, height);
        this.inputManager.resizeCameraController(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

	@Override
    public void hide() {
        // The screen is being swapped out. Unhook the inputs!
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
    }
}
