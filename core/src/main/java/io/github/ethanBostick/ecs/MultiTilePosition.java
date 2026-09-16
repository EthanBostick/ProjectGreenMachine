package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class MultiTilePosition implements Component, Poolable{
    public IntArray points = new IntArray();
    public int layer = 0;

    public MultiTilePosition(){}
    
	@Override
	public void reset(){
		this.layer = 0;
    	this.points.clear();
	}

}
