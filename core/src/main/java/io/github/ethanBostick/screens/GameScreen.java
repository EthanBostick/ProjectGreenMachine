package io.github.ethanBostick.screens;

// UI
import io.github.ethanBostick.ui.GameHUD;

// Input
import io.github.ethanBostick.input.MapInputAdapter;
import io.github.ethanBostick.input.InputManager;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;

public class GameScreen implements Screen {

	private GameHUD gameHUD = null;
	private InputManager inputManager = null;

	public GameScreen(){}

	@Override
	public void show() {
		this.gameHUD = new GameHUD();
		this.inputManager = new InputManager(this.gameHUD.stage); //ui stage index 0
		this.inputManager.addProcessor(new MapInputAdapter());

		Gdx.input.setInputProcessor(this.inputManager.getMultiplexer());
	}

    @Override
    public void render(float delta) {
        this.inputManager.poll();
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
