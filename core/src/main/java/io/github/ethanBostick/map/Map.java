package io.github.ethanBostick.map;

import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.ecs.BiomeType;
import io.github.ethanBostick.ecs.EntityBuilder;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventType;

import java.lang.Math;
import java.util.Random;

//GDX stuff
import com.badlogic.gdx.utils.Array;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;

public class Map implements Observer{
	private static Map theInstance = null;
	private int size = 0;
	private int w = 0;
	public Random random = null;
	private ObjectMap<TilePosition,Entity>[] map;
	private EntityBuilder entityBuilder = null;

	private Map(){
		this.random = new Random();
		this.entityBuilder = EntityBuilder.instance();
	}

	public void setEntityAt(int q, int r, Entity e, TilePosition t){
		int i = getIndex(q, r);
		this.map[i].put(t, e);
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
		return this.map[i].get(t);
	}

	@SuppressWarnings("unchecked")
	public void initMap(int size, double[] concentrations,double thresh, int startingArea){
		this.size = size;
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
		MapGenerator mapGenerator = new MapGenerator(this.size, thresh, initGrid);
		initGrid = mapGenerator.generate(999999,20);

		this.map = new ObjectMap[w*w];
		for (int q = -this.size ; q <= this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r <= Math.min(this.size, -q + this.size); r ++){
				
				int index = this.getIndex(q, r);
				double rInt = this.random.nextDouble();
				this.map[index] = new ObjectMap<TilePosition,Entity>(2); 

				Entity tileEntity = null;
				String tilePng = "hex_template.png";
				String extraPng = null;
				BiomeType bType = BiomeType.BLANK;

				switch(initGrid[index]){
					case 65:
						tilePng = "forestFloor.png";
						bType = BiomeType.FOREST;						
						if (rInt <= 0.45){
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
						if (rInt <= 0.6){
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
						if (rInt <= 0.25){
							extraPng = "pineTrees1.png";							
						}
						break;
					case 35:
						tilePng = "boreal.png";
						bType = BiomeType.BOREAL;						
						if (rInt <= 0.7){
							extraPng = "pineTrees1.png";							
						}
						break;
					case -1:
						tilePng = "mountain.png";
						bType = BiomeType.MOUNTAIN;						
						break;
				}
				tileEntity = entityBuilder.createRenderable(q,r,0,0, tilePng);
				entityBuilder.addBiome(bType, tileEntity);
				if(extraPng != null){
					Entity terrainEntity = entityBuilder.createRenderable(q,r,1, 0, extraPng);
					this.map[index].put(TilePosition.INANIMATE,terrainEntity);
					this.entityBuilder.addToEngine(terrainEntity);
				}
				this.entityBuilder.addToEngine(tileEntity);
				this.map[index].put(TilePosition.SURFACE,tileEntity);

				//underground gen
				Entity underEntity = entityBuilder.createRenderable(q,r,0,1, "dirt.png");
				entityBuilder.addBiome(bType, underEntity);
				this.entityBuilder.addToEngine(underEntity);
				this.map[index].put(TilePosition.UNDERGROUND,underEntity);
				// if (rock == 1){
				// 	rockEntity = entityBuilder.createRenderable(q,r, 2, "rock.png");
				// 	this.map[index].put(TilePosition.INANIMATE, rockEntity);
				// 	this.entityBuilder.addToEngine(rockEntity);
				// }

				if (q == 0 && r == 0){
					//do not track, for visual only
					Entity e = entityBuilder.createRenderable(0,0, 3,0, "hexCenter.png");
					this.entityBuilder.addToEngine(e);
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

    @Override
    public void onEvent(Event event){
		// switch (event.getType()){
		// 	case ZOOM:
        //         ZoomEvent zm = (ZoomEvent) event;
        //         this.zoom(zm.amount);
		// 		break;
		// 	default:
		// 		System.out.println("unknown event");
		// }
    }
}
