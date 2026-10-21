package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Depth;
import io.github.ethanBostick.ecs.components.FactoryEmitter;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MutagenMass;
import io.github.ethanBostick.ecs.components.Position;

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
                MutagenMass factoryMutagens = Mappers.mutagenMassCMap.get(factory);
                Mappers.mutagenMassCMap.get(Registry.player).amount += factoryMutagens.amount;
                factoryMutagens.amount = 0;

                //blow up the factory or smth
                Map.instance().setEntityAt(factoryPos.q, factoryPos.r, null, TilePosition.SURFACE_ENTITY);
                EntityBuilder.instance().dead(factory);
                return;
            }

            emitter.ticksUntilNextPulse = (emitter.ticksUntilNextPulse + 1) % emitter.pulseIntervalTicks;
            if(emitter.ticksUntilNextPulse == 0){
                Entity wave = EntityBuilder.instance().createToxicWave(factoryPos.q, factoryPos.r, emitter.hazardType, emitter.maxSpreadRadius, depth.depth);
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
