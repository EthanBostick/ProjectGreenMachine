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

    public final Pool<DepthChangeEvent> depthChangeEventPool = new Pool<DepthChangeEvent>() {
        @Override
        protected DepthChangeEvent newObject() {
            return new DepthChangeEvent();
        }
    };

    public final Pool<ToolChangeEvent> toolChangeEventPool = new Pool<ToolChangeEvent>() {
        @Override
        protected ToolChangeEvent newObject() {
            return new ToolChangeEvent();
        }
    };

    public final Pool<TileSelectEvent> tileSelectEventPool = new Pool<TileSelectEvent>() {
        @Override
        protected TileSelectEvent newObject() {
            return new TileSelectEvent();
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
