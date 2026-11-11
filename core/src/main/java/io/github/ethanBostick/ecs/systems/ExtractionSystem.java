package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.Adaptations;
import io.github.ethanBostick.core.ColorSettings;
import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Extraction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Nutrients;
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
    private int totalCarbonEx = 0;
    private int totalMineralEx =0;
    
    public ExtractionSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Extraction.class)
        .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    private void updateSprite(Entity resourceNode, int nodeCarbonsAvail, int nodeMineralsAvail){
        // update node sprite 
        Resource resourceType = Mappers.resourceCMap.get(resourceNode);
        Position nodePosition = Mappers.positionCMap.get(resourceNode);
        Nutrients nodeNutrients = Mappers.nutrientsCMap.get(resourceNode);
        Sprite nodeSprite = Mappers.spriteCMap.get(resourceNode);

        if(resourceType.resourceType == ResourceType.CARBON){
            nodeCarbonsAvail = Math.max(0, nodeNutrients.carbons + nodeNutrients.carbonDelta);
            int carbonLevel  = (nodeCarbonsAvail > 40)? 3: (nodeCarbonsAvail > 30) ? 2: (nodeCarbonsAvail>0)? 1 : 0;

            //cleanup or update the sprite
            if (carbonLevel <= 0 && nodePosition != null && nodePosition.tilePosition != null){
                Map.instance().setEntityAt(nodePosition.q, nodePosition.r, null, nodePosition.tilePosition);
                EntityBuilder.instance().dead(resourceNode);
            }
            else{
                nodeSprite.texture = TextureUtils.pathToTexture("carbonNode"+carbonLevel+".png");
            }
        }
        else if (resourceType.resourceType == ResourceType.MINERAL){
            nodeMineralsAvail = Math.max(0, nodeNutrients.minerals + nodeNutrients.mineralDelta);
            int mineralLevel  = (nodeMineralsAvail > 40)? 3: (nodeMineralsAvail > 30) ? 2: (nodeMineralsAvail > 0)? 1 : 0;

            //cleanup or update the sprite
            if (mineralLevel <= 0 && nodePosition != null && nodePosition.tilePosition != null){
                Map.instance().setEntityAt(nodePosition.q, nodePosition.r, null, nodePosition.tilePosition);
                EntityBuilder.instance().dead(resourceNode);
            }
            else{
                nodeSprite.texture = TextureUtils.pathToTexture("mineralNode"+mineralLevel+".png");
            }
        }
    }

    private void extractFromNode(Nutrients nodeNutrients,Extraction extraction, Nutrients nutrients, NutrientCapacity nutrientCapacity){
        int currentCarbon = nutrients.carbons + nutrients.carbonDelta;
        int currentMineral = nutrients.minerals + nutrients.mineralDelta;

        // Carbon extraction
        int nodeCarbonsAvail = Math.max(0, nodeNutrients.carbons + nodeNutrients.carbonDelta);
        int extractorCarbonRoom = Math.max(0, nutrientCapacity.carbonCapacity - currentCarbon);

        if (nodeCarbonsAvail > 0 && extractorCarbonRoom > 0) {
            int desiredExtract = Math.min(extraction.carbonRate, extractorCarbonRoom);
            int finalExtract = Math.min(desiredExtract, nodeCarbonsAvail);

            nodeNutrients.carbonDelta -= finalExtract;
            nutrients.carbonDelta += finalExtract;
            totalCarbonEx += finalExtract;
        }

        // Mineral extraction
        int nodeMineralsAvail = Math.max(0, nodeNutrients.minerals + nodeNutrients.mineralDelta);
        int extractorMineralRoom = Math.max(0, nutrientCapacity.mineralCapacity - currentMineral);
        if (nodeMineralsAvail > 0 && extractorMineralRoom > 0) {
            int desiredExtract = Math.min(extraction.mineralRate, extractorMineralRoom);
            int finalExtract = Math.min(desiredExtract, nodeMineralsAvail);

            nodeNutrients.mineralDelta -= finalExtract;
            nutrients.mineralDelta += finalExtract;
            totalMineralEx += finalExtract;
        }
    }

    @Override
    protected void processEntity(Entity extractor) {
        try{
            this.totalCarbonEx = 0;
            this.totalMineralEx = 0;

            Position position = Mappers.positionCMap.get(extractor);
            Nutrients nutrients = Mappers.nutrientsCMap.get(extractor);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(extractor);
            Extraction extraction = Mappers.extractionCMap.get(extractor);

            //get radius vector
            int radius = (int)CommonValues.EXTRACTION_RADIUS.value;
            this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);

            //look for and proccess resource nodes in area
            for (int i = 0; i < this.processingArray.size; i += 2) {
                int q = this.processingArray.get(i);
                int r = this.processingArray.get(i+1);
                Entity resourceNode = Map.instance().getEntityAt(
                    q,
                    r,
                    TilePosition.RESOURCE_NODE);

                //rock check and extraction
                Entity rock = Map.instance().getEntityAt(q, r, TilePosition.UNDERGROUND_TERRAIN);
                if(rock != null && Adaptations.HARSH_CHEMICALS.unlocked == true && Mappers.nutrientsCMap.get(rock)!=null ){
                    Nutrients rockNutrients = Mappers.nutrientsCMap.get(rock);     
                    this.extractFromNode(rockNutrients, extraction, nutrients, nutrientCapacity);

                    //TODO: UPDATE ROCK SPRITE

                    if(rockNutrients.minerals <= 0){
                        Map.instance().setEntityAt(q, r, null, TilePosition.UNDERGROUND_TERRAIN);
                        EntityBuilder.instance().dead(rock);
                    }
                }
                else if (rock == null && resourceNode != null && Mappers.nutrientsCMap.get(resourceNode) != null){
                    Nutrients nodeNutrients = Mappers.nutrientsCMap.get(resourceNode);
                    this.extractFromNode(nodeNutrients, extraction, nutrients, nutrientCapacity);
                    int nodeCarbonsAvail = Math.max(0, nodeNutrients.carbons + nodeNutrients.carbonDelta);
                    int nodeMineralsAvail = Math.max(0, nodeNutrients.minerals + nodeNutrients.mineralDelta);
                    this.updateSprite(resourceNode, nodeCarbonsAvail, nodeMineralsAvail);
                }
            }

            if (totalCarbonEx > 0) {
                EntityBuilder.instance().createFloatingText(
                    position.q, 
                    position.r, 
                    1,
                    "+" + totalCarbonEx + "C", 
                    ColorSettings.CARBON_GAINS.color,
                    true
                );
            }
            if (totalMineralEx > 0) {
                EntityBuilder.instance().createFloatingText(
                    position.q, 
                    position.r, 
                    1,
                    "+" + totalMineralEx + "M", 
                    ColorSettings.MINERAL_GAINS.color,
                    true
                );
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

