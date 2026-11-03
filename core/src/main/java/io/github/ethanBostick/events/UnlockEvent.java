package io.github.ethanBostick.events;

import com.badlogic.gdx.utils.Pool.Poolable;

import io.github.ethanBostick.core.Adaptations;

public class UnlockEvent implements Event, Poolable{

	public final EventType type = EventType.UNLOCK;
	public Adaptations unlockTarget = null;

	public UnlockEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
		unlockTarget = null;
	}
}