package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Cleansing implements Component, Poolable{
    public int radius = 0;
    public int mineralCost = 1;
    public int carbonCost = 1; //per tick
    public float rate = 1f; //cleanse progress per tick

    public Cleansing(){}
    
	@Override
	public void reset(){
        this.radius = 0;
        this.rate = 1f;
        this.mineralCost = 1;
        this.carbonCost = 1;
	}

}