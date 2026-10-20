package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Region;
import io.github.ethanBostick.ecs.components.ToxicWave;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

public class ToxicWaveSystem extends IntervalIteratingSystem {
    public final float interval; //seconds
    private int[][] hexDirections;
    private final IntArray nextToxicWavefront = new IntArray(false, 64);
    
    public ToxicWaveSystem(float interval, int priority) {
        super(Family.all( ToxicWave.class)
            .exclude(Dead.class).get(), interval, priority);
        this.interval = interval;
        this.hexDirections = HexUtils.hexDirections;
    }

    private boolean isCoveredByActiveCleanser(int q, int r){
        for (int[] dir : hexDirections){
            int targetQ = q + dir[0];
            int targetR = r + dir[1];
            Entity neighbor = Map.instance().getEntityAt(targetQ, targetR, TilePosition.MYCELIUM);
            if(neighbor == null) continue;
            if(Mappers.cleansingCMap.get(neighbor) == null) continue;
            else return true;
        } 
        return false;
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

    private boolean applySludgeDamage(int q, int r, Entity surfaceTile, Region region, RegionType hazardType){

        //harm env
        if(region.regionType == RegionType.RESTORED){

        }
        //simply region conversion
        else{
            region.regionType = hazardType;
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

                    // 4. DEFENSE CHECK: Active Cleanser shields block the wave
                    // if (tryAbsorbWithCleanser(nq, nr)) {
                    //     continue;
                    // }

                    // 5. UNBLOCKED: Apply toxic damage to greenery & surface
                    // applySludgeDamage(nq, nr, surface, region);

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