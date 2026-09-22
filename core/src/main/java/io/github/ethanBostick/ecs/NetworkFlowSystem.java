package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

public class NetworkFlowSystem extends IntervalIteratingSystem {
    public final float interval; //half seconds
    private final int[][] hexDirections = {
        {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}
    };
    
    /**
     * @param interval The time in seconds between each system execution (e.g., 0.5f)
     */
    public NetworkFlowSystem(float interval) {
        super(Family.all(Nutrients.class, NutrientDraw.class, NutrientCapacity.class, Position.class).get(), interval);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        try{
            Position position = Mappers.positionCMap.get(entity);
            Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
            NutrientCapacity nutrientCapacity = Mappers.nutrientCapacityCMap.get(entity);
            NutrientDraw nutrientDraw = Mappers.nutrientDrawCMap.get(entity);

        //two channel system, carbon and minerals may flow in oppisite directions
        //resource equilibrium and draw (channel draw and flow and mutually exclusive)
            for(int i = 0; i < 6; i++){
                Entity neighbor = Map.instance().getEntityAt(position.q + hexDirections[i][0], position.r + hexDirections[i][1], TilePosition.MYCELIUM);
                if(neighbor == null) continue;
                Nutrients neighborNutrients = Mappers.nutrientsCMap.get(neighbor);
                NutrientCapacity neighborNutrientCapacity = Mappers.nutrientCapacityCMap.get(neighbor);

                //minerals
                if(nutrientDraw.mineralDraw > 0 && nutrients.carbons < nutrientCapacity.carbonCapacity){
                    //handle overflow
                    int drawAmount = (nutrientDraw.mineralDraw + nutrients.carbons >= nutrientCapacity.carbonCapacity)? 
                        nutrientCapacity.mineralCapacity-nutrients.carbons : nutrientDraw.carbonDraw;

                    if(neighborNutrients.minerals > 0){
                        //handle overtaking
                        int finalDrawAmount = (neighborNutrients.minerals-drawAmount >= 0)?
                            drawAmount : drawAmount - neighborNutrients.minerals;

                        neighborNutrients.minerals -= finalDrawAmount;
                        nutrients.minerals += finalDrawAmount;
                    }
                } 
                else if (nutrientDraw.mineralDraw == 0 && 
                nutrients.minerals > 0 && 
                nutrients.minerals - neighborNutrients.minerals > 1 && 
                neighborNutrients.minerals < neighborNutrientCapacity.mineralCapacity){

                    nutrients.minerals --;
                    neighborNutrients.minerals ++;
                }

                //carbons
                if(nutrientDraw.carbonDraw > 0 && nutrients.carbons < nutrientCapacity.carbonCapacity){
                    //handle overflow
                    int drawAmount = (nutrientDraw.carbonDraw + nutrients.carbons >= nutrientCapacity.carbonCapacity)? 
                        nutrientCapacity.carbonCapacity-nutrients.carbons : nutrientDraw.carbonDraw;

                    if(neighborNutrients.carbons > 0){
                        //handle overtaking
                        int finalDrawAmount = (neighborNutrients.carbons-drawAmount >= 0)?
                            drawAmount : drawAmount - neighborNutrients.carbons;

                        neighborNutrients.carbons -= finalDrawAmount;
                        nutrients.carbons += finalDrawAmount;
                    }
                } 
                else if (nutrientDraw.carbonDraw == 0 && 
                nutrients.carbons > 0 && 
                nutrients.carbons - neighborNutrients.carbons > 1 && 
                neighborNutrients.carbons < neighborNutrientCapacity.carbonCapacity){

                    nutrients.carbons --;
                    neighborNutrients.carbons ++;
                }

            }
        }
        catch(Exception e){
            System.out.println("NetworkFlowError: " + e.getMessage());
        }
    }
}
