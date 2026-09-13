package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class MultiTiledSprite implements Component, Poolable{
	public Texture texture = null;
	public Texture textureHead = null;
    public IntArray points = null;
    public int layer = 0;

    public MultiTiledSprite(){}
    
	@Override
	public void reset(){
		this.layer = 0;
		this.texture = null;
		this.textureHead = null;
    	this.points = null;
	}

}
