package io.github.ethanBostick.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

import io.github.ethanBostick.map.RegionType;

public class FactoryEmitter implements Component, Poolable {
    public int maxSpreadRadius = 4;
    public int ticksUntilNextPulse = 10;
    public int pulseIntervalTicks = 10;
    
    public int cleanseProgress = 0;
    public int cleanseRequirement = 100; //mineral or carbon requirement DD-2
    public boolean isNeutralized = false;
    public RegionType hazardType = null;

    @Override
    public void reset() {
        maxSpreadRadius = 4;
        ticksUntilNextPulse = 10;
        pulseIntervalTicks = 10;
        cleanseProgress = 0;
        cleanseRequirement = 100;
        isNeutralized = false;
        hazardType = null;
    }
}