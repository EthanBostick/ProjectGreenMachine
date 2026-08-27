package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Position implements Component, Poolable{
    public int q = 0;
    public int r = 0;

    public Position(){}
    
	@Override
	public void reset(){
        this.q = 0;
        this.r = 0;
	}

}