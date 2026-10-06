package io.github.ethanBostick.core;

import com.badlogic.ashley.core.Entity;

import io.github.ethanBostick.ecs.components.ActiveTool;
import io.github.ethanBostick.ecs.components.Region;
import io.github.ethanBostick.ecs.components.Build;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Cleansing;
import io.github.ethanBostick.ecs.components.Complete;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Density;
import io.github.ethanBostick.ecs.components.Depth;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Extraction;
import io.github.ethanBostick.ecs.components.MouseState;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Reclaim;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.VectorArrow;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

//libGDX stuff
import com.badlogic.ashley.core.PooledEngine;

public class EntityBuilder {
	private static EntityBuilder theInstance = null;
    private PooledEngine engine = null;

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

	public Entity createMycelium(int q, int r, int density, int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim){
		String texturePath = (density == 3)? "myceliumD3.png" : (density == 2)? "myceliumD2.png" : "myceliumD1.png";
		Entity e = createRenderable(q, r, 3, 1,TilePosition.MYCELIUM, texturePath);
		Direction direction = this.engine.createComponent(Direction.class);
		NutrientCapacity nutrientCapacity = this.engine.createComponent(NutrientCapacity.class);
		NutrientFlow nutrientFlow = this.engine.createComponent(NutrientFlow.class);

		nutrientFlow.maxThroughput = density;
		nutrientCapacity.carbonCapacity = 10*density;
		nutrientCapacity.mineralCapacity = 5*density;

		e.add(nutrientCapacity);
		e.add(nutrientFlow);
		e.add(direction);
		addDensity(e, density);		
		addNutrients(e, initCarbon, initMineral, carbonReclaim, mineralReclaim);

		return e;
	}

	public Entity createMyceliumExtractor(int q, int r, int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim){
		Entity e = createRenderable(q, r, 3, 1,TilePosition.MYCELIUM, "extractorMycelium.png");
		Direction direction = this.engine.createComponent(Direction.class);
		NutrientCapacity nutrientCapacity = this.engine.createComponent(NutrientCapacity.class);
		NutrientFlow nutrientFlow = this.engine.createComponent(NutrientFlow.class);
		Extraction extraction = this.engine.createComponent(Extraction.class);
		nutrientCapacity.carbonCapacity = 30;
		nutrientCapacity.mineralCapacity = 15;
		extraction.carbonRate = 5;
		extraction.mineralRate = 1;
		extraction.radius = 0;
		nutrientFlow.maxThroughput = 3;

		e.add(nutrientCapacity);
		e.add(nutrientFlow);
		e.add(direction);
		e.add(extraction);
		addDensity(e, 4);
		addNutrients(e, initCarbon, initMineral, carbonReclaim, mineralReclaim);

		return e;
	}

	public Entity createMyceliumCleanser(int q, int r, int initCarbon, int initMineral,int carbonReclaim,int mineralReclaim){
		Entity e = createRenderable(q, r, 3, 1,TilePosition.MYCELIUM, "cleanserMycelium.png");
		Direction direction = this.engine.createComponent(Direction.class);
		NutrientCapacity nutrientCapacity = this.engine.createComponent(NutrientCapacity.class);
		NutrientFlow nutrientFlow = this.engine.createComponent(NutrientFlow.class);
		Cleansing cleansing = this.engine.createComponent(Cleansing.class);
		nutrientCapacity.carbonCapacity = 20;
		nutrientCapacity.mineralCapacity = 20;
		cleansing.carbonCost = 1;
		cleansing.mineralCost = 1;
		cleansing.rate = 10f;
		cleansing.radius = 1;

		nutrientFlow.maxThroughput = 3;
		//actively requests nutrients
		nutrientFlow.needsCarbons = true;
		nutrientFlow.needsMinerals = true;

		e.add(nutrientCapacity);
		e.add(nutrientFlow);
		e.add(direction);
		e.add(cleansing);
		addDensity(e, 4);
		addNutrients(e, initCarbon, initMineral,carbonReclaim,mineralReclaim);

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

	public Entity createBuild(BuildType buildType, int q, int r, int carbonCost, int mineralCost){
		Entity build = this.engine.createEntity();
		Position position = this.engine.createComponent(Position.class);
		Sprite sprite = this.engine.createComponent(Sprite.class);
		NutrientCapacity nutrientCapacity = this.engine.createComponent(NutrientCapacity.class);
		NutrientFlow nutrientFlow = this.engine.createComponent(NutrientFlow.class);
		Nutrients nutrients = this.engine.createComponent(Nutrients.class);
		Build buildTarget = this.engine.createComponent(Build.class);

		position.q = q;
		position.r = r;
		position.layer = 3;

		nutrientCapacity.carbonCapacity = carbonCost;
		nutrientCapacity.mineralCapacity = mineralCost;

		nutrientFlow.carbonDistance = (carbonCost > 0)? 0: Integer.MAX_VALUE;
		nutrientFlow.mineralDistance = (mineralCost > 0)? 0: Integer.MAX_VALUE;

		nutrientFlow.isStorageNode = false;
		nutrientFlow.maxThroughput = 0;
		nutrientFlow.needsCarbons = carbonCost > 0;
		nutrientFlow.needsMinerals = mineralCost > 0;

		buildTarget.buildType = buildType;
		sprite.texture = TextureUtils.pathToTexture(buildType.spritePath());
		
		build.add(nutrients);
		build.add(position);
		build.add(sprite);
		build.add(nutrientCapacity);
		build.add(nutrientFlow);
		build.add(buildTarget);

		return build; 
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

		vectorArrow.arrowTexture = TextureUtils.pathToTexture("dragArrow.png");

		entity.add(vectorArrow);
		entity.add(mousePosition);
		entity.add(mouseState);
		entity.add(mtp);

		this.engine.addEntity(entity);
		return entity;
	}

	public Entity initPlayer(){
		Depth depth = this.engine.createComponent(Depth.class);
		ActiveTool tool = this.engine.createComponent(ActiveTool.class);
		Entity entity = this.engine.createEntity();

		entity.add(tool);
		entity.add(depth);

		this.engine.addEntity(entity);
		return entity;
	}

	public void addRegion(RegionType b, Entity e){
		Region region = this.engine.createComponent(Region.class);
		region.regionType = b;
		e.add(region);
	}

	public void addDensity(Entity e, int density){
		Density d = this.engine.createComponent(Density.class);
		d.density = density;
		e.add(d);
	}

	public void addNutrients(Entity e, int carbonAmount, int mineralAmount, int cReclaim, int mReclaim){
		Nutrients n = this.engine.createComponent(Nutrients.class);
		n.carbonReclaim = cReclaim;
		n.mineralReclaim = mReclaim;
		n.carbons = carbonAmount;
		n.minerals = mineralAmount;
		e.add(n);
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

	public void dead(Entity e){
		Dead dead = this.engine.createComponent(Dead.class);
		e.add(dead);
	}

	public void reclaim(Entity e){
		Reclaim r = this.engine.createComponent(Reclaim.class);
		e.add(r);
	}

	public void addToEngine(Entity e){
		this.engine.addEntity(e);
	}


	private EntityBuilder(PooledEngine e){
        this.engine = e;
    }

	public static EntityBuilder instance(PooledEngine e){
		if (EntityBuilder.theInstance == null){
			EntityBuilder.theInstance = new EntityBuilder(e);
		}
		return EntityBuilder.theInstance;
	}
	public static EntityBuilder instance(){
		return EntityBuilder.theInstance;
	}
}
