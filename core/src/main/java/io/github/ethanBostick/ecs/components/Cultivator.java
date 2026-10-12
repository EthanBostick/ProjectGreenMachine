package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Cultivator implements Component, Poolable{

    public int radius = 0;

    public Cultivator(){}
    
	@Override
	public void reset(){
        radius = 0;
	}
}
