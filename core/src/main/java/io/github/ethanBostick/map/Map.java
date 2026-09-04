package io.github.ethanBostick.map;

import io.github.ethanBostick.core.HexFactory;
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
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

public class Map implements Observer{
	private static Map theInstance = null;
	private int size = 0;
	private static Random random = null;
	private 
	//private ObjectMap<, Array<Entity>> map;

	private Map(){}

	public void generateMap(int size){
		this.size = size;
		HexFactory hexFactory = HexFactory.instance();

		for (int q = -this.size ; q < this.size ; q ++){
			for (int r = Math.max(-this.size, -q - this.size); r < Math.min(this.size, -q + this.size); r ++){
				if (q == 0 && r == 0){
					hexFactory.createHex(0,0, "hexCenter.png");
				}
				else{
					// hexFactory.createHex(q,r, "hex_template.png");
					hexFactory.createHex(q,r, "dirt.png");
					int rInt = random.nextInt(7);
					int rock = random.nextInt(4);
					
					if (rInt > 1 && rInt < 4){
						hexFactory.createHex(q,r, "grass.png");
					}
					else if (rInt > 3){
						hexFactory.createHex(q,r, "sand.png");
					}

					if (rock == 1){
						hexFactory.createHex(q,r, "rock.png");
					}
				}
			}
		}

	}

	public static Map instance(){
		if (Map.theInstance == null){
			Map.theInstance = new Map();
			Map.random = new Random();
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
