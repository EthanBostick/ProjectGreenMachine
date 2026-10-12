package io.github.ethanBostick.events;

import com.badlogic.gdx.utils.Pool.Poolable;

import io.github.ethanBostick.core.OverlayMap;

public class OverlayChangeEvent implements Event, Poolable{

	public final EventType type = EventType.OVERLAY_CHANGE;
    public OverlayMap selectedOverlay = null;

	public OverlayChangeEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
        this.selectedOverlay = null;
	}
}
