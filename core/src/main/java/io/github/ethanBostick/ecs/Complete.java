package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Complete implements Component, Poolable{
    public boolean complete = false;

    //for depth mechanics
    public Complete(){}
    
	@Override
	public void reset(){
        this.complete = false;
	}

}
