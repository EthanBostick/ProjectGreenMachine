package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Cultivator;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Density;
import io.github.ethanBostick.ecs.components.Green;
import io.github.ethanBostick.ecs.components.Health;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

public class GreenSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private IntArray processingArray = new IntArray(14);
    
    public GreenSystem(float interval, int priority) {
        super(Family.all(Nutrients.class, NutrientCapacity.class, Position.class,Sprite.class, Green.class, Health.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
    }

    private int getLevelThresh(int level){
        return (int)(CommonValues.GREEN_BASE_LEVEL_SCALAR.value * (level-1));
    }

    @Override
    protected void processEntity(Entity greenNode) {
        try{
            Position position = Mappers.positionCMap.get(greenNode);
            Nutrients nutrients = Mappers.nutrientsCMap.get(greenNode);
            NutrientCapacity levelThreshold = Mappers.nutrientCapacityCMap.get(greenNode);
            Health health = Mappers.healthCMap.get(greenNode);
            Green green = Mappers.greenCMap.get(greenNode);
            Density level = Mappers.densityCMap.get(greenNode);
            Sprite sprite = Mappers.spriteCMap.get(greenNode);

        //maintanence step
            //cost of maintanence 
            boolean withering = false;
            int usableMinerals = nutrients.minerals + Math.min(0, nutrients.mineralDelta);
            float mineralBuildup = green.mineralUsage * (this.interval/CommonValues.TICK_SPEED.value); //usage in per system tick from tickRate
            health.degenerationMineralBuildup += mineralBuildup;
            int mineralRate = (int)health.degenerationMineralBuildup;
            health.degenerationMineralBuildup -= mineralRate;

            //if not enough nutrients, degrade
            if(usableMinerals < mineralRate){
                health.health -= CommonValues.GREEN_DECAY_HEALTH_PER_TICK.value*(this.interval/CommonValues.TICK_SPEED.value);
                withering = true;
                //DD-1
                //update sprite to withering 
            }
            else{
                health.health = Math.min(100, 1 + health.health);
                nutrients.mineralDelta -= mineralRate;
            }
            //update for checks
            usableMinerals = nutrients.minerals + Math.min(0, nutrients.mineralDelta);

            //downgrade & reset
            if (health.health <= 0 && withering){
                levelThreshold.mineralCapacity = Math.max(0,levelThreshold.mineralCapacity - this.getLevelThresh(level.density));
                level.density --;
                if(level.density == 0){
                    this.processingArray.clear();
                    Map.instance().setEntityAt(position.q, position.r, null, position.tilePosition);
                    EntityBuilder.instance().dead(greenNode);
                    return;
                }
                green.carbonOutput = (int)(CommonValues.GREEN_BASE_CARBON_OUTPUT_PER_HEX_PER_TICK.value * level.density);
                sprite.texture = TextureUtils.pathToTexture("greenS"+level.density+".png");
                health.health = 100;
            }

        //check for green upgrade & then reset
            if (usableMinerals >= levelThreshold.mineralCapacity && level.density < 4){
                level.density ++;
                green.carbonOutput = (int)(CommonValues.GREEN_BASE_CARBON_OUTPUT_PER_HEX_PER_TICK.value * level.density);
                sprite.texture = TextureUtils.pathToTexture("greenS"+level.density+".png");
                levelThreshold.mineralCapacity += this.getLevelThresh(level.density);
                health.health = 100;
            }
        
        //carbon out step
            if(!withering){
                //get radius vector
                int radius = Math.min(0,level.density - 1);
                this.processingArray = HexUtils.getAreaAround(position, radius, processingArray);
                int carbonOutput = (int)(green.carbonOutput * (this.interval/CommonValues.TICK_SPEED.value)); //output per system tick per hex

                //distribute carbon
                for (int i = 0; i < this.processingArray.size; i += 2) {
                    int q = this.processingArray.get(i);
                    int r = this.processingArray.get(i+1);

                    //continue if no mycelium
                    Entity myceliumNode = Map.instance().getEntityAt(
                        q, 
                        r, 
                        TilePosition.MYCELIUM);
                    if(myceliumNode == null) continue;

                    Nutrients myceliumNutrients = Mappers.nutrientsCMap.get(myceliumNode);
                    NutrientCapacity myceliumNutrientCapacity = Mappers.nutrientCapacityCMap.get(myceliumNode);
                    int trueCarbonAmount = myceliumNutrients.carbonDelta + myceliumNutrients.carbons;
                    int carbonRoom = Math.max(0, myceliumNutrientCapacity.carbonCapacity - trueCarbonAmount);

                    int finalOutput = Math.min(carbonOutput, carbonRoom);
                    myceliumNutrients.carbonDelta += finalOutput;

                    if (finalOutput > 0) {
                        EntityBuilder.instance().createFloatingText(
                            position.q, 
                            position.r, 
                            1,
                            "+" + finalOutput + "C", 
                            Color.GREEN,
                            true
                        );
                    }
                }
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
