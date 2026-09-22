package io.github.ethanBostick.screens;

import io.github.ethanBostick.ui.GameHUD;
import io.github.ethanBostick.input.GameController;

import io.github.ethanBostick.ecs.RenderSystem;
import io.github.ethanBostick.ecs.MultiRenderSystem;
import io.github.ethanBostick.ecs.VectorRenderSystem;

import io.github.ethanBostick.ecs.ActionSystem;
import io.github.ethanBostick.ecs.NetworkGrowthSystem;
import io.github.ethanBostick.ecs.NetworkFlowSystem;
import io.github.ethanBostick.ecs.RunnerSystem;

import io.github.ethanBostick.ecs.PlayerStateSystem;
import io.github.ethanBostick.ecs.EntityBuilder;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.InputMultiplexer;

//testing
import io.github.ethanBostick.map.BiomeType;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.ecs.Registry;
//end testing

public class GameScreen implements Screen {

	private GameHUD gameHUD = null;
	private InputMultiplexer multiplexer = null;
	private GameController gameController = null;
	public float tickRate = 0.5f;
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
		this.engine.addSystem(new VectorRenderSystem());

		this.engine.addSystem(new ActionSystem());
		this.engine.addSystem(new NetworkGrowthSystem(tickRate));
		this.engine.addSystem(new NetworkFlowSystem(tickRate));
		this.engine.addSystem(new RunnerSystem(tickRate));
		this.engine.addSystem(new PlayerStateSystem());

		Map map = Map.instance();
		double[] concentrations = new double[] {
			0.4,//blank tile
			0.09,
			0.09,
			0.09,
			0.15,
			0.09,
			0.09};
		BiomeType[] biomes = new BiomeType[]{
			BiomeType.BLANK,
			BiomeType.DESERT,
			BiomeType.GRASS_LAND,
			BiomeType.FOREST,
			BiomeType.MOUNTAIN,
			BiomeType.BOREAL,
			BiomeType.TAIGA
		};

		map.initConfig(100, concentrations,biomes,0.75 , 10, 67);
		map.initMap();

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
