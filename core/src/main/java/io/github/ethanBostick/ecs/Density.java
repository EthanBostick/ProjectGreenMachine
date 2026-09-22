package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Density implements Component, Poolable{
    public int density = 0;

    //for depth mechanics
    public Density(){}
    
	@Override
	public void reset(){
        this.density = 0;
	}

}