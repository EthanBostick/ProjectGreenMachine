package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Nutrients implements Component, Poolable{
    float carbon;
    float phosphorus;
    float nitrogen;

    public Nutrients(){}
    
	@Override
	public void reset(){
        this.carbon = 0f;
        this.phosphorus = 0f;
        this.nitrogen = 0f;
	}

}