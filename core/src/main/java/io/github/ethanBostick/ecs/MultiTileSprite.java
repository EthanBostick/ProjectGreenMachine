package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class MultiTileSprite implements Component, Poolable{
	public Array<Texture> texture = null;
	public Texture textureHead = null;

    public MultiTileSprite(){}
    
	@Override
	public void reset(){
		this.texture = null;
		this.textureHead = null;
	}

}
