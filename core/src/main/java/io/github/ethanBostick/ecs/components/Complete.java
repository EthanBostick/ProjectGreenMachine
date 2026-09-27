package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Complete implements Component, Poolable{
    public boolean complete = false;

    public Complete(){}
    
	@Override
	public void reset(){
        this.complete = false;
	}

}
