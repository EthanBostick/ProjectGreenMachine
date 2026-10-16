package io.github.ethanBostick.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class NutrientFlow implements Component, Poolable {
    public PriorityRole mineralRole = PriorityRole.NONE; //default values
    public PriorityRole carbonRole = PriorityRole.NONE;
    public PriorityRole mineralRoleOverride = null; //overrides default
    public PriorityRole carbonRoleOverride = null;
    public int mineralDistance = Integer.MAX_VALUE;
    public int carbonDistance = Integer.MAX_VALUE;
    public int maxThroughput = 1;

    @Override
    public void reset() {
        this.mineralRole = PriorityRole.NONE;
        this.carbonRole = PriorityRole.NONE;
        this.mineralRoleOverride = null;
        this.carbonRoleOverride = null;
        this.mineralDistance = Integer.MAX_VALUE;
        this.carbonDistance = Integer.MAX_VALUE;
        this.maxThroughput = 1;
    }
}