package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.systems.IteratingSystem;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Build;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class BuildSystem extends IteratingSystem{

    public BuildSystem(int priority) {
        super(Family.all(Build.class).get(), priority);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    private Entity createTargetBuild(BuildType target, Position p){
        switch (target){
            case MYCELIUM_D1:
                return EntityBuilder.instance().createMycelium(p.q, p.r, 1, 0, 0);
            case MYCELIUM_D2:
                return EntityBuilder.instance().createMycelium(p.q, p.r, 2, 0, 0);
            case MYCELIUM_D3:
                return EntityBuilder.instance().createMycelium(p.q, p.r, 3, 0, 0);
            case EXTRACTOR:
                return EntityBuilder.instance().createMyceliumExtractor(p.q, p.r,0,0);
        }
        return null;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (Mappers.deadCMap.get(entity) != null) return;

        Build targetBuild = Mappers.buildCMap.get(entity);
        Nutrients currentProgress = Mappers.nutrientsCMap.get(entity);
        NutrientCapacity nutrientsNeeded = Mappers.nutrientCapacityCMap.get(entity);
        Position position = Mappers.positionCMap.get(entity);

        if(currentProgress.carbons == nutrientsNeeded.carbonCapacity &&
        currentProgress.minerals == nutrientsNeeded.mineralCapacity){
            Entity creation = this.createTargetBuild(targetBuild.buildType, position);
            Map.instance().setEntityAt(position.q, position.r, creation, TilePosition.MYCELIUM);
            EntityBuilder.instance().addToEngine(creation);
            //schedule for cleaning
            EntityBuilder.instance().addDead(entity);
        }
    }
}