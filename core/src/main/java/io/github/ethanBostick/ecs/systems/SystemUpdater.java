package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.OverlayChangeEvent;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class SystemUpdater extends EntitySystem implements Observer{

    public SystemUpdater(int priority) {
        super(priority);
		EventBus.instance().subscribe(EventType.OVERLAY_CHANGE, this);
    }

    @Override
    public void addedToEngine(Engine engine) {
        getEngine().getSystem(RegionOverlay.class).setProcessing(false);;
    }

    @Override
    public void update(float deltaTime) {
        // Executes every single frame.
    }

    @Override
    public void removedFromEngine(Engine engine) {
        // Executes when the system is removed or the engine is destroyed.
        // Crucial for unsubscribing from Event Buses to prevent memory leaks.
    } 

    private void switchToOverlay(OverlayChangeEvent event){
        Engine theEngine = this.getEngine(); //PooledEngine extends Engine, this is a pooled engine
        theEngine.getSystem(RegionOverlay.class).setProcessing(false);;

        switch(event.selectedOverlay){
            case NONE:
                theEngine.getSystem(RegionOverlay.class).setProcessing(false);;
                break;
            case REGION:
                theEngine.getSystem(RegionOverlay.class).setProcessing(true);;
                break;
            case GREEN:
                break;
        }
    }

    @Override 
    public void onEvent(Event event){
		switch (event.getType()){
			case OVERLAY_CHANGE:
				OverlayChangeEvent overlayEvent = (OverlayChangeEvent) event;
                switchToOverlay(overlayEvent);
                break;
			default:
				System.out.println("unknown event");
		}
    }
}

