package io.github.ethanBostick.ecs;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.IntArray;


public class MapGenerationSystem extends IteratingSystem{
    private double thresh;
    private Map map;
    private Entity[] blankTiles;
    private IntArray tempNeighbors = null;

    private boolean converged = false;
    private static ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Biome> bMap = ComponentMapper.getFor(Biome.class);

    public MapGenerationSystem(double thresh, Map map) {
        super(Family.all(Position.class, Biome.class).get());
        this.thresh = thresh;
        this.map = map;
		int w = (this.map.getMapSize()*2) + 1;
		this.blankTiles = new Entity[w*w];
    }

    private Entity getRandomBlank() {
        Entity selected = null;
        int validCount = 0;

        for (int i = 0; i < this.blankTiles.length; i++) {
            if (this.blankTiles[i] != null) {
                validCount++;
                if (this.map.random.nextInt(validCount) == 0) {
                    selected = this.blankTiles[i];
                }
            }
        }
        return selected; 
    }
    private double getHappiness(Entity self){
        if(this.tempNeighbors == null){
            this.tempNeighbors = new IntArray();
        }
        Biome selfBiome = bMap.get(self);
        int selfTemp = selfBiome.temp;

        HexUtils.getNeighborsPositions(pMap.get(self), this.tempNeighbors);
        int numNeighbors = 0;
        double simularitySum = 0;

        for (int i = 0 ; i < tempNeighbors.size; i += 2){
            Entity neighbor = this.map.getEntityAt(this.tempNeighbors.items[i],this.tempNeighbors.items[i+1], TilePosition.TILE);

            if(neighbor == null) continue;
            else{
                Biome neighborBiome = bMap.get(neighbor);
                int neighborTemp = neighborBiome.temp;

                if (neighborTemp != -1){
                    double diff = Math.abs(selfTemp - neighborTemp);
                    double simularity = Math.max(0.0,1.0-(diff/100.0));
                    simularitySum += simularity;
                    numNeighbors ++;
                }
            }
        }
        return (numNeighbors == 0) ? 0 : simularitySum/numNeighbors;
    }

    private void swapTiles(Entity self, Entity target){
        Position selfPosition = pMap.get(self);
        Position targetPosition = pMap.get(target);
        int tempQ, tempR;
        tempQ = selfPosition.q;
        tempR = selfPosition.r;
        selfPosition.q = targetPosition.q;
        selfPosition.r = targetPosition.r;
        targetPosition.q = tempQ;
        targetPosition.r = tempR;

        this.map.setEntityAt(targetPosition.q, targetPosition.r, target, TilePosition.TILE);
        this.map.setEntityAt(selfPosition.q, selfPosition.r, self, TilePosition.TILE);

        this.blankTiles[this.map.getIndex(selfPosition.q,selfPosition.r)] = null;
        this.blankTiles[this.map.getIndex(targetPosition.q,targetPosition.r)] = target;
    }

    @Override
    public void update(float deltaTime) {
        if(converged){
            this.blankTiles = null;
            this.map = null;
            this.tempNeighbors = null;
            this.setProcessing(false);
            return;
        }

        // IteratingSystem runs processEntity() on all matching entities
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = pMap.get(entity);
        Biome biome = bMap.get(entity);
        
        if (biome.biomeType == BiomeType.BLANK){
            this.blankTiles[this.map.getIndex(position.q, position.r)] = entity;
            return;
        }

        if (this.getHappiness(entity) < this.thresh){

            Entity blankTile = getRandomBlank();
            if(blankTile != null){
                this.swapTiles(entity, blankTile);
            }
        }
    }
}
