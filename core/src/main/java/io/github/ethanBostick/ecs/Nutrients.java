package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Nutrients implements Component, Poolable{
    public int carbons = 0;
    public int minerals = 0;

    public Nutrients(){}
    
	@Override
	public void reset(){
        this.carbons = 0;
        this.minerals = 0;
	}
}