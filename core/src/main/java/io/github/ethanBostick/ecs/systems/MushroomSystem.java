package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Mushroom;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class MushroomSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(128);
    private IntArray landingArray = new IntArray(false, 6);
    
    public MushroomSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Mushroom.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    private void removeUnhidden(){
        for (int i = processingArray.size - 2; i >= 0; i -= 2) {
            int q = processingArray.get(i);
            int r = processingArray.get(i + 1);

            if (Map.instance().getRevealState(q, r) == 1) {
                processingArray.removeIndex(i + 1);
                processingArray.removeIndex(i);
            }
        }
    }

    private boolean findLandingSpots(){
        boolean sporeLanded = false;
        landingArray.clear();

        for (int i = 0; i < (int)CommonValues.SPORES_PER_MUSHROOM.value; i++){
            int randSpot = Map.instance().random.nextInt((processingArray.size/2 + 1) * 2);
            int q = processingArray.get(randSpot);
            int r = processingArray.get(randSpot+1);

            if(Map.instance().getEntityAt(q, r, TilePosition.UNDERGROUND_TERRAIN) != null) continue;

            landingArray.add(q);            
            landingArray.add(r);            
            sporeLanded = true;
        }

        return sporeLanded;
    }

    @Override
    protected void processEntity(Entity mushroomNode) {
        try{
            Nutrients nutrients = Mappers.nutrientsCMap.get(mushroomNode);
            NutrientCapacity capacity = Mappers.nutrientCapacityCMap.get(mushroomNode);

            //skip if not yet reached capacity
            if (nutrients.carbons < capacity.carbonCapacity || nutrients.minerals < capacity.mineralCapacity){
                return;
            }

            //launch spores (find landing spots)
            Position position = Mappers.positionCMap.get(mushroomNode);
            HexUtils.getAreaAround(position, (int)CommonValues.SPORE_RADIUS.value, processingArray);
            this.removeUnhidden();
            this.findLandingSpots();

            //build extractors at spore locations for propogation
            //animate later
            for(int i=0; i < landingArray.size ; i +=2){
                int q = landingArray.get(i);
                int r = landingArray.get(i+1);
                
                Entity extractor = EntityBuilder.instance().createMyceliumExtractor(q, r,0,0,BuildType.EXTRACTOR.carbonCost/2,BuildType.EXTRACTOR.mineralCost/2);
                Map.instance().setEntityAt(q,r, extractor, TilePosition.MYCELIUM);
                EntityBuilder.instance().addToEngine(extractor);
            }

            //degenerate Mushrooms (spore explosion or something)
            Map.instance().setEntityAt(position.q, position.r, null, TilePosition.MYCELIUM);       
            Map.instance().setEntityAt(position.q, position.r, null, TilePosition.SURFACE_MYCELIUM);       
            EntityBuilder.instance().reclaim(mushroomNode);
            
        }
        catch(Exception e){
            System.out.println("MushroomError: " + e.getMessage());
        }
        finally{
            this.processingArray.clear();
        }
    }
}

