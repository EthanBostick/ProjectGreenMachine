package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class Child implements Component, Poolable{

    public Entity child;
    
    @Override
    public void reset(){
        child = null;
    }
}
