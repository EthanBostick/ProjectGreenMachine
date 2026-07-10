package io.github.ethanBostick.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.ethanBostick.Main;

public class GameWorldScreen implements Screen {

	private final Main theGame;
	private Stage uiStage;
	private InputMultiplexer multiplexer;

	public GameWorldScreen(Main game){
		this.theGame = game;
	}
}
