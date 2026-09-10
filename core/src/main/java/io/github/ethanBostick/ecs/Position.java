package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Position implements Component, Poolable{
    public int q = 0;
    public int r = 0;
    public float x = 0;
    public float y = 0;
    public int layer = 0;

    public Position(){}
    
	@Override
	public void reset(){
        this.q = 0;
        this.r = 0;
        this.x = 0;
        this.y = 0;
        this.layer = 0;
	}

}