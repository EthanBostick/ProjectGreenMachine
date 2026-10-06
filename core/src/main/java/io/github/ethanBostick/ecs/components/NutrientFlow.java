package io.github.ethanBostick.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class NutrientFlow implements Component, Poolable {
    public int mineralDistance = Integer.MAX_VALUE; //set to 0 for dire need / priority
    public int carbonDistance = Integer.MAX_VALUE;
    public boolean needsMinerals = false;
    public boolean needsCarbons = false;
    public boolean isStorageNode = false;
    public int maxThroughput = 1;

    @Override
    public void reset() {
        this.mineralDistance = Integer.MAX_VALUE;
        this.carbonDistance = Integer.MAX_VALUE;
        this.needsMinerals = false;
        this.needsCarbons = false;
        this.isStorageNode = false;
        this.maxThroughput = 1;
    }
}