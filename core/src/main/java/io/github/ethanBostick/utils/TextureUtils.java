package io.github.ethanBostick.utils;

import io.github.ethanBostick.ecs.Direction;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.Gdx;

public class TextureUtils {
    private static ObjectMap<String, Texture> textureMap = new ObjectMap<>(10); //argue estimated size
	private static String[] dragToolSpriteHeads = {"arrowNE.png","arrowSE.png","arrowS.png","arrowSW.png","arrowNW.png","arrowN.png"};
	private static String[] dragToolSprites = {"topRightPath.png","topLeftPath.png","topPath.png","topRightPath.png","topLeftPath.png","topPath.png"};

	private static int getIndex(int q, int r){
		// Matches {1, 0} to index 0, and {1, -1} to index 1
		if (q == 1) return (r == 0) ? 0 : 1;
		
		// Matches {0, -1} to index 2, and {0, 1} to index 5
		if (q == 0) return (r == -1) ? 2 : 5;
		
		// Matches {-1, 0} to index 3, and {-1, 1} to index 4
		if (q == -1) return (r == 0) ? 3 : 4;
		
		return -1; // Fallback if the vector is not a direct neighbor
	}

    public static Texture pathToTexture(String texturePath){
		Texture texture = textureMap.get(texturePath); 

		if (texture == null){
			texture = new Texture(Gdx.files.internal(texturePath));
			textureMap.put(texturePath,texture);
		}
        return texture;
    }

    public static Texture dirToTexture(int key, Direction dir){
		Texture rtn=null;
		switch(key){
			case 0:
				rtn=pathToTexture(dragToolSpriteHeads[getIndex(dir.directionVector[0], dir.directionVector[1])]);
				break;
			case 1:
				rtn=pathToTexture(dragToolSprites[getIndex(dir.directionVector[0], dir.directionVector[1])]);
				break;

		}
		return rtn;
    }

    public static Texture dirToTexture(int key, int[] dirVec){
		Texture rtn=null;
		switch(key){
			case 0:
				rtn=pathToTexture(dragToolSpriteHeads[getIndex(dirVec[0], dirVec[1])]);
				break;
			case 1:
				rtn=pathToTexture(dragToolSprites[getIndex(dirVec[0], dirVec[1])]);
				break;
		}
		return rtn;
    }
}
