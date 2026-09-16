package io.github.ethanBostick.screens;

import io.github.ethanBostick.ui.GameHUD;
import io.github.ethanBostick.input.GameController;
import com.badlogic.gdx.InputMultiplexer;
import io.github.ethanBostick.ecs.RenderSystem;
import io.github.ethanBostick.ecs.MultiRenderSystem;
import io.github.ethanBostick.ecs.ActionSystem;
import io.github.ethanBostick.ecs.EntityBuilder;
import io.github.ethanBostick.ecs.SelectionSystem;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.ashley.core.Engine;

//testing
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.ecs.Registry;
//end testing

public class GameScreen implements Screen {

	private GameHUD gameHUD = null;
	private InputMultiplexer multiplexer = null;
	private GameController gameController = null;
	public Engine engine;

	public GameScreen(){}

	@Override
	public void show() {
	//init Ashley ECS
		this.engine = new Engine();
		EntityBuilder.instance(engine);
		Registry.init();
		this.engine.addSystem(new RenderSystem());
		this.engine.addSystem(new MultiRenderSystem());
		this.engine.addSystem(new ActionSystem());
		this.engine.addSystem(new SelectionSystem());

		Map map = Map.instance();
		double[] concentrations = new double[6];
		concentrations[0] = 0.4;
		concentrations[1] = 0.49;
		concentrations[2] = 0.58;
		concentrations[3] = 0.67;
		concentrations[4] = 0.76;
		concentrations[5] = 0.85;
		map.initMap(100,concentrations,0.75,8);

	//init Input and UI
		this.multiplexer = new InputMultiplexer();
		this.gameHUD = new GameHUD();
		this.multiplexer.addProcessor(0,this.gameHUD.stage); 
		this.gameController = new GameController(500, 500);
		this.multiplexer.addProcessor(this.gameController);
		Gdx.input.setInputProcessor(this.multiplexer);
	}

    @Override
    public void render(float delta) {
        //camera control updates
		this.gameController.handleKeyboardInput(delta);
		this.gameController.update(delta);

        //engine systems update
		this.engine.update(delta);
		this.gameHUD.stage.act(delta);
		this.gameHUD.stage.draw();
    }
	@Override
    public void resize(int width, int height) {
        this.gameHUD.resize(width, height);
        this.gameController.resize(width, height);
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
		Registry.dispose();
    }
}
