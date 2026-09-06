package io.github.ethanBostick.map;

import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventType;

import java.lang.Math;
import java.util.Random;

import com.badlogic.ashley.core.Entity;
//GDX stuff
import com.badlogic.gdx.utils.Array;
<<<<<<< HEAD
=======
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
>>>>>>> e7fcbfae8b1747592887535f2ffbc699234e6598

public class Map implements Observer{
	private static Map theInstance = null;
	private int size = 0;
<<<<<<< HEAD
	private int w = 0;
	private Random random = null;
	private Array<Entity>[] map;
	private EntityBuilder entityBuilder = null;
=======
	private static Random random = null;
	private 
	//private ObjectMap<, Array<Entity>> map;
>>>>>>> e7fcbfae8b1747592887535f2ffbc699234e6598

	private Map(){
		this.random = new Random();
		this.entityBuilder = EntityBuilder.instance();
	}

	public void setMapPosition(int q, int r, Entity e){
		int i = getIndex(q, r);
		this.map[i].add(e);
	}

	private int getIndex(int q, int r){
		return (q + this.size) + ((r+this.size) * w);
	}

	public Array<Entity> getMapPosition(int q, int r){
		int i = getIndex(q, r);

		return this.map[i];
	}

	public void initMap(int size){
		this.size = size;
		this.w = (size*2) + 1;
		this.map = new Array[w*w];

		for (int q = -this.size ; q < this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r < Math.min(this.size, -q + this.size); r ++){
				
				int index = this.getIndex(q, r);
				this.map[index] = new Array<Entity>(false,1); 

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
					this.map[index].add(tileEntity);

				if (rock == 1){
					rockEntity = entityBuilder.createRenderable(q,r, 2, "rock.png");
					this.map[index].add(rockEntity);
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
		switch (event.getType()){
			case ZOOM:
                ZoomEvent zm = (ZoomEvent) event;
                this.zoom(zm.amount);
				break;
			default:
				System.out.println("unknown event");
		}
    }
}
