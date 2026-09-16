package io.github.ethanBostick.screens;

// UI
import io.github.ethanBostick.ui.MenuHUD;
// Input
import io.github.ethanBostick.input.MenuInputAdapter;
import com.badlogic.gdx.InputMultiplexer;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;

public class MenuScreen implements Screen {

	private MenuHUD menuHUD;
    private InputMultiplexer multiplexer;

	public MenuScreen(){}

	@Override
	public void show() {
        //init Input and UI
		this.multiplexer = new InputMultiplexer();
		this.menuHUD = new MenuHUD();
		this.multiplexer.addProcessor(0,this.menuHUD.stage); 
		this.multiplexer.addProcessor(new MenuInputAdapter());
		Gdx.input.setInputProcessor(this.multiplexer);
	}

    @Override
    public void render(float delta) {
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
