package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Direction implements Component, Poolable{

    public int[] directionVector = {0,0}; // q,r

    public Direction(){}
    
	@Override
	public void reset(){
        directionVector[0] = 0;
        directionVector[1] = 0;
	}

}
