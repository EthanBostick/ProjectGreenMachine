package io.github.ethanBostick.events;

import com.badlogic.gdx.utils.Pool.Poolable;

public class UnlockEvent implements Event, Poolable{

	public final EventType type = EventType.UNLOCK;

	public UnlockEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
	}
}