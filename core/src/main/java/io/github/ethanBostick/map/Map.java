package io.github.ethanBostick.map;

import io.github.ethanBostick.ecs.Biome;
import io.github.ethanBostick.ecs.EntityBuilder;

import java.lang.Math;
import java.util.Random;

//GDX stuff
import com.badlogic.ashley.core.Entity;

public class Map{
	private static Map theInstance = null;
	private Entity[][] map;
	private EntityBuilder entityBuilder = null;

	//map config
	private int size = 0;
	private int w = 0;
	public Random random = null;
	private BiomeType[] activeBiomes = null;
	private double[] biomeConcentrations = null;
	private int startingArea;
	private double likenessThresh = 0; 
	private int seed;

	private Map(){
		this.entityBuilder = EntityBuilder.instance();
	}

	public void setEntityAt(int q, int r, Entity e, TilePosition t){
		int i = getIndex(q, r);
		this.map[i][t.value()] = e;
	}

	public int getMapSize(){
		return this.size;
	}

	public int getIndex(int q, int r){
		return (q + this.size) + ((r+this.size) * w);
	}

	public boolean outOfMapBounds(int q, int r){
		return (Math.abs(q) > this.size || Math.abs(r) > this.size || Math.abs(q+r) > this.size);
	}

	public Entity getEntityAt(int q, int r, TilePosition t){

		if(outOfMapBounds(q, r)){
			return null;
		}

		int i = getIndex(q, r);
		return this.map[i][t.value()];
	}

	private int weightedBiomeChoice(){
		double randomValue = this.random.nextDouble();
		double total = 0.0;

		for(int i=0; i < this.biomeConcentrations.length; i++){
			if(this.biomeConcentrations[i] < 0){
				throw new IllegalArgumentException();
			}

			total += this.biomeConcentrations[i];

			if (randomValue < total){
				return i;
			}
		}

		return this.biomeConcentrations.length - 1;
	}

	public void initConfig(int size, double[] concentrations, BiomeType[] activeBiomes, double thresh, int startingArea, int seed){
		this.size = size;
		this.random = new Random(seed);
		this.seed = seed;
		this.w = (size*2) + 1;
		this.biomeConcentrations = concentrations;
		this.startingArea = startingArea;
		this.likenessThresh = thresh;
		this.activeBiomes = activeBiomes;
	}

	public void initMap(){
		int[] biomeGrid = new int[w*w];
		for (int q = -this.size ; q <= this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r <= Math.min(this.size, -q + this.size); r ++){
				int index = this.getIndex(q, r);
				if(Math.abs(q) <= startingArea && Math.abs(r) <= startingArea && Math.abs(q+r) <= startingArea){
					biomeGrid[index] = BiomeType.FOREST.tempValue();
				}
				else{
					biomeGrid[index] = activeBiomes[this.weightedBiomeChoice()].tempValue();
				}
			}
		}
		MapGenerator mapGenerator = new MapGenerator(this.size, likenessThresh, biomeGrid, this.random, this.seed);
		int[][] duelGrid = mapGenerator.generate(999999,20);

