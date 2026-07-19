package io.github.ethanBostick;

// Screens
import io.github.ethanBostick.screens.GameScreen;
import io.github.ethanBostick.screens.MenuScreen;

//GDX stuff
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.ashley.core.Engine;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

	public AssetManager assetManager;
	public Engine engine;
	//Ashley ECS will go here
	//public Engine engine;

    @Override
    public void create() {
		//init asset manager
		this.assetManager = new AssetManager();

		//load global textures/fonts if needed

		//init Ashley ECS
		this.engine = new Engine();

		//init screen
		this.setScreen(new MenuScreen(this)); //initialized with the Game
    }

    @Override
    public void render() {

		super.render(); //delegates rendering to active screen
    }

    @Override
    public void dispose() {
		//big clean everything
		if (screen != null) screen.dispose();
		assetManager.dispose();
    }
}

