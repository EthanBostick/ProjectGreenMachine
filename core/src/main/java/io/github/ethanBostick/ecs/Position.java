package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Position implements Component, Poolable{
    public int x = 0;
    public int y = 0;
    public int z = 0;

	@Override
	public void reset(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
	}

}