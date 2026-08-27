package io.github.ethanBostick.map;

import io.github.ethanBostick.core.HexFactory;
import io.github.ethanBostick.utils.HexUtils;

import java.lang.Math;

//GDX stuff
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Map {
	private static Map theInstance = null;
	private int size = 0;

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
					hexFactory.createHex(q,r, "hex_template.png");
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
