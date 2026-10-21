package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.ColorSettings;
import io.github.ethanBostick.core.CommonValues;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Cleanser;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Health;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Region;
import io.github.ethanBostick.ecs.components.ToxicWave;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

public class ToxicWaveSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private int[][] hexDirections;
    private final IntArray nextToxicWavefront = new IntArray(false, 64);
    private final Array<Entity> tempNeighborArray = new Array<Entity>(false, 7);
    
    public ToxicWaveSystem(float interval, int priority) {
        super(Family.all( ToxicWave.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
        this.hexDirections = HexUtils.hexDirections;
    }

    private boolean isCoveredByActiveCleanser(int q, int r){
        tempNeighborArray.clear();
        boolean modified = false;

        int centerQ = q;
        int centerR = r;
        Entity ownTile = Map.instance().getEntityAt(centerQ, centerR, TilePosition.MYCELIUM);
        if(ownTile != null && Mappers.cleanserCMap.get(ownTile) != null){
            tempNeighborArray.add(ownTile);
            modified = true;
        } 

        for (int[] dir : hexDirections){
            int targetQ = q + dir[0];
            int targetR = r + dir[1];
            Entity neighbor = Map.instance().getEntityAt(targetQ, targetR, TilePosition.MYCELIUM);
            if(neighbor != null && Mappers.cleanserCMap.get(neighbor) != null){
                tempNeighborArray.add(neighbor);
                modified = true;
            } 
        } 
        return modified;
    }

    // Checks if (nq, nr) sits diagonally behind a tile on currentRingDist that was blocked
    private boolean isShadowedByBlockedInnerNeighbor(int nq, int nr, int emitterQ, int emitterR, int currentRingDist, IntArray activeWavefront) {
        for (int[] dir : hexDirections) {
            int innerQ = nq + dir[0];
            int innerR = nr + dir[1];

            int dq = innerQ - emitterQ;
            int dr = innerR - emitterR;
            int dist = (Math.abs(dq) + Math.abs(dr) + Math.abs(dq + dr)) / 2;

            // If this neighbor sits on the current ring (an inner parent of nq, nr),
            // check if it survived in activeWavefront. If it's missing, it was blocked!
            if (dist == currentRingDist) {
                boolean parentIsActive = false;
                for (int j = 0; j < activeWavefront.size; j += 2) {
                    if (activeWavefront.get(j) == innerQ && activeWavefront.get(j + 1) == innerR) {
                        parentIsActive = true;
                        break;
                    }
                }
                if (!parentIsActive) {
                    return true; // One of its inner parents was blocked by a mountain/cleanser!
                }
            }
        }
        return false;
    }

    private void updateRegionConditionSprite(Entity surfaceTile, Region region){
        if (region.condition >= 100){
            region.condition = 100;
            region.regionType = RegionType.RESTORED;
            Mappers.spriteCMap.get(surfaceTile).texture = TextureUtils.pathToTexture("restoredTile.png");
        }
        //tile sprite updates
        else if(region.condition >= 75){
            Mappers.spriteCMap.get(surfaceTile).texture = TextureUtils.pathToTexture(region.regionType.name+"TileR3.png");
        }
        else if(region.condition >= 50){
            Mappers.spriteCMap.get(surfaceTile).texture = TextureUtils.pathToTexture(region.regionType.name+"TileR2.png");
        }
        else if(region.condition >= 25){
            Mappers.spriteCMap.get(surfaceTile).texture = TextureUtils.pathToTexture(region.regionType.name+"TileR1.png");
        }
        else{
            Mappers.spriteCMap.get(surfaceTile).texture = TextureUtils.pathToTexture(region.regionType.name+"Tile.png");
        }
    }

    private boolean applySludgeDamage(int q, int r, Entity surfaceTile, Region region, RegionType hazardType){

        //harm env
        if(region.regionType == RegionType.RESTORED){
            Entity surfaceEntity = Map.instance().getEntityAt(q, r, TilePosition.SURFACE_ENTITY);

            if(surfaceEntity != null && Mappers.greenCMap.get(surfaceEntity) != null){
                Health greenHealth = Mappers.healthCMap.get(surfaceEntity);
                Nutrients greenNutrients = Mappers.nutrientsCMap.get(surfaceEntity);
                int availableHealthLoss = Math.max(0, greenHealth.health-(int)CommonValues.TOXIC_WAVE_DMG.value);

                int availableMinerals = greenNutrients.minerals + Math.min(0, greenNutrients.mineralDelta);                 
                int mineralLeach = Math.min(availableMinerals, (int)CommonValues.TOXIC_WAVE_NUTRIENT_LEACH.value);

                int availableCarbons = greenNutrients.carbons + Math.min(0, greenNutrients.carbonDelta);
                int carbonLeach = Math.min(availableCarbons, (int)CommonValues.TOXIC_WAVE_NUTRIENT_LEACH.value);

                greenHealth.health -= availableHealthLoss;
                greenNutrients.mineralDelta -= mineralLeach;
                greenNutrients.carbonDelta -= carbonLeach;

                //display values
                if (availableHealthLoss > 0) {
                    EntityBuilder.instance().createFloatingText(q, r, 0, "-" + availableHealthLoss + "H", ColorSettings.HEALTH_LOSSES.color, true);
                }
                if (carbonLeach > 0) {
                    EntityBuilder.instance().createFloatingText(q, r, 0, "-" + carbonLeach + "C", ColorSettings.CARBON_LOSSES.color, true);
                }
                if (mineralLeach > 0) {
                    EntityBuilder.instance().createFloatingText( q, r, 0, "-" + mineralLeach + "M", ColorSettings.MINERAL_LOSSES.color, true);
                }
            }
            else{
                region.regionType = hazardType;
                region.condition = Math.max(0, region.condition-25);
                this.updateRegionConditionSprite(surfaceTile, region);
            }
        }
        //region is already corrupt 
        else{
            region.condition = Math.max(0, region.condition-25);
            this.updateRegionConditionSprite(surfaceTile, region);
        }

        return true;
    }

    @Override
    protected void processEntity(Entity toxicWave) {
        try {
            ToxicWave wave = Mappers.toxicWaveCMap.get(toxicWave);
            MultiTilePosition wavefront = Mappers.multiTilePositionCMap.get(toxicWave);
            Position emitterPosition = Mappers.positionCMap.get(toxicWave);

            int emitterQ = emitterPosition.q;
            int emitterR = emitterPosition.r;

            if (wave.currentStep == 0 || wavefront.points.size == 0) {
                wavefront.points.clear();
                wave.visitedHexes.clear();
                wavefront.points.add(emitterQ);
                wavefront.points.add(emitterR);
                wave.visitedHexes.add(Map.instance().getIndex(emitterQ, emitterR));
            }

            nextToxicWavefront.clear();
            int currentRingDist = wave.currentStep;
            int targetRingDist = wave.currentStep + 1;

            // Expand every hex currently on the leading edge of the wave
            for (int i = 0; i < wavefront.points.size; i += 2) {
                int curQ = wavefront.points.get(i);
                int curR = wavefront.points.get(i + 1);

                for (int[] dir : hexDirections) {
                    int nq = curQ + dir[0];
                    int nr = curR + dir[1];

                    int dq = nq - emitterQ;
                    int dr = nr - emitterR;
                    int dist = (Math.abs(dq) + Math.abs(dr) + Math.abs(dq + dr)) / 2;
                    if (dist != targetRingDist) continue;

                    int hexIndex = Map.instance().getIndex(nq, nr);
                    if (wave.visitedHexes.contains(hexIndex)) continue;

                    if (currentRingDist >= 1 && isShadowedByBlockedInnerNeighbor(nq, nr, emitterQ, emitterR, currentRingDist, wavefront.points)) {
                        continue;
                    }

                    wave.visitedHexes.add(hexIndex);

                    Entity surface = Map.instance().getEntityAt(nq, nr, TilePosition.SURFACE);
                    if (surface == null) continue;

                    Region region = Mappers.regionCMap.get(surface);

                    if (region != null && region.regionType == RegionType.MOUNTAIN) {
                        continue; // Do not add to nextToxicWavefront!
                    }

                    //Active Cleanser shields block the wave
                    if (this.isCoveredByActiveCleanser(nq, nr)) {
                        int carbonCost = (int)CommonValues.TOXIC_WAVE_CARBON_BLOCK_COST.value;
                        int mineralCost = (int)CommonValues.TOXIC_WAVE_MINERAL_BLOCK_COST.value;
                        //check each cleanser
                        for (int j = 0 ; j < this.tempNeighborArray.size; j++){
                            Entity cleanserNode = this.tempNeighborArray.get(j);
                            Nutrients nutrients = Mappers.nutrientsCMap.get(cleanserNode);
                            Position position = Mappers.positionCMap.get(cleanserNode);

                            int usableMinerals = nutrients.minerals + Math.min(0, nutrients.mineralDelta);
                            int usableCarbons = nutrients.carbons + Math.min(0, nutrients.carbonDelta);

                            int mineralBlock = Math.min(usableMinerals, mineralCost);
                            int carbonBlock = Math.min(usableCarbons, carbonCost);
                            
                            nutrients.carbonDelta -= carbonBlock; 
                            carbonCost -= carbonBlock;
                            nutrients.mineralDelta -= mineralBlock; 
                            mineralCost -= mineralBlock;

                            if (carbonBlock > 0) {
                                EntityBuilder.instance().createFloatingText(position.q, position.r, 0, "-" + carbonBlock + "C", ColorSettings.CARBON_LOSSES.color, true);
                            }
                            if (mineralBlock > 0) {
                                EntityBuilder.instance().createFloatingText(position.q, position.r, 0, "-" + mineralBlock + "M", ColorSettings.MINERAL_LOSSES.color, true);
                            }

                            //wave stopped
                            if(carbonCost <= 0 && mineralCost <= 0) break;
                        }

                        //blocked
                        if(carbonCost <= 0 && mineralCost <= 0) continue;
                    }

                    //unblocked
                    applySludgeDamage(nq, nr, surface, region, wave.hazardType);

                    //propogate wave
                    nextToxicWavefront.add(nq);
                    nextToxicWavefront.add(nr);
                }
            }

            // Swap nextToxicWavefront into wavefront.points for the next tick
            wavefront.points.clear();
            wavefront.points.addAll(nextToxicWavefront);

            wave.currentStep++;
            if (wave.currentStep >= wave.maxSteps || wavefront.points.size == 0) {
                wavefront.points.clear();
                EntityBuilder.instance().dead(toxicWave);
            }
        } catch (Exception e) {
            System.out.println("ToxicWaveError: " + e.getMessage());
        }
    }
}