package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Vision implements Component, Poolable{
    public boolean vision = true;

    public Vision(){}
    
	@Override
	public void reset(){
        this.vision = true;
	}

}
