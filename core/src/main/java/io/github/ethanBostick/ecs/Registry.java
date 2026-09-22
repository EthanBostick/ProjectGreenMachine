package io.github.ethanBostick.ecs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.ashley.core.Entity;


public class Registry {
    public static OrthographicCamera camera = null;
    public static SpriteBatch spriteBatch = null;
    public static Entity mouse = null;
    public static Entity highlighter = null;
    public static Entity player = null;

    public static void init(){
        camera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
        highlighter = EntityBuilder.instance().initHighlighter();
        mouse = EntityBuilder.instance().initMouse();
        player = EntityBuilder.instance().initPlayer();
    }

    public static void dispose(){
        camera = null;
		spriteBatch = null;
        highlighter = null;
        mouse = null;
        player = null;
    }
}
