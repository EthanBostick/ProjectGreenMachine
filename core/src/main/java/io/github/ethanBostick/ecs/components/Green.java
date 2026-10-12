package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Green implements Component, Poolable{

    public float mineralUsage = 0;
    public float carbonOutput = 0; //max output per hex in radius

    public Green(){}
    
	@Override
	public void reset(){
        mineralUsage = 0;
        carbonOutput = 0;
	}

}
