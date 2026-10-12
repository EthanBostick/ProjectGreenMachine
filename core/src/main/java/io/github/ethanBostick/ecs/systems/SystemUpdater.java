package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.OverlayChangeEvent;
import io.github.ethanBostick.events.ToolChangeEvent;
import io.github.ethanBostick.events.DepthChangeEvent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;

public class SystemUpdater extends EntitySystem implements Observer{
    public final PooledEngine theEngine;

    public SystemUpdater(PooledEngine engine, int priority) {
        super(priority);
		EventBus.instance().subscribe(EventType.OVERLAY_CHANGE, this);
        this.theEngine = engine;
    }

    @Override
    public void addedToEngine(Engine engine) {
        theEngine.getSystem(RegionOverlay.class).setProcessing(false);;
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

