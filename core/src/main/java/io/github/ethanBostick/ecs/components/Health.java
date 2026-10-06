package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Health implements Component, Poolable{
    public int health = 0;

    public Health(){}
    
	@Override
	public void reset(){
        this.health = 0;
	}

}