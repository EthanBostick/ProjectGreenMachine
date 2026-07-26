package io.github.ethanBostick.events;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.Screen;

public class ScreenChangeEvent implements Event, Poolable{

	public final EventType type = EventType.SCREEN_CHANGE;
	public Screen targetScreen;	

	public ScreenChangeEvent(){}

	@Override
	public EventType getType(){
		return this.type;
	}

	@Override
	public void reset(){
		this.targetScreen = null;	
	}
}
