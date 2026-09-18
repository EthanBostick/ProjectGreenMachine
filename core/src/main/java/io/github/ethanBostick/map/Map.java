package io.github.ethanBostick.map;

import io.github.ethanBostick.ecs.BiomeType;
import io.github.ethanBostick.ecs.EntityBuilder;

import java.lang.Math;
import java.util.Random;

//GDX stuff
import com.badlogic.ashley.core.Entity;

public class Map{
	private static Map theInstance = null;
	private int size = 0;
	private int w = 0;
	public Random random = null;
	private Entity[][] map;
	private EntityBuilder entityBuilder = null;

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

	//rules for nutrient amount depVar: density, type, biome
	public int getRandomNutrientValue(int nutrientDensity, boolean carbon, BiomeType biome){
		double noiseScalar = this.random.nextGaussian();
		int nutrientAmount = 0;
		double typeScalar = 0;
		switch (nutrientDensity){
			case(-1): //plant
				typeScalar = (carbon)? 1.0 : 0.0;
				nutrientAmount = (int)((noiseScalar*5 + (100*typeScalar)));
				break;
			case(0):
				typeScalar = (carbon)? 1.0 : 0.5;
				nutrientAmount = (int)((noiseScalar*5 + (10*typeScalar))*biome.nutrientScalar());
				break;
			case(1):
				typeScalar = (carbon)? 1.0 : 0.5;
				nutrientAmount = (int)((noiseScalar*10 + (50*typeScalar)) *biome.nutrientScalar());
				break;
			case(2):
				typeScalar = (carbon)? 1.0 : 0.5;
				nutrientAmount = (int)((noiseScalar*15 + (100*typeScalar))*biome.nutrientScalar());
				break;
		}

		return (nutrientAmount < 0) ? 0 : nutrientAmount;
	}

	public void initMap(int size, double[] concentrations,double thresh, int startingArea, int seed){
		this.size = size;
		this.random = new Random(seed);
		this.w = (size*2) + 1;
		int[] initGrid = new int[w*w];
		for (int q = -this.size ; q <= this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r <= Math.min(this.size, -q + this.size); r ++){

				int index = this.getIndex(q, r);
				double rInt = this.random.nextDouble();
				if(Math.abs(q) <= startingArea && Math.abs(r) <= startingArea && Math.abs(q+r) <= startingArea){
					initGrid[index] = 65;
				}
				else if (rInt <= concentrations[0]){
					initGrid[index] = -1;
				}
				else if (rInt <= concentrations[1]){
					initGrid[index] = 100;
				}
				else if (rInt <= concentrations[2]){
					initGrid[index] = 65;
				}
				else if (rInt <= concentrations[3]){
					initGrid[index] = 15;
				}
				else if (rInt <= concentrations[4]){
					initGrid[index] = 35;
				}
				else if (rInt <= concentrations[5]){
					initGrid[index] = 50;
				}
				else{
					initGrid[index] = 75;
				}
			}
		}
		MapGenerator mapGenerator = new MapGenerator(this.size, thresh, initGrid, this.random);
		initGrid = mapGenerator.generate(999999,20);

		this.map = new Entity[w*w][TilePosition.MAX_POSITIONS.value()];
		for (int q = -this.size ; q <= this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r <= Math.min(this.size, -q + this.size); r ++){
				
				int index = this.getIndex(q, r);
				double rDouble = this.random.nextDouble();

				Entity tileEntity = null;
				String tilePng = "hex_template.png";
				String extraPng = null;
				BiomeType bType = BiomeType.BLANK;

				switch(initGrid[index]){
					case 65:
						tilePng = "forestFloor.png";
						bType = BiomeType.FOREST;						
						if (rDouble <= 0.45){
							extraPng = "trees2.png";							
						}
						break;
					case 100:
						tilePng = "sand.png";
						bType = BiomeType.DESERT;						
						break;
					case 75:
						tilePng = "grassFloor.png";
						bType = BiomeType.GRASS_LAND;						
						if (rDouble <= 0.6){
							extraPng = "grass.png";							
						}
						break;
					case 50:
						tilePng = "mountain.png";
						bType = BiomeType.MOUNTAIN;						
						break;
					case 15:
						tilePng = "taiga.png";
						bType = BiomeType.TAIGA;						
						if (rDouble <= 0.25){
							extraPng = "pineTrees1.png";							
						}
						break;
					case 35:
						tilePng = "boreal.png";
						bType = BiomeType.BOREAL;						
						if (rDouble <= 0.7){
							extraPng = "pineTrees1.png";							
						}
						break;
					case -1:
						tilePng = "mountain.png";
						bType = BiomeType.MOUNTAIN;						
						break;
				}
				//Put the Entities together and update map
				tileEntity = entityBuilder.createRenderable(q,r,0,0,TilePosition.SURFACE, tilePng);
				entityBuilder.addBiome(bType, tileEntity);
				if(extraPng != null){
					Entity terrainEntity = entityBuilder.createRenderable(q,r,1, 0,TilePosition.TERRAIN, extraPng);
					this.entityBuilder.addNutrients(terrainEntity, this.getRandomNutrientValue(-1, true, bType), 0);
					this.map[index][TilePosition.TERRAIN.value()] = terrainEntity;
					this.entityBuilder.addToEngine(terrainEntity);
				}
				this.entityBuilder.addToEngine(tileEntity);
				this.map[index][TilePosition.SURFACE.value()] = tileEntity;

				//underground gen
				Entity underEntity = entityBuilder.createRenderable(q,r,0,1,TilePosition.UNDERGROUND, "dirt.png");
				entityBuilder.addBiome(bType, underEntity);
				this.entityBuilder.addToEngine(underEntity);
				this.map[index][TilePosition.UNDERGROUND.value()] = underEntity;

				//nutrient allocation
				double rockThreshold = (tilePng == "mountain.png") ? 0.75 : 0.13;
				if (rDouble <= rockThreshold){
					Entity rockEntity = entityBuilder.createRenderable(q,r, 2,1,TilePosition.UNDERGROUND_TERRAIN, "mineralsD3.png");
					this.map[index][TilePosition.UNDERGROUND_TERRAIN.value()] = rockEntity;
					this.entityBuilder.addNutrients(rockEntity, 0, this.getRandomNutrientValue(2, false, bType));
					this.entityBuilder.addToEngine(rockEntity);
				}
				else if (rDouble <= 0.4){
					Entity rockEntity = entityBuilder.createRenderable(q,r, 2,1,TilePosition.UNDERGROUND_TERRAIN, "mineralsD2.png");
					this.map[index][TilePosition.UNDERGROUND_TERRAIN.value()] = rockEntity;
					this.entityBuilder.addNutrients(rockEntity, 0, this.getRandomNutrientValue(1, false, bType));
					this.entityBuilder.addToEngine(rockEntity);

				}
				else{
					Entity rockEntity = entityBuilder.createRenderable(q,r, 2,1,TilePosition.UNDERGROUND_TERRAIN, "mineralsD1.png");
					this.map[index][TilePosition.UNDERGROUND_TERRAIN.value()] = rockEntity;
					this.entityBuilder.addNutrients(rockEntity, 0, this.getRandomNutrientValue(0, false, bType));
					this.entityBuilder.addToEngine(rockEntity);

				}


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
