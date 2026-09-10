package io.github.ethanBostick.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.Gdx;

public class TextureUtils {
    private static ObjectMap<String, Texture> textureMap = new ObjectMap<>(10); //argue estimated size

    public static Texture pathToTexture(String texturePath){
		Texture texture = textureMap.get(texturePath); 

		if (texture == null){
			texture = new Texture(Gdx.files.internal(texturePath));
			textureMap.put(texturePath,texture);
		}
        return texture;
    }
}
