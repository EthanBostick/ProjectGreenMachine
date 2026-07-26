package io.github.ethanBostick.core;

import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.ScreenChangeEvent;

//GDX stuff
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class EventBus{

	private static EventBus theInstance = null;
	private ObjectMap<EventType, Array<Observer>> observers;

	private EventBus(){
		this.observers = new ObjectMap<>(2); //argue estimated size

		//define the event pool for screenChangedEvents
		Pool<ScreenChangeEvent> screenEventPool = new Pool<ScreenChangeEvent>() {
			@Override
			protected ScreenChangeEvent newObject() {
				return new ScreenChangeEvent();
			}
		};
		Pools.set(ScreenChangeEvent.class, screenEventPool);
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

		if (event instanceof Poolable){
			Pools.free(event); //calls the events reset()
		}
	}
}
