package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.ColorSettings;
import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Cultivator;
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
                int totalMineralOutput = 0;
                for (int i = 0; i < this.processingArray.size; i += 2) {
                    int q = this.processingArray.get(i);
                    int r = this.processingArray.get(i+1);

                    //continue if not restored tile
                    Entity surfaceNode = Map.instance().getEntityAt(
                        q, 
                        r, 
                        TilePosition.SURFACE);
                    if (surfaceNode == null) continue;
                    Region surfaceRegion = Mappers.regionCMap.get(surfaceNode);
                    if (surfaceRegion.regionType != RegionType.RESTORED) continue;

                    //check for surface greenery
                    Entity terrainNode = Map.instance().getEntityAt(
                        q,
                        r,
                        TilePosition.SURFACE_ENTITY);

                    if (terrainNode == null && surfaceRegion.condition <= 150 && surfaceRegion.condition >= 100){
                        nutrients.mineralDelta -= (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
                        totalMineralOutput += (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
                        surfaceRegion.condition += (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value * 5;

                        Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture("growingTile.png");
                    }
                    //if none, make one
                    else if (terrainNode == null && surfaceRegion.condition >= 150){
                        nutrients.mineralDelta -= (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
                        totalMineralOutput += (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
                        Mappers.spriteCMap.get(surfaceNode).texture = TextureUtils.pathToTexture("greenTile.png");
                        Entity newGreenNode = EntityBuilder.instance().createGreen(q, r, 1);

                        Map.instance().setEntityAt(q, r, newGreenNode, TilePosition.SURFACE_ENTITY);
                        EntityBuilder.instance().addToEngine(newGreenNode);
                    }
                    else if(Mappers.greenCMap.get(terrainNode)!=null){
                        Nutrients greenNutrients = Mappers.nutrientsCMap.get(terrainNode);
                        NutrientCapacity greenNutrientCapacity = Mappers.nutrientCapacityCMap.get(terrainNode);
                        int trueMineralAmount = greenNutrients.mineralDelta + greenNutrients.minerals;
                        int mineralRoom = Math.max(0, greenNutrientCapacity.mineralCapacity - trueMineralAmount);

                        int finalOutput = Math.min((int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value, mineralRoom);
                        nutrients.mineralDelta -= finalOutput;
                        greenNutrients.mineralDelta += finalOutput;
                        totalMineralOutput += (int)CommonValues.CULTIVATE_MINERAL_PER_TILE.value;
                    }
                }
                if (totalMineralOutput > 0) {
                    EntityBuilder.instance().createFloatingText(
                        position.q, 
                        position.r, 
                        1,
                        "-" + totalMineralOutput + "M", 
                        ColorSettings.MINERAL_LOSSES.color,
                        true
                    );
                }
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
