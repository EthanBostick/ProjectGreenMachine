package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Revealed implements Component, Poolable{
    public boolean revealed = false;

    public Revealed(){}
    
	@Override
	public void reset(){
        this.revealed = false;
	}

}
