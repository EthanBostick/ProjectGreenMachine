package io.github.ethanBostick;

import io.github.ethanBostick.core.Observer;
//events
import io.github.ethanBostick.events.ScreenChangeEvent;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventType;

// init Screen
import io.github.ethanBostick.screens.MenuScreen;

//GDX stuff
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game implements Observer {

	public AssetManager assetManager;

    @Override
    public void create() {
		//init asset manager
		this.assetManager = new AssetManager();

		//sub to Events
		EventBus.instance().subscribe(EventType.SCREEN_CHANGE,this);

		//load global textures/fonts if needed


		//init screen
		this.setScreen(new MenuScreen()); //initialized ui
    }

    @Override
    public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		//standard screen wipe
		Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//dont render entities until on the gamescreen
		if (!(this.getScreen() instanceof MenuScreen)){
		}

		super.render(); //delegates rendering to active screen
    }

    @Override
    public void dispose() {
		//big clean everything
		if (screen != null) screen.dispose();
		assetManager.dispose();
    }

	@Override
	public void onEvent(Event event){ //note w/poolable never store ref to event

		switch (event.getType()){
			case SCREEN_CHANGE:
				Screen oldScreen = this.getScreen();
				ScreenChangeEvent sce = (ScreenChangeEvent) event;
				this.setScreen(sce.targetScreen); 

				if (oldScreen != null) {
					oldScreen.dispose();
				}
				break;
			default:
				System.out.println("unknown event");
		}
	}
}

