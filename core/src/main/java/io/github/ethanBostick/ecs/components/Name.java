package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Name implements Component, Poolable{
    public String name;

    public Name(){}
    
	@Override
	public void reset(){
        this.name = null;
	}

}
