package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.core.Adaptations;
import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MutagenMass;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.UnlockEvent;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class UpgradeSystem extends EntitySystem implements Observer{

    public UpgradeSystem(int priority) {
        super(priority);
		EventBus.instance().subscribe(EventType.UNLOCK, this);
    }

    @Override
    public void addedToEngine(Engine engine) {
    }

    @Override
    public void update(float deltaTime) {
        // Executes every single frame.
    }

    @Override
    public void removedFromEngine(Engine engine) {
        // Executes when the system is removed or the engine is destroyed.
        EventBus.instance().unsubscribeAll(this);
    } 

    private boolean makePurchase(UnlockEvent unlockEvent){
        MutagenMass playerMutagen = Mappers.mutagenMassCMap.get(Registry.player);
        int mutagenCost = unlockEvent.unlockTarget.mutagenCost;

        //make purchase
        if(playerMutagen.amount >= mutagenCost){
            playerMutagen.amount -= mutagenCost;
            unlockEvent.unlockTarget.unlocked = true; //automatically toggles visablity for ui unlocks
        }
        //cannont make purchase: post err event or smth
        else{

        }

        return unlockEvent.unlockTarget.unlocked;
    }

    private void updateOnUnlock(UnlockEvent unlockEvent){
        Adaptations adaptation = unlockEvent.unlockTarget;

        switch(adaptation){
            case SURFACE_CONSTRUCTS:
                break;
            case EXTRACTOR_RANGE:
                CommonValues.EXTRACTION_RADIUS.value += 1;
                Adaptations.EXTRACTOR_RANGE.unlocked = true;
                break;
            case TRANSPORT_HYPHAE: 
                break;
            case HARSH_CHEMICALS:    
                Adaptations.HARSH_CHEMICALS.unlocked = true;
                break;
        }
    }

    @Override 
    public void onEvent(Event event){
		switch (event.getType()){
			case UNLOCK:
				UnlockEvent unlockEvent = (UnlockEvent) event;
                if (makePurchase(unlockEvent)){
                    updateOnUnlock(unlockEvent);
                }
                break;
			default:
				System.out.println("unknown event");
		}
    }
}