package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.ecs.components.Extraction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class ExtractionSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(6);
    
    public ExtractionSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Extraction.class).get(), interval, priority);
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

            //look for and proccess resource nodes in area
            for (int i = 0; i < this.processingArray.size; i += 2) {
                Entity resourceNode = Map.instance().getEntityAt(
                    this.processingArray.get(i), 
                    this.processingArray.get(i + 1), 
                    TilePosition.RESOURCE_NODE);
                if (resourceNode == null) continue;

                Nutrients nodeNutrients = Mappers.nutrientsCMap.get(resourceNode);

                // Carbon extraction
                if (nodeNutrients.carbons > 0 && nutrients.carbons < nutrientCapacity.carbonCapacity) {
                    int capacityLeft = nutrientCapacity.carbonCapacity - nutrients.carbons;
                    int desiredExtract = Math.min(extraction.carbonRate, capacityLeft);
                    int finalExtract = Math.min(desiredExtract, nodeNutrients.carbons);

                    nodeNutrients.carbons -= finalExtract;
                    nutrients.carbons += finalExtract;
                }

                // Mineral extraction
                if (nodeNutrients.minerals > 0 && nutrients.minerals < nutrientCapacity.mineralCapacity) {
                    int capacityLeft = nutrientCapacity.mineralCapacity - nutrients.minerals;
                    int desiredExtract = Math.min(extraction.mineralRate, capacityLeft);
                    int finalExtract = Math.min(desiredExtract, nodeNutrients.minerals);

                    nodeNutrients.minerals -= finalExtract;
                    nutrients.minerals += finalExtract;
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

