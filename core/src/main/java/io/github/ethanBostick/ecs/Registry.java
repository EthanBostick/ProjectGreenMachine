package io.github.ethanBostick.ecs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.ashley.core.Entity;


public class Registry {
    public static OrthographicCamera camera = null;
    public static SpriteBatch spriteBatch = null;
    public static Entity mouse = null;
    public static Entity selectTool = null;
    public static Entity dragTool = null;
    public static Entity player = null;

    public static void init(){
        camera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
        selectTool = EntityBuilder.instance().initSelectTool();
        mouse = EntityBuilder.instance().initMouse();
        dragTool = EntityBuilder.instance().initDragTool();
        player = EntityBuilder.instance().initPlayer();
    }

    public static void dispose(){
        camera = null;
		spriteBatch = null;
        selectTool = null;
        dragTool = null;
        mouse = null;
        player = null;
    }
}
