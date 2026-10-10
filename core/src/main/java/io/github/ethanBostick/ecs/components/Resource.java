package io.github.ethanBostick.ecs.components;

import io.github.ethanBostick.map.ResourceType;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Resource implements Component, Poolable{
    public ResourceType resourceType = null;

    public Resource(){}
    
	@Override
	public void reset(){
        resourceType = null;
	}
}
