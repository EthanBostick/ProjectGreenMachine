package io.github.ethanBostick.core;

import com.badlogic.ashley.core.Entity;

import io.github.ethanBostick.ecs.Biome;
import io.github.ethanBostick.ecs.BiomeType;
import io.github.ethanBostick.ecs.Position;
import io.github.ethanBostick.ecs.MouseState;
import io.github.ethanBostick.ecs.Sprite;
import io.github.ethanBostick.utils.TextureUtils;

//libGDX stuff
import com.badlogic.ashley.core.Engine;

public class EntityBuilder {
	private static EntityBuilder theInstance = null;
    private Engine engine = null;

    public Entity createRenderable(int q, int r, int renderLayer, String texturePath){
		Entity entity = this.engine.createEntity();

		Sprite s = this.engine.createComponent(Sprite.class);
		Position p = this.engine.createComponent(Position.class);
        s.texture = TextureUtils.pathToTexture(texturePath);
        p.q = q;
		p.r = r;
		p.layer = renderLayer;

		entity.add(p);
		entity.add(s);

		return entity;
    }

    public void initHighlighter(Position p, Sprite s){
		Entity entity = this.engine.createEntity();

		p.q = 0;
		p.r = 0;
		p.layer = 99;

		entity.add(p);
		entity.add(s);

		this.addToEngine(entity);
    }

	public void addBiome(BiomeType b, Entity e){
		Biome biome = this.engine.createComponent(Biome.class);
		biome.biomeType = b;
		switch (b){
			case GRASS_LAND:
				biome.temp = 75;
				break;
			case DESERT:
				biome.temp = 100;
				break;
			case FOREST:
				biome.temp = 65;
				break;
			case TAIGA:
				biome.temp = 15;
				break;
			case BOREAL:
				biome.temp = 35;
				break;
			case MOUNTAIN:
				biome.temp = 50;
				break;
			case BLANK:
				biome.temp = -1;
				break;
		}
		e.add(biome);
	}

	public void initMouse(Position mousePosition, MouseState mouseState){
		Entity entity = this.engine.createEntity();
		entity.add(mousePosition);
		entity.add(mouseState);
		this.engine.addEntity(entity);
	}

	public void addToEngine(Entity e){
		this.engine.addEntity(e);
	}

	private EntityBuilder(Engine e){
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
