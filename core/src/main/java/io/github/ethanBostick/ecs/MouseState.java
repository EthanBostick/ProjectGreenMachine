package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;


public class MouseState implements Component, Poolable{
    public boolean pressedDown = false;
    public boolean pressedUp = false;
    public boolean heldDown = false;
    public int button = -1;

    public MouseState(){}
    
	@Override
	public void reset(){
        this.button = -1;
        this.pressedDown = false;
        this.pressedUp = false;
        this.heldDown = false;
	}

}
