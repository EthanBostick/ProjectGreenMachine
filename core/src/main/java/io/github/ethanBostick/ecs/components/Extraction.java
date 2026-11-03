package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Extraction implements Component, Poolable{
    public int carbonRate = 5;
    public int mineralRate = 2;

    public Extraction(){}
    
	@Override
	public void reset(){
        this.carbonRate = 5;
        this.mineralRate = 2;
	}

}