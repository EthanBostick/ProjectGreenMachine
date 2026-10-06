package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Density;
import io.github.ethanBostick.ecs.components.Health;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

public class DegenerationSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    
    public DegenerationSystem(float interval, int priority) {
        super(Family.all(Density.class, Sprite.class, Nutrients.class, NutrientFlow.class, NutrientCapacity.class, Position.class, Health.class).get(), interval, priority);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        try{

            Position position = Mappers.positionCMap.get(entity);
            Nutrients nutrients = Mappers.nutrientsCMap.get(entity);
            Health health = Mappers.healthCMap.get(entity);
            NutrientFlow flow = Mappers.nutrientFlowCMap.get(entity);
            Sprite sprite = Mappers.spriteCMap.get(entity);
            Density density = Mappers.densityCMap.get(entity);


            //region degeneration cost scalar
            Entity tileNode = Map.instance().getEntityAt(position.q, position.r, TilePosition.SURFACE);
            float regionScalar = 1;
            if (tileNode != null){
                regionScalar = Mappers.regionCMap.get(tileNode).regionType.degenerationScalar();
            }
            //cost of maintanence 
            int carbonRate = (int)(this.interval * CommonValues.DEGENERATION_CARBON_PER_TICK.value * regionScalar);
            int mineralRate = (int)(this.interval * CommonValues.DEGENERATION_MINERAL_PER_TICK.value * regionScalar);

            //if node health < 50 request nutrient
            if (health.health < 50){
                flow.needsMinerals = true;
                flow.needsCarbons = true;

                String texturePath = "myceliumD" + density.density + "Dying.png";
                sprite.texture = TextureUtils.pathToTexture(texturePath);
            }

            //if not enough nutrients, degrade
            if(nutrients.carbons + nutrients.carbonDelta < carbonRate || nutrients.minerals + nutrients.mineralDelta < mineralRate){
                health.health -= 1;
            }
            else{
                health.health = Math.min(100, 1 + health.health);
                nutrients.carbonDelta -= carbonRate;
                nutrients.mineralDelta -= mineralRate;
            }

            //mycelium death
            if(health.health <= 0){
                Map.instance().setEntityAt(position.q, position.r, null, TilePosition.MYCELIUM);
                EntityBuilder.instance().dead(entity);
            }
        }
        catch(Exception e){
            System.out.println("DegenerationError: " + e.getMessage());
        }
        finally{
        }
    }
}