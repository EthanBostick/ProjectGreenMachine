package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Build implements Component, Poolable{
    public BuildType buildType;

    public Build(){}
    
	@Override
	public void reset(){
        this.buildType = null;
	}
}
