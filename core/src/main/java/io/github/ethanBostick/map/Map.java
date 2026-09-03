package io.github.ethanBostick.map;

import io.github.ethanBostick.core.HexFactory;
import io.github.ethanBostick.utils.HexUtils;

import java.lang.Math;
import java.util.Random;

//GDX stuff
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Map {
	private static Map theInstance = null;
	private int size = 0;
	private static Random random = null;

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
					
					if (rock == 1){
						hexFactory.createHex(q,r, "rock.png");
					}
					if (rInt > 1 && rInt < 4){
						hexFactory.createHex(q,r, "grass.png");
					}
					else if (rInt > 3){
						hexFactory.createHex(q,r, "sand.png");
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
}
