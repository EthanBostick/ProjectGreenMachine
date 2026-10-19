package io.github.ethanBostick.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
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


    @Override
    protected void processEntity(Entity toxicWave) {
        try{
            System.out.println("wave toxic system");
            ToxicWave wave = Mappers.toxicWaveCMap.get(toxicWave);
            MultiTilePosition wavefront = Mappers.multiTilePositionCMap.get(toxicWave);

            if (wave.currentStep == 0 || wavefront.points.size == 0) {
                int emitterQ = wavefront.points.get(0);
                int emitterR = wavefront.points.get(1);
                wavefront.points.clear();
                wave.visitedHexes.clear();
                wavefront.points.add(emitterQ);
                wavefront.points.add(emitterR);
                wave.visitedHexes.add(Map.instance().getIndex(emitterQ, emitterR));
            }

            nextToxicWavefront.clear();

            // Expand every hex currently on the leading edge of the wave
            for (int i = 0; i < wavefront.points.size; i += 2) {
                int curQ = wavefront.points.get(i);
                int curR = wavefront.points.get(i + 1);

                for (int[] dir : hexDirections) {
                    int nq = curQ + dir[0];
                    int nr = curR + dir[1];

                    int hexIndex = Map.instance().getIndex(nq, nr);
                    if (wave.visitedHexes.contains(hexIndex)) continue;
                    wave.visitedHexes.add(hexIndex);

                    Entity surface = Map.instance().getEntityAt(nq, nr, TilePosition.SURFACE);
                    if (surface == null) continue;

                    Region region = Mappers.regionCMap.get(surface);
                    
                    // 1. COLLISION CHECK: Mountains physically block the wave
                    if (region != null && region.regionType == RegionType.MOUNTAIN) {
                        continue; // Do not add to nextToxicWavefront!
                    }

                    // 2. DEFENSE CHECK: Active Cleanser shields block the wave
                    // if (tryAbsorbWithCleanser(nq, nr)) {
                    //     // Optional: spawn a floating text "-1M -1C" or shield ripple here!
                    //     continue; // ToxicWave dies on the shield; do not add to nextToxicWavefront!
                    // }

                    // 3. UNBLOCKED: Apply toxic damage to greenery & surface
                    // applySludgeDamage(nq, nr, surface, region);

                    // 4. Propagate wavefront forward from this tile on the next step
                    nextToxicWavefront.add(nq);
                    nextToxicWavefront.add(nr);
                }
            }

            // Swap nextToxicWavefront into currentToxicWavefront for the next tick
            wavefront.points.clear();
            wavefront.points.addAll(nextToxicWavefront);

            wave.currentStep++;
            if (wave.currentStep >= wave.maxSteps) {
                wavefront.points.clear();
                EntityBuilder.instance().dead(toxicWave);
            }
        }
        catch(Exception e){
            System.out.println("ToxicWaveError: " + e.getMessage());
        }
        finally{
        }
    }
}
