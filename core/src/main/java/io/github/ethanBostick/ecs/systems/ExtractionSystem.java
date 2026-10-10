package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Extraction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Resource;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.ResourceType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

public class ExtractionSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(12);
    
    public ExtractionSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Extraction.class)
        .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity extractor) {
        try{
            Position position = Mappers.positionCMap.get(extractor);
            Nutrients nutrients = Mappers.nutrientsCMap.get(extractor);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(extractor);
            Extraction extraction = Mappers.extractionCMap.get(extractor);

            //get radius vector
            int radius = extraction.radius;
            if (radius <= 0){
                this.processingArray.add(position.q);
                this.processingArray.add(position.r);
            }
            else{
                this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);
            }

            int currentCarbon = nutrients.carbons + nutrients.carbonDelta;
            int currentMineral = nutrients.minerals + nutrients.mineralDelta;
            //look for and proccess resource nodes in area
            for (int i = 0; i < this.processingArray.size; i += 2) {
                Entity resourceNode = Map.instance().getEntityAt(
                    this.processingArray.get(i), 
                    this.processingArray.get(i + 1), 
                    TilePosition.RESOURCE_NODE);
                if (resourceNode == null) continue;

                Nutrients nodeNutrients = Mappers.nutrientsCMap.get(resourceNode);
                Sprite nodeSprite = Mappers.spriteCMap.get(resourceNode);

                // Carbon extraction
                int nodeCarbonsAvail = Math.max(0, nodeNutrients.carbons + nodeNutrients.carbonDelta);
                int extractorCarbonRoom = Math.max(0, nutrientCapacity.carbonCapacity - currentCarbon);

                if (nodeCarbonsAvail > 0 && extractorCarbonRoom > 0) {
                    int desiredExtract = Math.min(extraction.carbonRate, extractorCarbonRoom);
                    int finalExtract = Math.min(desiredExtract, nodeCarbonsAvail);

                    nodeNutrients.carbonDelta -= finalExtract;
                    nutrients.carbonDelta += finalExtract;
                    currentCarbon += finalExtract; // update local counter for subsequent nodes in loop
                }

                // Mineral extraction
                int nodeMineralsAvail = Math.max(0, nodeNutrients.minerals + nodeNutrients.mineralDelta);
                int extractorMineralRoom = Math.max(0, nutrientCapacity.mineralCapacity - currentMineral);

                if (nodeMineralsAvail > 0 && extractorMineralRoom > 0) {
                    int desiredExtract = Math.min(extraction.mineralRate, extractorMineralRoom);
                    int finalExtract = Math.min(desiredExtract, nodeMineralsAvail);

                    nodeNutrients.mineralDelta -= finalExtract;
                    nutrients.mineralDelta += finalExtract;
                    currentMineral += finalExtract;
                }

                // update node sprite 
                Resource resourceType = Mappers.resourceCMap.get(resourceNode);

                if(resourceType.resourceType == ResourceType.CARBON){
                    nodeCarbonsAvail = Math.max(0, nodeNutrients.carbons + nodeNutrients.carbonDelta);
                    int carbonLevel  = (nodeCarbonsAvail > 40)? 3: (nodeCarbonsAvail > 30) ? 2: (nodeCarbonsAvail>0)? 1 : 0;
                    if (carbonLevel > 0){
                        nodeSprite.texture = TextureUtils.pathToTexture("carbonNode"+carbonLevel+".png");
                    }
                    else{
                        EntityBuilder.instance().dead(resourceNode);
                    }
                }
                else if (resourceType.resourceType == ResourceType.MINERAL){
                    nodeMineralsAvail = Math.max(0, nodeNutrients.minerals + nodeNutrients.mineralDelta);
                    int mineralLevel  = (nodeMineralsAvail > 40)? 3: (nodeMineralsAvail > 30) ? 2: (nodeMineralsAvail > 0)? 1 : 0;
                    if (mineralLevel > 0){
                        nodeSprite.texture = TextureUtils.pathToTexture("mineralNode"+mineralLevel+".png");
                    }
                    else{
                        EntityBuilder.instance().dead(resourceNode);
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("ExtractionError: " + e.getMessage());
        }
        finally{
            this.processingArray.clear();
        }
    }
}

