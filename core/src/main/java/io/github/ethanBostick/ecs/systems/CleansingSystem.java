package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.ecs.components.Cleansing;
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

public class CleansingSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(14);
    
    public CleansingSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Cleansing.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity cleanser) {
        try{
            Position position = Mappers.positionCMap.get(cleanser);
            Nutrients nutrients = Mappers.nutrientsCMap.get(cleanser);
            Cleansing cleansing = Mappers.cleansingCMap.get(cleanser);
            NutrientFlow flow = Mappers.nutrientFlowCMap.get(cleanser);
            int uncleanTiles = 0;

            //get radius vector
            int radius = cleansing.radius;
            this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);

            for (int i = 0; i < this.processingArray.size; i += 2) {
                Entity surfaceNode = Map.instance().getEntityAt(
                    this.processingArray.get(i), 
                    this.processingArray.get(i + 1), 
                    TilePosition.SURFACE);
                if (surfaceNode == null) continue;

                int carbonCost = cleansing.carbonCost;
                int mineralCost = cleansing.mineralCost;
                Region nodeRegion = Mappers.regionCMap.get(surfaceNode);

                //already cleansed return
                if (nodeRegion.regionType.cleansingScalar() <= 0) continue;

                float cleansingRate = cleansing.rate * nodeRegion.regionType.cleansingScalar();
                int usableMinerals = nutrients.minerals + Math.min(0, nutrients.mineralDelta);
                int usableCarbons = nutrients.carbons + Math.min(0, nutrients.carbonDelta);

                //node cleansing
                if (nodeRegion.condition < 100){
                    uncleanTiles ++;
                    if (carbonCost >= usableCarbons || mineralCost >= usableMinerals) continue;
                    
                    nutrients.carbonDelta -= carbonCost; 
                    nutrients.mineralDelta -= mineralCost; 
                    nodeRegion.condition += Math.min(cleansingRate,100-nodeRegion.condition);                    
                }

                //node cleansed
                Name tileName = Mappers.nameCMap.get(surfaceNode);
                if (nodeRegion.condition >= 100){
                    nodeRegion.condition = 100;
                    nodeRegion.regionType = RegionType.RESTORED;
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture("restoredTile.png");
                }
                //tile sprite updates
                else if(tileName == null){
                    continue;
                }
                else if(nodeRegion.condition >= 75){
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture(tileName.name+"R3.png");
                }
                else if(nodeRegion.condition >= 50){
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture(tileName.name+"R2.png");
                }
                else if(nodeRegion.condition >= 25){
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture(tileName.name+"R1.png");
                }
            }

            if(uncleanTiles == 0){
                flow.needsCarbons = false;
                flow.needsMinerals = false;
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
