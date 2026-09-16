package io.github.ethanBostick.ecs;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class ActionSystem extends IteratingSystem{

    public ActionSystem() {
        super(Family.all(MouseState.class, Position.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = Mappers.positionCMap.get(entity);
        MouseState state = Mappers.mouseStateCMap.get(entity);
        int q = position.q;
        int r = position.r;
    }
}

