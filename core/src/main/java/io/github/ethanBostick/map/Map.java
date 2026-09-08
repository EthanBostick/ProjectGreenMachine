package io.github.ethanBostick.map;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.ecs.BiomeType;
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

	public Entity getEntityAt(int q, int r, TilePosition t){

		if(Math.abs(q) >= this.size || Math.abs(r) >= this.size || Math.abs(q+r) >= this.size){
			return null;
		}

		int i = getIndex(q, r);
		return this.map[i].get(t);
	}

	@SuppressWarnings("unchecked")
	public void initMap(int size){
		this.size = size;
		this.w = (size*2) + 1;
		this.map = new ObjectMap[w*w];

		for (int q = -this.size ; q < this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r < Math.min(this.size, -q + this.size); r ++){
				
				int index = this.getIndex(q, r);
				this.map[index] = new ObjectMap<TilePosition,Entity>(2); 

				// entityBuilder.createHex(q,r, "hex_template.png");
				int rInt = this.random.nextInt(16);
				int rock = this.random.nextInt(4);
				
				Entity tileEntity = null;
				Entity rockEntity = null;

				if (rInt <= 3){
					tileEntity = entityBuilder.createRenderable(q,r,0, "hex_template.png");
					entityBuilder.addBiome(BiomeType.BLANK, tileEntity);
				}
				else if (rInt <= 5){
					tileEntity = entityBuilder.createRenderable(q,r, 0, "sand.png");
					entityBuilder.addBiome(BiomeType.DESERT, tileEntity);
				}
				else if (rInt <= 7){
					tileEntity = entityBuilder.createRenderable(q,r,0, "grass.png");
					entityBuilder.addBiome(BiomeType.FOREST, tileEntity);
				}
				else if (rInt <= 9){
					tileEntity = entityBuilder.createRenderable(q,r,0, "taiga.png");
					entityBuilder.addBiome(BiomeType.TAIGA, tileEntity);
				}
				else if (rInt <= 11){
					tileEntity = entityBuilder.createRenderable(q,r,0, "boreal.png");
					entityBuilder.addBiome(BiomeType.BOREAL, tileEntity);
				}
				else if (rInt <= 13){
					tileEntity = entityBuilder.createRenderable(q,r,0, "mountain.png");
					entityBuilder.addBiome(BiomeType.MOUNTAIN, tileEntity);
				}
				else{
					tileEntity = entityBuilder.createRenderable(q,r,0, "grassLand.png");
					entityBuilder.addBiome(BiomeType.GRASS_LAND, tileEntity);
				}

					this.entityBuilder.addToEngine(tileEntity);
					this.map[index].put(TilePosition.TILE,tileEntity);

				// if (rock == 1){
				// 	rockEntity = entityBuilder.createRenderable(q,r, 2, "rock.png");
				// 	this.map[index].put(TilePosition.ENTITY, rockEntity);
				// 	this.entityBuilder.addToEngine(rockEntity);
				// }

				if (q == 0 && r == 0){
					//do not track, for visual only
					Entity e = entityBuilder.createRenderable(0,0, 3, "hexCenter.png");
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
