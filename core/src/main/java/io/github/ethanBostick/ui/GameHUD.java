package io.github.ethanBostick.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameHUD {
    // Expose the stage so your Main class can render it and pass it to the Multiplexer
    public Stage stage; 
    private Skin uiSkin;

    public GameHUD() {
        // 1. Initialize the Stage with a viewport locked to the screen
        stage = new Stage(new ScreenViewport());

        // 2. Load your styling (Requires a JSON file, texture atlas, and font in your assets)
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

        // 3. Create the layout Table and set it to fill the whole screen
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.bottom().right(); // Align contents to the bottom right of the screen

        // 4. Create your Actors (Widgets)
        TextButton buildButton = new TextButton("Click Me", uiSkin);

        // 5. Add Input Listeners to the Actors
        buildButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Cookie +1");
                // Trigger your ECS event here
            }
        });

        // 6. Assemble the Hierarchy: Add Button -> Table -> Stage
        // The pad(10) adds 10 pixels of margin around the button
        rootTable.add(buildButton).pad(10); 
        stage.addActor(rootTable);
    }
    
    // Call this in your Main resize() method to keep UI crisp
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Call this to clean up memory when closing the game
    public void dispose() {
        stage.dispose();
        uiSkin.dispose();
    }
}
