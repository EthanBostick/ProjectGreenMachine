package io.github.ethanBostick.core;

import com.badlogic.ashley.core.Entity;
import io.github.ethanBostick.ecs.Position;
import io.github.ethanBostick.ecs.Sprite;

//libGDX stuff
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

public class EntityBuilder {
	private static EntityBuilder theInstance = null;
    private Engine engine = null;
	private ObjectMap<String, Texture> textureMap;

    public Entity createRenderable(int q, int r, int renderLayer, String texturePath){
		Entity entity = this.engine.createEntity();
		Texture texture = this.textureMap.get(texturePath); 

		if (texture == null){
			texture = new Texture(Gdx.files.internal(texturePath));
			this.textureMap.put(texturePath,texture);
		}

		Sprite s = this.engine.createComponent(Sprite.class);
		Position p = this.engine.createComponent(Position.class);
        s.texture = texture;
        p.q = q;
		p.r = r;
		p.layer = renderLayer;

		entity.add(p);
		entity.add(s);

		return entity;
    }

	public void addToEngine(Entity e){
		this.engine.addEntity(e);
	}

	private EntityBuilder(Engine e){
		this.textureMap = new ObjectMap<>(2); //argue estimated size
        this.engine = e;
    }

	public static EntityBuilder instance(Engine e){
		if (EntityBuilder.theInstance == null){
			EntityBuilder.theInstance = new EntityBuilder(e);
		}
		return EntityBuilder.theInstance;
	}
	public static EntityBuilder instance(){
		return EntityBuilder.theInstance;
	}
}
