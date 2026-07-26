package io.github.ethanBostick.screens;

// UI
import io.github.ethanBostick.ui.GameHUD;

// Input
import io.github.ethanBostick.input.MapInputAdapter;
import io.github.ethanBostick.input.InputRouter;
//main ref
import io.github.ethanBostick.Main;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {

	private GameHUD gameHUD = null;
	private InputRouter inputRouter = null;

	public GameScreen(){}

	@Override
	public void show() {
		this.gameHUD = new GameHUD();
		this.inputRouter = new InputRouter(this.gameHUD.stage); //ui stage index 0
		this.inputRouter.addProcessor(new MapInputAdapter());

		Gdx.input.setInputProcessor(this.inputRouter.getMultiplexer());
	}

    @Override
    public void render(float delta) {
		//standard screen wipe
		Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.gameHUD.stage.act(delta);
		this.gameHUD.stage.draw();
    }
	@Override
    public void resize(int width, int height) {
        this.gameHUD.resize(width, height);
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
