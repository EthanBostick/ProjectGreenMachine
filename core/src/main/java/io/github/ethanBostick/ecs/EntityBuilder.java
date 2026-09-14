package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.Entity;

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

    public Entity initSelectTool(){
		Entity entity = this.engine.createEntity();
		Sprite s = this.engine.createComponent(Sprite.class);
		Position p = this.engine.createComponent(Position.class);

		p.q = 0;
		p.r = 0;
		p.layer = 99;

		entity.add(p);
		entity.add(s);

		this.addToEngine(entity);
		return entity;
    }

    public Entity initDragTool(){
		Entity entity = this.engine.createEntity();
		MultiTileSprite mts = this.engine.createComponent(MultiTileSprite.class);
		MultiTilePosition mtp = this.engine.createComponent(MultiTilePosition.class);

		mtp.layer = 99;
		mts.texture = TextureUtils.pathToTexture("pathSelect.png");
		mts.textureHead = TextureUtils.pathToTexture("pathSelectHead.png");

		entity.add(mtp);
		entity.add(mts);

		this.addToEngine(entity);
		return entity;
    }

	public Entity initMouse(){
		MouseState mouseState = this.engine.createComponent(MouseState.class);
		Position mousePosition = this.engine.createComponent(Position.class);
		Entity entity = this.engine.createEntity();

		entity.add(mousePosition);
		entity.add(mouseState);

		this.engine.addEntity(entity);
		return entity;
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
