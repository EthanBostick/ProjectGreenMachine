package io.github.ethanBostick.events;

//libGDX
import com.badlogic.gdx.utils.Pool;

public class EventFactory {
	private static EventFactory theInstance = null;

    public final Pool<ScreenChangeEvent> screenChangeEventPool = new Pool<ScreenChangeEvent>() {
        @Override
        protected ScreenChangeEvent newObject() {
            return new ScreenChangeEvent();
        }
    };

	private EventFactory(){}

	public static EventFactory instance(){
		if (EventFactory.theInstance == null){
			EventFactory.theInstance = new EventFactory();
		}
		return EventFactory.theInstance;
	}
}
