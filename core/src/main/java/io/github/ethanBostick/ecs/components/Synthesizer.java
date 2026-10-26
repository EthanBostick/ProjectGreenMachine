package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;

import io.github.ethanBostick.map.ResourceType;

import com.badlogic.ashley.core.Component;

public class Synthesizer implements Component, Poolable{
	public ResourceType resourceType = null;
	public float resourceBuildUp = 0;

	@Override
	public void reset(){
		resourceType = null;
		resourceBuildUp = 0;
	}
}