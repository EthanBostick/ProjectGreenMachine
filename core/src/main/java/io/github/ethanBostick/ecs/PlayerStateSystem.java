package io.github.ethanBostick.ecs;


import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.ToolChangeEvent;
import io.github.ethanBostick.events.DepthChangeEvent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class PlayerStateSystem extends EntitySystem implements Observer{
    private final Entity player;

    public PlayerStateSystem() {
        // Optional: Set system priority. Lower numbers execute first.
        super(0);
        this.player = Registry.player;
		EventBus.instance().subscribe(EventType.DEPTH_CHANGE,this);
		EventBus.instance().subscribe(EventType.TOOL_CHANGE,this);
    }

    @Override
    public void addedToEngine(Engine engine) {
    }

    @Override
    public void update(float deltaTime) {
        // Executes every single frame.
        // Unlike IteratingSystem, you must manually fetch and process data here.
    }

    @Override
    public void removedFromEngine(Engine engine) {
        // Executes when the system is removed or the engine is destroyed.
        // Crucial for unsubscribing from Event Buses to prevent memory leaks.
    } 

    @Override 
    public void onEvent(Event event){
		switch (event.getType()){
			case DEPTH_CHANGE:
				DepthChangeEvent depthEvent = (DepthChangeEvent) event;
                Mappers.depthCMap.get(this.player).depth =  (Mappers.depthCMap.get(this.player).depth + depthEvent.newDepthDir) %2;
				break;
			case TOOL_CHANGE:
				ToolChangeEvent toolEvent = (ToolChangeEvent) event;
                //if that is the current active tool swap it to default (toggle on/off)
                Mappers.activeToolCMap.get(this.player).tool = (Mappers.activeToolCMap.get(this.player).tool == toolEvent.tool)? ToolType.DEFAULT : toolEvent.tool;
				break;
			case OVERLAY_CHANGE:
                break;
			default:
				System.out.println("unknown event");
		}
    }
}
