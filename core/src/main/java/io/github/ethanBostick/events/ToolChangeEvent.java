package io.github.ethanBostick.events;

import io.github.ethanBostick.ecs.ToolType;

import com.badlogic.gdx.utils.Pool.Poolable;

public class ToolChangeEvent implements Event, Poolable{

	public final EventType type = EventType.TOOL_CHANGE;
    public ToolType tool = null;

	public ToolChangeEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
        this.tool = null;
	}
}

