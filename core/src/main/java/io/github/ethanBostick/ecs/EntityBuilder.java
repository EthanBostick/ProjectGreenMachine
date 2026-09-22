package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;

import io.github.ethanBostick.map.BiomeType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

//libGDX stuff
import com.badlogic.ashley.core.Engine;

public class EntityBuilder {
	private static EntityBuilder theInstance = null;
    private Engine engine = null;

    public Entity createRenderable(int q, int r, int renderLayer, int depth, TilePosition tilePosition, String texturePath){
		Entity entity = this.engine.createEntity();

		Depth d = this.engine.createComponent(Depth.class);
		Sprite s = this.engine.createComponent(Sprite.class);
		Position p = this.engine.createComponent(Position.class);

		d.depth = depth;
        s.texture = TextureUtils.pathToTexture(texturePath);
        p.q = q;
		p.r = r;
		p.layer = renderLayer;
		p.tilePosition = tilePosition;

		entity.add(p);
		entity.add(d);
		entity.add(s);

		return entity;
    }

	public Entity createMycelium(int q, int r, int density, int initCarbon, int initMineral){
		Entity e = createRenderable(q, r, 1, 1,TilePosition.MYCELIUM, "myceliumD1.png");
		Direction direction = this.engine.createComponent(Direction.class);
		NutrientCapacity nutrientCapacity = this.engine.createComponent(NutrientCapacity.class);
		NutrientDraw nutrientDraw = this.engine.createComponent(NutrientDraw.class);
		nutrientCapacity.carbonCapacity = 10*density;
		nutrientCapacity.mineralCapacity = 5*density;

		e.add(nutrientCapacity);
		e.add(nutrientDraw);
		e.add(direction);
		addDensity(e, density);		
		addNutrients(e, initCarbon, initMineral);

		return e;
	}

	public Entity createMyceliumRunner(int startQ, int startR, int endQ, int endR ){
		Entity runner = this.engine.createEntity();
		Sprite targetHighlight = this.engine.createComponent(Sprite.class);
		Position targetPosition = this.engine.createComponent(Position.class);
		MultiTilePosition startAndEnd = this.engine.createComponent(MultiTilePosition.class);
		Direction initDirection = this.engine.createComponent(Direction.class);
		Complete status = this.engine.createComponent(Complete.class);
		Depth depth = this.engine.createComponent(Depth.class);

		targetHighlight.texture = TextureUtils.pathToTexture("runnerTarget.png");
		targetPosition.q = endQ;
		targetPosition.r = endR;
		depth.depth = 1;

		startAndEnd.points.add(startQ);
		startAndEnd.points.add(startR);
		startAndEnd.points.add(endQ);
		startAndEnd.points.add(endR);

		runner.add(depth);
		runner.add(targetHighlight);
		runner.add(targetPosition);
		runner.add(startAndEnd);
		runner.add(initDirection);
		runner.add(status);

		return runner;
	}

    public Entity initHighlighter(){
		Entity entity = this.engine.createEntity();
		Sprite s = this.engine.createComponent(Sprite.class);
		Position p = this.engine.createComponent(Position.class);

		p.q = 0;
		p.r = 0;
		p.layer = 99;
		p.tilePosition = TilePosition.SURFACE;

		entity.add(p);
		entity.add(s);

		this.addToEngine(entity);
		return entity;
    }

	public Entity initMouse(){
		Entity entity = this.engine.createEntity();
		MouseState mouseState = this.engine.createComponent(MouseState.class);
		Position mousePosition = this.engine.createComponent(Position.class);
		MultiTilePosition mtp = this.engine.createComponent(MultiTilePosition.class);
		VectorArrow vectorArrow = this.engine.createComponent(VectorArrow.class);
		ActiveTool tool = this.engine.createComponent(ActiveTool.class);

		vectorArrow.arrowTexture = TextureUtils.pathToTexture("dragArrow.png");

		entity.add(vectorArrow);
		entity.add(mousePosition);
		entity.add(mouseState);
		entity.add(mtp);
		entity.add(tool);

		this.engine.addEntity(entity);
		return entity;
	}

	public Entity initPlayer(){
		Depth depth = this.engine.createComponent(Depth.class);
		Entity entity = this.engine.createEntity();

		entity.add(depth);

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

	public void addDensity(Entity e, int density){
		Density d = this.engine.createComponent(Density.class);
		d.density = density;
		e.add(d);
	}

	public void addNutrients(Entity e, int carbonAmount, int mineralAmount){
		Nutrients n = this.engine.createComponent(Nutrients.class);
		n.carbons = carbonAmount;
		n.minerals = mineralAmount;
		e.add(n);
	}

	public void addNutrients(Entity e){
		Nutrients n = this.engine.createComponent(Nutrients.class);
		e.add(n);
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
