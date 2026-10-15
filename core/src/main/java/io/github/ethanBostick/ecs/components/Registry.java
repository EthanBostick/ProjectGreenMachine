package io.github.ethanBostick.ecs.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.ethanBostick.core.EntityBuilder;


public class Registry {
    public static OrthographicCamera camera = null;
    public static SpriteBatch spriteBatch = null;
    public static Entity mouse = null;
    public static Entity highlighter = null;
    public static Entity player = null;
    public static Skin uiSkin = null;
    //public static BitmapFont worldFont = null;

    public static void init(){
        camera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
        //worldFont = new BitmapFont(Gdx.files.internal("default.fnt"));
        highlighter = EntityBuilder.instance().initHighlighter();
        mouse = EntityBuilder.instance().initMouse();
        player = EntityBuilder.instance().initPlayer();
    }

    public static void dispose(){
        //worldFont = null; 
        uiSkin = null;
        camera = null;
		spriteBatch = null;
        highlighter = null;
        mouse = null;
        player = null;
    }
}
