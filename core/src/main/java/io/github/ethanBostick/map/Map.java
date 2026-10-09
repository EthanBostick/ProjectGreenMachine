package io.github.ethanBostick.map;

import java.lang.Math;
import java.util.Random;

//GDX stuff
import com.badlogic.ashley.core.Entity;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.Position;

public class Map{
	private static Map theInstance = null;
	private Entity[][] map;
	private int[] revealedMap;
	private EntityBuilder entityBuilder = null;

	//map config
	private int size = 0;
	private int w = 0;
	public Random random = null;
	private RegionType[] activeRegions = null;
	private double[] regionConcentrations = null;
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

	public void setRevealed(int q, int r){
		int i = getIndex(q, r);
		this.revealedMap[i] = 1;
	}

	public int getRevealState(int q, int r){
		int i = getIndex(q, r);
		return this.revealedMap[i];
	}
	public int getRevealState(Position p){
		int i = getIndex(p.q, p.r);
		return this.revealedMap[i];
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

	private int weightedRegionChoice(){
		double randomValue = this.random.nextDouble();
		double total = 0.0;

		for(int i=0; i < this.regionConcentrations.length; i++){
			if(this.regionConcentrations[i] < 0){
				throw new IllegalArgumentException();
			}

			total += this.regionConcentrations[i];

			if (randomValue < total){
				return i;
			}
		}

		return this.regionConcentrations.length - 1;
	}

	public void initConfig(int size, double[] concentrations, RegionType[] activeRegions, double thresh, int startingArea, int seed){
		this.size = size;
		this.random = new Random(seed);
		this.seed = seed;
		this.w = (size*2) + 1;
		this.regionConcentrations = concentrations;
		this.startingArea = startingArea;
		this.likenessThresh = thresh;
		this.activeRegions = activeRegions;
	}

	public void initMap(){
		int[] regionGrid = new int[w*w];
		this.revealedMap = new int[w*w];
		for (int q = -this.size ; q <= this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r <= Math.min(this.size, -q + this.size); r ++){
				int index = this.getIndex(q, r);
				this.revealedMap[index] = 0; //default unrevealed
				if(Math.abs(q) <= startingArea && Math.abs(r) <= startingArea && Math.abs(q+r) <= startingArea){
					regionGrid[index] = RegionType.RESTORED.tempValue();
				}
				else{
					regionGrid[index] = activeRegions[this.weightedRegionChoice()].tempValue();
				}
			}
		}
		MapGenerator mapGenerator = new MapGenerator(this.size, likenessThresh, regionGrid, this.random, this.seed);
		int[][] duelGrid = mapGenerator.generate(999999,6);

		this.map = new Entity[w*w][TilePosition.MAX_POSITIONS.value()];
		for (int q = -this.size ; q <= this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r <= Math.min(this.size, -q + this.size); r ++){
				
				int index = this.getIndex(q, r);
				double rDouble = this.random.nextDouble();

				Entity tileEntity = null;
				String tilePng = "hex_template.png";
				String name = "";
				String extraPng = null;
				int renderLayer = 0;
				RegionType bType = RegionType.BLANK;

				if (duelGrid[0][index] == RegionType.BARREN.tempValue()){
					tilePng = "barrenTile.png";
					name = "barrenTile";
					bType = RegionType.BARREN;						

				} else if (duelGrid[0][index] == RegionType.RADIOACTIVE.tempValue()){
					tilePng = "radioactiveTile.png";
					name = "radioactiveTile";
					bType = RegionType.RADIOACTIVE;						
				} else if (duelGrid[0][index] == RegionType.POLLUTED.tempValue()){
					tilePng = "pollutedTile.png";
					name = "pollutedTile";
					bType = RegionType.POLLUTED;						
					if (rDouble <= 0.45){
						extraPng = "deadTree.png";							
					}
				} else if (duelGrid[0][index] == RegionType.MOUNTAIN.tempValue()){
					tilePng = "mountainTile.png";
					name = "mountainTile";
					renderLayer = 2;
					bType = RegionType.MOUNTAIN;						
				} else if (duelGrid[0][index] == RegionType.RESTORED.tempValue()){
					tilePng = "restoredTile.png";
					name = "restoredTile";
					bType = RegionType.RESTORED;						
				} else{
					tilePng = "mountainTile.png";
					name = "mountainTile";
					renderLayer = 2;
					bType = RegionType.MOUNTAIN;						
				}

				//Put the Entities together and update map
				tileEntity = entityBuilder.createRenderable(q,r,renderLayer,0,TilePosition.SURFACE, tilePng, name);
				entityBuilder.addRegion(bType, tileEntity);
				if(extraPng != null){
					Entity terrainEntity = entityBuilder.createRenderable(q,r,1, 0,TilePosition.TERRAIN, extraPng);
					this.map[index][TilePosition.TERRAIN.value()] = terrainEntity;
					this.entityBuilder.addToEngine(terrainEntity);
				}
				this.entityBuilder.addToEngine(tileEntity);
				this.map[index][TilePosition.SURFACE.value()] = tileEntity;

				//underground gen
				Entity underGroundTileEntity = entityBuilder.createRenderable(q,r,0,1,TilePosition.UNDERGROUND, "dirtTile.png");
				entityBuilder.addRegion(bType, underGroundTileEntity);
				this.entityBuilder.addToEngine(underGroundTileEntity);
				this.map[index][TilePosition.UNDERGROUND.value()] = underGroundTileEntity;

				int resourceConcentration = duelGrid[1][index];
				if (resourceConcentration > 0 && bType.resourceType() != ResourceType.NONE){
					Entity resourceNode;
					int nodeLevel  = (resourceConcentration > 40)? 3: (resourceConcentration > 30) ? 2:1;

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

				double rockRoll = this.random.nextDouble();
				double rockThreshold = (tilePng == "mountainTile.png") ? 0.75 : 0.13; // rock obstacle

				if (rockRoll < rockThreshold){
					Entity rock = entityBuilder.createRenderable(q, r, 2, 1, TilePosition.UNDERGROUND_TERRAIN, "stone.png");	
					this.entityBuilder.addToEngine(rock);
					this.map[index][TilePosition.UNDERGROUND_TERRAIN.value()] = rock;
				}

				//init mycelium network node
				if (q == 0 && r == 0){
					Entity initMycelium = entityBuilder.createMycelium(q, r, 3,30,15,15,7);
					this.entityBuilder.addToEngine(initMycelium);
					this.map[index][TilePosition.MYCELIUM.value()] = initMycelium;

					//do not track, for visual only
					// Entity center = entityBuilder.createRenderable(0,0, 3,0,null, "hexCenter.png");
					// this.entityBuilder.addToEngine(center);
					// Entity centerUnder = entityBuilder.createRenderable(0,0, 3,1,null, "hexCenter.png");
					// this.entityBuilder.addToEngine(centerUnder);
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
