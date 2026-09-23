package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Extraction implements Component, Poolable{
    public int radius = 0;
    public int carbonRate = 5;
    public int mineralRate = 1;

    public Extraction(){}
    
	@Override
	public void reset(){
        this.radius = 0;
        this.carbonRate = 5;
        this.mineralRate = 1;
	}

}