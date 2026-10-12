package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Health implements Component, Poolable{
    public int health = 0;
    public float degenerationCarbonBuildup = 0f;
    public float degenerationMineralBuildup = 0f;

    public Health(){}
    
	@Override
	public void reset(){
        this.health = 0;
        this.degenerationCarbonBuildup = 0f;
        this.degenerationMineralBuildup = 0f;
	}

}