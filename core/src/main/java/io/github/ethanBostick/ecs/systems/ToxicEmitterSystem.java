package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Depth;
import io.github.ethanBostick.ecs.components.FactoryEmitter;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.utils.HexUtils;

public class ToxicEmitterSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    
    public ToxicEmitterSystem(float interval, int priority) {
        super(Family.all( FactoryEmitter.class, Position.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity factory) {
        try{
            FactoryEmitter emitter = Mappers.factoryEmitterCMap.get(factory);
            Position factoryPos = Mappers.positionCMap.get(factory);
            Depth depth = Mappers.depthCMap.get(factory);
            if(emitter.isNeutralized){
                //unlock something based on neutralized factory
                return;
            }

            emitter.ticksUntilNextPulse = (emitter.ticksUntilNextPulse + 1) % emitter.pulseIntervalTicks;
            if(emitter.ticksUntilNextPulse == 0){
                Entity wave = EntityBuilder.instance().createToxicWave(factoryPos.q, factoryPos.r, emitter.maxSpreadRadius, depth.depth);
                EntityBuilder.instance().addToEngine(wave);
            }
        }
        catch(Exception e){
            System.out.println("ToxicEmitterError: " + e.getMessage());
        }
        finally{
        }
    }
}
