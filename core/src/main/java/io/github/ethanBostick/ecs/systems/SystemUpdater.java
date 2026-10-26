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
        hideAll();
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

    private void hideAll(){
        Engine theEngine = this.getEngine();
        theEngine.getSystem(RegionOverlay.class).setProcessing(false);
        theEngine.getSystem(GreenOverlay.class).setProcessing(false);
        theEngine.getSystem(PriorityOverlay.class).setProcessing(false);
    }

    private void switchToOverlay(OverlayChangeEvent event){
        Engine theEngine = this.getEngine(); //PooledEngine extends Engine, this is a pooled engine
        hideAll();

        switch(event.selectedOverlay){
            case NONE:
                hideAll();
                break;
            case REGION:
                theEngine.getSystem(RegionOverlay.class).setProcessing(true);;
                break;
            case GREEN:
                theEngine.getSystem(GreenOverlay.class).setProcessing(true);;
                break;
            case PRIORITY:
                theEngine.getSystem(PriorityOverlay.class).setProcessing(true);;
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

