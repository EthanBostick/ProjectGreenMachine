package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class NutrientDraw implements Component, Poolable{
    public int carbonDraw = 0;
    public int mineralDraw = 0;

    public NutrientDraw(){}
    
	@Override
	public void reset(){
        this.carbonDraw = 0;
        this.mineralDraw = 0;
	}
}

