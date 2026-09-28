package io.github.ethanBostick.screens;

import io.github.ethanBostick.ui.GameHUD;
import io.github.ethanBostick.input.GameController;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.ecs.systems.ActionSystem;
import io.github.ethanBostick.ecs.systems.BuildSystem;
import io.github.ethanBostick.ecs.systems.EntityManagementSystem;
import io.github.ethanBostick.ecs.systems.ExtractionSystem;
import io.github.ethanBostick.ecs.systems.CleansingSystem;
import io.github.ethanBostick.ecs.systems.FlowGradientSystem;
import io.github.ethanBostick.ecs.systems.MultiRenderSystem;
import io.github.ethanBostick.ecs.systems.NetworkFlowSystem;
import io.github.ethanBostick.ecs.systems.NetworkGrowthSystem;
import io.github.ethanBostick.ecs.systems.PlayerStateSystem;
import io.github.ethanBostick.ecs.systems.RenderSystem;
import io.github.ethanBostick.ecs.systems.RunnerSystem;
import io.github.ethanBostick.ecs.systems.VectorRenderSystem;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.InputMultiplexer;

//testing
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.Map;

public class GameScreen implements Screen {

	private GameHUD gameHUD = null;
	private InputMultiplexer multiplexer = null;
	private GameController gameController = null;
	public float tickRate = 0.5f;
	public PooledEngine engine;

	public GameScreen(){}

	@Override
	public void show() {
	//init Ashley ECS
		this.engine = new PooledEngine();
		EntityBuilder.instance(engine);
		Registry.init();
		this.engine.addSystem(new RenderSystem(0));
		this.engine.addSystem(new MultiRenderSystem(1));
		this.engine.addSystem(new VectorRenderSystem(99)); //99 so it renders on top

		this.engine.addSystem(new ActionSystem(2));
		this.engine.addSystem(new BuildSystem(3));
		this.engine.addSystem(new RunnerSystem(tickRate, 3));
		this.engine.addSystem(new NetworkGrowthSystem(tickRate*2, 4));

		this.engine.addSystem(new ExtractionSystem(tickRate*2,4));
		this.engine.addSystem(new CleansingSystem(tickRate*2,4));
		this.engine.addSystem(new FlowGradientSystem(tickRate,5));
		this.engine.addSystem(new NetworkFlowSystem(tickRate*2, 6));

		this.engine.addSystem(new PlayerStateSystem());
		this.engine.addSystem(new EntityManagementSystem(engine, 100));

		Map map = Map.instance();
		double[] concentrations = new double[] {
			0.35,//blank tile
			0.15,
			0.15,
			0.20,
			0.10,
			0.05
		};
		RegionType[] regions = new RegionType[]{
			RegionType.BLANK,
			RegionType.MOUNTAIN,
			RegionType.BARREN,
			RegionType.POLLUTED,
			RegionType.RADIOACTIVE,
			RegionType.RESTORED
		};

		map.initConfig(100, concentrations,regions,0.75 , 0, 67);
		map.initMap();

	//init Input and UI
		this.multiplexer = new InputMultiplexer();
		this.gameHUD = new GameHUD();
		this.gameController = new GameController(500, 500);
		this.multiplexer.addProcessor(this.gameHUD.stage); 
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
		this.gameHUD.update();
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
