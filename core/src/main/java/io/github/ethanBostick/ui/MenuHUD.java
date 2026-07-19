package io.github.ethanBostick.ui;

//main game ref
import io.github.ethanBostick.Main;

//other screen
import io.github.ethanBostick.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Screen;

public class MenuHUD {
    // Expose the stage so your Main class can render it and pass it to the Multiplexer
    public Stage stage; 
    private Skin uiSkin;
	private final Main theGame;

    public MenuHUD(Main theGame) {
		//ref to main
		this.theGame = theGame;

        // 1. Initialize the Stage with a viewport locked to the screen
        stage = new Stage(new ScreenViewport());

        // 2. Load your styling (Requires a JSON file, texture atlas, and font in your assets)
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

        // 3. Create the layout Table and set it to fill the whole screen
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom().right(); // Align contents to the bottom right of the screen

        // 4. Create your Actors (Widgets)
        TextButton startButton = new TextButton("Start", uiSkin);

        // 5. Add Input Listeners to the Actors
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
				Screen oldScreen = theGame.getScreen();
				theGame.setScreen(new GameScreen(theGame));

				if (oldScreen != null) {
					oldScreen.dispose();
				}
            }
        });

        // 6. Assemble the Hierarchy: Add Button -> Table -> Stage
        rootTable.add(startButton).colspan(1).pad(100); 
        stage.addActor(rootTable);
    }
    
    // Call this in your Main resize() method to keep UI crisp
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        uiSkin.dispose();
    }
}
