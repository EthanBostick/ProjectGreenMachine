package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class VectorArrow implements Component, Poolable{
    public float startX = 0f;
    public float startY = 0f;
    public float endX = 0f;
    public float endY = 0f;
    public Texture arrowTexture = null;

    public VectorArrow(){}
    
	@Override
	public void reset(){
        this.startX = 0f;
        this.startY = 0f;
        this.endX = 0f;
        this.endY = 0f;
        this.arrowTexture = null;
	}

}
