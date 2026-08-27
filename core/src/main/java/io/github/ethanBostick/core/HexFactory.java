package io.github.ethanBostick.core;

import com.badlogic.ashley.core.Entity;
import io.github.ethanBostick.ecs.Position;
import io.github.ethanBostick.ecs.Sprite;

//libGDX stuff
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

public class HexFactory {
	private static HexFactory theInstance = null;
    private Engine engine = null;
	private ObjectMap<String, Texture> textureMap;

    public void createHex(int q, int r, String texturePath){
		Entity hex = this.engine.createEntity();
		Texture texture = this.textureMap.get(texturePath); 

		if (texture == null){
			texture = new Texture(Gdx.files.internal(texturePath));
			this.textureMap.put(texturePath,texture);
            System.out.println("new texture made!");
		}
            System.out.println("reusing texture: " + texturePath);

		Sprite s = this.engine.createComponent(Sprite.class);
		Position p = this.engine.createComponent(Position.class);
        s.texture = texture;
        p.q = q;
		p.r = r;

		hex.add(p);
		hex.add(s);
		this.engine.addEntity(hex);

    }

	private HexFactory(Engine e){
		this.textureMap = new ObjectMap<>(2); //argue estimated size
        this.engine = e;
    }

	public static HexFactory instance(Engine e){
		if (HexFactory.theInstance == null){
			HexFactory.theInstance = new HexFactory(e);
		}
		return HexFactory.theInstance;
	}
	public static HexFactory instance(){
		return HexFactory.theInstance;
	}
}
