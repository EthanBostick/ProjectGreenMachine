package io.github.ethanBostick.events;

import com.badlogic.gdx.utils.Pool.Poolable;

public class DepthChangeEvent implements Event, Poolable{

	public final EventType type = EventType.DEPTH_CHANGE;
    public int newDepthDir = 0;

	public DepthChangeEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
        this.newDepthDir = 0;
	}
}
