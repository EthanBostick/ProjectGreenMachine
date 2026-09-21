package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class NutrientCapacity implements Component, Poolable{
    public int carbonCapacity = 0;
    public int mineralCapacity = 0;

    public NutrientCapacity(){}
    
	@Override
	public void reset(){
        this.carbonCapacity = 0;
        this.mineralCapacity = 0;
	}
}
