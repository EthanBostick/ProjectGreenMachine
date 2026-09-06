package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class Biome implements Component, Poolable{
    public int temp = 0;
    public BiomeType biomeType = null;

    public Biome(){}
    
	@Override
	public void reset(){
        this.temp = 0;
        this.biomeType = null;
	}
}
