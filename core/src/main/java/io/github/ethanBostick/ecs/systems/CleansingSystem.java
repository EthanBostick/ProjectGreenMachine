package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.ecs.components.Cleansing;
import io.github.ethanBostick.ecs.components.Mappers;
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
    private IntArray processingArray = new IntArray(12);
    
    public CleansingSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Cleansing.class).get(), interval, priority);
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
            if (radius <= 0){
                this.processingArray.add(position.q);
                this.processingArray.add(position.r);
            }
            else{
                this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);
            }

            //look for and proccess resource nodes in area
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

                //node cleansing
                if (nodeRegion.condition < 100){
                    uncleanTiles ++;
                    if (carbonCost >= nutrients.carbons + nutrients.carbonDelta || mineralCost >= nutrients.minerals + nutrients.mineralDelta) continue;
                    
                    nutrients.carbonDelta -= carbonCost; 
                    nutrients.mineralDelta -= mineralCost; 
                    nodeRegion.condition += Math.min(cleansingRate,100-nodeRegion.condition);                    
                }

                //node cleansed
                if (nodeRegion.condition >= 100){
                    nodeRegion.condition = 100;
                    nodeRegion.regionType = RegionType.RESTORED;
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture("restoredTile.png");
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
