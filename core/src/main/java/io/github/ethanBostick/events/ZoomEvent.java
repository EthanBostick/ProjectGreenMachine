package io.github.ethanBostick.events;

import com.badlogic.gdx.utils.Pool.Poolable;

public class ZoomEvent implements Event, Poolable{
	public final EventType type = EventType.ZOOM;
    public float amount;

	public ZoomEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
        this.amount = 0;
	}
}
