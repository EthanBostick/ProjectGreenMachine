package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Reclaim implements Component, Poolable{

    public Reclaim(){}
    
	@Override
	public void reset(){
	}
}