		this.map = new Entity[w*w][TilePosition.MAX_POSITIONS.value()];
		for (int q = -this.size ; q <= this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r <= Math.min(this.size, -q + this.size); r ++){
				
				int index = this.getIndex(q, r);
				double rDouble = this.random.nextDouble();

				Entity tileEntity = null;
				String tilePng = "hex_template.png";
				String extraPng = null;
				BiomeType bType = BiomeType.BLANK;

				if (duelGrid[0][index] == BiomeType.FOREST.tempValue()){
					tilePng = "forestFloor.png";
					bType = BiomeType.FOREST;						
					if (rDouble <= 0.45){
						extraPng = "trees2.png";							
					}

				} else if (duelGrid[0][index] == BiomeType.DESERT.tempValue()){
					tilePng = "sand.png";
					bType = BiomeType.DESERT;						
				} else if (duelGrid[0][index] == BiomeType.GRASS_LAND.tempValue()){
					tilePng = "grassFloor.png";
					bType = BiomeType.GRASS_LAND;						
					if (rDouble <= 0.6){
						extraPng = "grass.png";							
					}
				} else if (duelGrid[0][index] == BiomeType.MOUNTAIN.tempValue()){
					tilePng = "mountain.png";
					bType = BiomeType.MOUNTAIN;						
				} else if (duelGrid[0][index] == BiomeType.TAIGA.tempValue()){
					tilePng = "taiga.png";
					bType = BiomeType.TAIGA;						
					if (rDouble <= 0.25){
						extraPng = "pineTrees1.png";							
					}
				} else if (duelGrid[0][index] == BiomeType.BOREAL.tempValue()){
					tilePng = "boreal.png";
					bType = BiomeType.BOREAL;						
					if (rDouble <= 0.7){
						extraPng = "pineTrees1.png";							
					}
				} else{
					tilePng = "mountain.png";
					bType = BiomeType.MOUNTAIN;						
				}

				//Put the Entities together and update map
				tileEntity = entityBuilder.createRenderable(q,r,0,0,TilePosition.SURFACE, tilePng);
				entityBuilder.addBiome(bType, tileEntity);
				if(extraPng != null){
					Entity terrainEntity = entityBuilder.createRenderable(q,r,1, 0,TilePosition.TERRAIN, extraPng);
					this.map[index][TilePosition.TERRAIN.value()] = terrainEntity;
					this.entityBuilder.addToEngine(terrainEntity);
				}
				this.entityBuilder.addToEngine(tileEntity);
				this.map[index][TilePosition.SURFACE.value()] = tileEntity;

				//underground gen
				Entity underGroundTileEntity = entityBuilder.createRenderable(q,r,0,1,TilePosition.UNDERGROUND, "dirt.png");
				entityBuilder.addBiome(bType, underGroundTileEntity);
				this.entityBuilder.addToEngine(underGroundTileEntity);
				this.map[index][TilePosition.UNDERGROUND.value()] = underGroundTileEntity;

				int resourceConcentration = duelGrid[1][index];
				if (resourceConcentration > 0 && bType.resourceType() != ResourceType.NONE){
					Entity resourceNode;
					int nodeLevel  = (resourceConcentration > 50)? 3: (resourceConcentration > 25) ? 2:1;

					//create the entity with sprite
					if(bType.resourceType() == ResourceType.CARBON){
						resourceNode = entityBuilder.createRenderable(q, r, 1, 1, TilePosition.RESOURCE_NODE, "carbonNode"+nodeLevel+".png");
						this.entityBuilder.addNutrients(resourceNode, resourceConcentration, 0);
					}
					else{
						resourceNode = entityBuilder.createRenderable(q, r, 1, 1, TilePosition.RESOURCE_NODE, "mineralNode"+nodeLevel+".png");
						this.entityBuilder.addNutrients(resourceNode, 0, resourceConcentration);
					}
					//commit it to engine and map
					this.entityBuilder.addToEngine(resourceNode);
					this.map[index][TilePosition.RESOURCE_NODE.value()] = resourceNode;
				}

				double rockThreshold = (tilePng == "mountain.png") ? 0.75 : 0.13; // rock obstacle

				if (q == 0 && r == 0){
					Entity initMycelium = entityBuilder.createMycelium(q, r, 3);
					this.entityBuilder.addToEngine(initMycelium);
					this.map[index][TilePosition.MYCELIUM.value()] = initMycelium;

					//do not track, for visual only
					Entity center = entityBuilder.createRenderable(0,0, 3,0,null, "hexCenter.png");
					this.entityBuilder.addToEngine(center);
					Entity centerUnder = entityBuilder.createRenderable(0,0, 3,1,null, "hexCenter.png");
					this.entityBuilder.addToEngine(centerUnder);
				}
			}
		}
	}

	public static Map instance(){
		if (Map.theInstance == null){
			Map.theInstance = new Map();
		}
		return Map.theInstance;
	}
}
