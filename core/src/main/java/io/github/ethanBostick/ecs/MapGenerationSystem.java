package io.github.ethanBostick.ecs;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;
import com.badlogic.gdx.math.MathUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.IntArray;


public class MapGenerationSystem extends IteratingSystem{
    private int thresh;
    private Map map;
    private Entity[] blankTiles;
    private IntArray tempNeighbors = null;

    private boolean converged = false;
    private static ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private static ComponentMapper<Biome> bMap = ComponentMapper.getFor(Biome.class);

    public MapGenerationSystem(int thresh, Map map) {
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
    private int getHappiness(Position p){
        if(this.tempNeighbors == null){
            this.tempNeighbors = new IntArray();
        }

        HexUtils.getNeighborsPositions(p, this.tempNeighbors);
        int numNeighbors = 0;
        int simularitySum = 0;

        for (int i = 0 ; i < tempNeighbors.size; i += 2){
            Entity neighbor = this.map.getMapPosition(this.tempNeighbors.items[i],this.tempNeighbors.items[i+1], TilePosition.TILE);

            if(neighbor == null) continue;
            else{
                Position neighborPosition = pMap.get(neighbor);
                Biome neighBiome = bMap.get(neighbor);

                numNeighbors ++;
            }
        }

        return 0;
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

        Entity blankTile = getRandomBlank();
        if(blankTile != null){

        }
    }
}
