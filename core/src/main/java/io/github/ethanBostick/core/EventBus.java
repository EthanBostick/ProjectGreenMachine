package io.github.ethanBostick.core;

import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.EventFactory;
import io.github.ethanBostick.events.ScreenChangeEvent;

//GDX stuff
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EventBus{

	private static EventBus theInstance = null;
	private ObjectMap<EventType, Array<Observer>> observers;

	private EventBus(){
		this.observers = new ObjectMap<>(2); //argue estimated size
	}

	public static EventBus instance(){
		if (EventBus.theInstance == null){
			EventBus.theInstance = new EventBus();
		}
		return EventBus.theInstance;
	}

	public void subscribe(EventType type, Observer observer){
		Array<Observer> listeners = this.observers.get(type); 

		if (listeners == null){
			listeners = new Array<>(false,1);
			this.observers.put(type,listeners);
		}

		listeners.add(observer);
	}

	public void publish(Event event){
		Array<Observer> listeners = this.observers.get(event.getType()); 

		if (listeners != null){
			for (Observer observer : listeners){
				observer.onEvent(event);
			}
		}

		if (event instanceof ScreenChangeEvent){
			EventFactory.instance().screenChangeEventPool.free((ScreenChangeEvent)event); //calls the events reset()
		}
	}
}
