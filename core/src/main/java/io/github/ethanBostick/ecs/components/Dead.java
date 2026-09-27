package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Dead implements Component, Poolable{

    public Dead(){}
    
	@Override
	public void reset(){
	}

}
