package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.ColorSettings;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Cleanser;
import io.github.ethanBostick.ecs.components.Dead;
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

public class CleanserSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(14);
    
    public CleanserSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Cleanser.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity cleanserNode) {
        try{
            Position position = Mappers.positionCMap.get(cleanserNode);
            Nutrients nutrients = Mappers.nutrientsCMap.get(cleanserNode);
            Cleanser cleanser = Mappers.cleanserCMap.get(cleanserNode);

            //get radius vector
            int radius = cleanser.radius;
            this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);

            int totalMineralOutput = 0;
            int totalCarbonOutput = 0;

            for (int i = 0; i < this.processingArray.size; i += 2) {
                Entity surfaceNode = Map.instance().getEntityAt(
                    this.processingArray.get(i), 
                    this.processingArray.get(i + 1), 
                    TilePosition.SURFACE);
                if (surfaceNode == null) continue;

                int carbonCost = cleanser.carbonCost;
                int mineralCost = cleanser.mineralCost;
                Region nodeRegion = Mappers.regionCMap.get(surfaceNode);

                //already cleansed return
                if (nodeRegion.regionType.cleanserScalar() <= 0) continue;

                float cleanserRate = cleanser.rate * nodeRegion.regionType.cleanserScalar();
                int usableMinerals = nutrients.minerals + Math.min(0, nutrients.mineralDelta);
                int usableCarbons = nutrients.carbons + Math.min(0, nutrients.carbonDelta);

                //node cleanser
                if (nodeRegion.condition < 100){
                    if (carbonCost >= usableCarbons || mineralCost >= usableMinerals) continue;
                    
                    nutrients.carbonDelta -= carbonCost; 
                    nutrients.mineralDelta -= mineralCost; 
                    totalCarbonOutput += carbonCost;
                    totalMineralOutput += mineralCost;
                    nodeRegion.condition += Math.min(cleanserRate,100-nodeRegion.condition);                    
                }

                //node cleansed
                if (nodeRegion.condition >= 100){
                    nodeRegion.condition = 100;
                    nodeRegion.regionType = RegionType.RESTORED;
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture("restoredTile.png");
                }
                //tile sprite updates
                else if(nodeRegion.condition >= 75){
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture(nodeRegion.regionType.name+"TileR3.png");
                }
                else if(nodeRegion.condition >= 50){
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture(nodeRegion.regionType.name+"TileR2.png");
                }
                else if(nodeRegion.condition >= 25){
                    Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture(nodeRegion.regionType.name+"TileR1.png");
                }
            }

            if (totalMineralOutput > 0){
                EntityBuilder.instance().createFloatingText(
                    position.q, 
                    position.r, 
                    1,
                    "-"+totalMineralOutput+"M", 
                    ColorSettings.MINERAL_LOSSES.color,
                    true
                );
            }
            if(totalCarbonOutput > 0) {
                EntityBuilder.instance().createFloatingText(
                    position.q, 
                    position.r, 
                    1,
                    "-"+totalCarbonOutput+"C", 
                    ColorSettings.CARBON_LOSSES.color,
                    true
                );
            }
        }
        catch(Exception e){
            System.out.println("CleanserError: " + e.getMessage());
        }
        finally{
            this.processingArray.clear();
        }
    }
}
