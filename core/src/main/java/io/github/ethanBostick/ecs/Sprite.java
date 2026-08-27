package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;


public class Sprite implements Component, Poolable{
	public Texture texture;

	public Sprite(){
	}

	@Override
	public void reset(){
		texture = null;
	}
}
