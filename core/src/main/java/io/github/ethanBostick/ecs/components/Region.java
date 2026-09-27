package io.github.ethanBostick.ecs.components;

import io.github.ethanBostick.map.RegionType;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Region implements Component, Poolable{
    public RegionType regionType = null;

    public Region(){}
    
	@Override
	public void reset(){
        this.regionType = null;
	}
}
