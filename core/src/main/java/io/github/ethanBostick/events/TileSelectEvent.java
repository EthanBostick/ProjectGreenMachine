package io.github.ethanBostick.events;

import com.badlogic.gdx.utils.Pool.Poolable;

import io.github.ethanBostick.ecs.components.Position;

public class TileSelectEvent implements Event, Poolable{

	public final EventType type = EventType.TILE_SELECTED;
    public Position position = null;

	public TileSelectEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
        this.position = null;
	}
}
