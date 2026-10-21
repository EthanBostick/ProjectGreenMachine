package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Build;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Child;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class BuildSystem extends IntervalIteratingSystem{

    public final float interval;

    public BuildSystem(float interval,int priority) {
        super(Family.all(Build.class).exclude(Dead.class).get(),interval, priority);
        this.interval = interval;
    }

    private void createTargetBuild(BuildType target, Position p){
        Entity creation = null;

        switch (target){
            case MYCELIUM_D1:
                creation = EntityBuilder.instance().createMycelium(p.q, p.r, 1, 0, 0,target.carbonCost/2,target.mineralCost/2);
                break;
            case MYCELIUM_D2:
                creation = EntityBuilder.instance().createMycelium(p.q, p.r, 2, 0, 0,target.carbonCost,target.mineralCost);
                break;
            case MYCELIUM_D3:
                creation = EntityBuilder.instance().createMycelium(p.q, p.r, 3, 0, 0,(target.carbonCost*3)/2,(target.mineralCost*3)/2);
                break;
            case EXTRACTOR:
                creation = EntityBuilder.instance().createMyceliumExtractor(p.q, p.r,0,0,target.carbonCost/2,target.mineralCost/2);
                break;
            case CLEANSER:
                creation = EntityBuilder.instance().createMyceliumCleanser(p.q, p.r,0,0,target.carbonCost/2,target.mineralCost/2);
                break;
            case CULTIVATOR:
                creation = EntityBuilder.instance().createMyceliumCultivator(p.q, p.r,0,0,target.carbonCost/2,target.mineralCost/2);
                break;
            case STORAGE:
                creation = EntityBuilder.instance().createMyceliumStorage(p.q, p.r,0,0,target.carbonCost/2,target.mineralCost/2);
                break;
        }

        Map.instance().setEntityAt(p.q, p.r, creation, TilePosition.MYCELIUM);
        EntityBuilder.instance().addToEngine(creation);

        Child child = Mappers.childCMap.get(creation);
        if(child != null){
            Map.instance().setEntityAt(p.q, p.r, child.child, TilePosition.SURFACE_MYCELIUM);
            EntityBuilder.instance().addToEngine(child.child);
        }
    }

    @Override
    protected void processEntity(Entity entity) {
        if (Mappers.deadCMap.get(entity) != null) return;

        Build targetBuild = Mappers.buildCMap.get(entity);
        Nutrients currentProgress = Mappers.nutrientsCMap.get(entity);
        NutrientCapacity nutrientsNeeded = Mappers.nutrientCapacityCMap.get(entity);
        Position position = Mappers.positionCMap.get(entity);

        if(currentProgress.carbons == nutrientsNeeded.carbonCapacity &&
        currentProgress.minerals == nutrientsNeeded.mineralCapacity){
            this.createTargetBuild(targetBuild.buildType, position);
            currentProgress.carbons -= nutrientsNeeded.carbonCapacity;
            currentProgress.minerals -= nutrientsNeeded.mineralCapacity;
            //schedule for reclaim
            EntityBuilder.instance().reclaim(entity);
        }
    }
}