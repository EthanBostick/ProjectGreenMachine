package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Nutrients implements Component, Poolable{
    float carbons;
    float minerals;

    public Nutrients(){}
    
	@Override
	public void reset(){
        this.carbons = 0f;
        this.minerals = 0f;
	}

}