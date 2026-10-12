package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Cultivator;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class CultivationSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(14);
    
    public CultivationSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Cultivator.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity cultivatorNode) {
        try{
            Position position = Mappers.positionCMap.get(cultivatorNode);
            Nutrients nutrients = Mappers.nutrientsCMap.get(cultivatorNode);
            Cultivator cultivator = Mappers.cultivatorCMap.get(cultivatorNode);

            //get radius vector
            int radius = cultivator.radius;
            this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);

            int usableMinerals = nutrients.minerals + Math.min(0, nutrients.mineralDelta);

            //only process if enough minerals to do whole area
            if(usableMinerals >= (int)((this.processingArray.size/2)*CommonValues.CULTIVATE_MINERAL_PER_TILE.value)){
                for (int i = 0; i < this.processingArray.size; i += 2) {
                    int q = this.processingArray.get(i);
                    int r = this.processingArray.get(i+1);

                    //continue if not restored tile
                    Entity surfaceNode = Map.instance().getEntityAt(
                        q, 
                        r, 
                        TilePosition.SURFACE);
                    if (surfaceNode == null) continue;
                    if (Mappers.regionCMap.get(surfaceNode).regionType != RegionType.RESTORED) continue;

                    //check for surface greenery
                    Entity greenNode = Map.instance().getEntityAt(
                        q,
                        r,
                        TilePosition.GREEN);
                    //if none, make one
                    if (greenNode == null){
                        nutrients.mineralDelta -= (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
                        Entity newGreenNode = EntityBuilder.instance().createGreen(q, r, 1);

                        Map.instance().setEntityAt(q, r, newGreenNode, TilePosition.GREEN);
                        EntityBuilder.instance().addToEngine(newGreenNode);
                    }
                    else{
                        Nutrients greenNutrients = Mappers.nutrientsCMap.get(greenNode);
                        NutrientCapacity greenNutrientCapacity = Mappers.nutrientCapacityCMap.get(greenNode);
                        int trueMineralAmount = greenNutrients.mineralDelta + greenNutrients.minerals;
                        int mineralRoom = Math.max(0, greenNutrientCapacity.mineralCapacity - trueMineralAmount);

                        int finalOutput = Math.min((int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value, mineralRoom);
                        nutrients.mineralDelta -= finalOutput;
                        greenNutrients.mineralDelta += finalOutput;
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("CleansingError: " + e.getMessage());
        }
        finally{
            this.processingArray.clear();
        }
    }
}
