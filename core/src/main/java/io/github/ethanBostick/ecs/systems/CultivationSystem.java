package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Cleansing;
import io.github.ethanBostick.ecs.components.Cultivator;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Name;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Region;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

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
            NutrientFlow flow = Mappers.nutrientFlowCMap.get(cultivatorNode);

            //get radius vector
            int radius = cultivator.radius;
            this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);

            for (int i = 0; i < this.processingArray.size; i += 2) {
                //break if no minerals
                int q = this.processingArray.get(i);
                int r = this.processingArray.get(i+1);
                int usableMinerals = nutrients.minerals + Math.min(0, nutrients.mineralDelta);
                if(usableMinerals < CommonValues.CULTIVATE_MINERAL_PER_TILE.value) break;

                //continue if not restored tile
                Entity surfaceNode = Map.instance().getEntityAt(
                    q, 
                    r, 
                    TilePosition.SURFACE);
                if (surfaceNode == null || Mappers.regionCMap.get(surfaceNode).regionType != RegionType.RESTORED) continue;

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
                }
                else{
                    Nutrients greenNutrients = Mappers.nutrientsCMap.get(greenNode);
                    nutrients.mineralDelta -= (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
                    greenNutrients.mineralDelta += (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
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
