package io.github.ethanBostick.screens;

// UI
import io.github.ethanBostick.ui.MenuHUD;
// Input
import io.github.ethanBostick.input.MenuInputAdapter;
import io.github.ethanBostick.input.InputRouter;
//main
import io.github.ethanBostick.Main;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MenuScreen implements Screen {

	private final Main theGame;

	private MenuHUD menuHUD;
	private InputRouter inputRouter;

	public MenuScreen(Main game){
		this.theGame = game;
	}

	@Override
	public void show() {
		this.menuHUD = new MenuHUD(this.theGame);
		this.inputRouter = new InputRouter(this.menuHUD.stage); //ui stage index 0
		this.inputRouter.addProcessor(new MenuInputAdapter());

		Gdx.input.setInputProcessor(this.inputRouter.getMultiplexer());
	}

    @Override
    public void render(float delta) {
		//standard screen wipe
		Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.menuHUD.stage.act(delta);
		this.menuHUD.stage.draw();
    }
	@Override
    public void resize(int width, int height) {
        this.menuHUD.resize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

	//called when a new screen is being switched to AND for temp overlays
	@Override
    public void hide() { 
        // The screen is being swapped out. Unhook the inputs!
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
		this.menuHUD.dispose();	
    }
}
