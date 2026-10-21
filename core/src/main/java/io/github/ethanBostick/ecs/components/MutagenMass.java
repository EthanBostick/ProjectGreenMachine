package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class MutagenMass implements Component, Poolable{
    public int amount = 0;

	@Override
	public void reset(){
        this.amount = 0;
	}
}
