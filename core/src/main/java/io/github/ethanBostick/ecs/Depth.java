package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Depth implements Component, Poolable{
    public int depth = 0;

    //for depth mechanics
    public Depth(){}
    
	@Override
	public void reset(){
        this.depth = 0;
	}

}
