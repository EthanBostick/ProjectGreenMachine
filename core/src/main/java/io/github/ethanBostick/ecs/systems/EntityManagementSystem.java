package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.systems.IteratingSystem;

import io.github.ethanBostick.ecs.components.Dead;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;

public class EntityManagementSystem extends IteratingSystem{

    private PooledEngine theEngine;

    public EntityManagementSystem(PooledEngine engine, int priority) {
        //prio 99 so it goes last
        super(Family.all(Dead.class).get(), priority);
        this.theEngine = engine;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        this.theEngine.removeEntity(entity); //calls reset on all components and returns them to pool + removes entity
    }
}
