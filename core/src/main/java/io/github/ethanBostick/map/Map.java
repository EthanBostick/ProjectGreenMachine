package io.github.ethanBostick.map;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.core.Observer;
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

	public void setMapPosition(int q, int r, Entity e, TilePosition t){
		int i = getIndex(q, r);
		this.map[i].put(t, e);
	}

	public int getMapSize(){
		return this.size;
	}

	public int getIndex(int q, int r){
		return (q + this.size) + ((r+this.size) * w);
	}

	public Entity getMapPosition(int q, int r, TilePosition t){

		if(Math.abs(q) >= this.size || Math.abs(r) >= Math.min(this.size, -q + this.size)){
			return null;
		}

		int i = getIndex(q, r);
		return this.map[i].get(t);
	}

	public void initMap(int size){
		this.size = size;
		this.w = (size*2) + 1;
		this.map = new ObjectMap[w*w];

		for (int q = -this.size ; q < this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r < Math.min(this.size, -q + this.size); r ++){
				
				int index = this.getIndex(q, r);
				this.map[index] = new ObjectMap<TilePosition,Entity>(2); 

				// entityBuilder.createHex(q,r, "hex_template.png");
				int rInt = this.random.nextInt(7);
				int rock = this.random.nextInt(4);
				
				Entity tileEntity = null;
				Entity rockEntity = null;

				if (rInt > 1 && rInt < 4){
					tileEntity = entityBuilder.createRenderable(q,r,1, "grass.png");
				}
				else if (rInt > 3){
					tileEntity = entityBuilder.createRenderable(q,r, 0, "sand.png");
				}
				else{
					tileEntity = entityBuilder.createRenderable(q,r,0, "dirt.png");
				}

					this.entityBuilder.addToEngine(tileEntity);
					this.map[index].put(TilePosition.TILE,tileEntity);

				if (rock == 1){
					rockEntity = entityBuilder.createRenderable(q,r, 2, "rock.png");
					this.map[index].put(TilePosition.ENTITY, rockEntity);
					this.entityBuilder.addToEngine(rockEntity);
				}

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
