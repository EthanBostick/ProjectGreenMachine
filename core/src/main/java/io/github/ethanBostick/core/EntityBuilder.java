package io.github.ethanBostick.core;

import com.badlogic.ashley.core.Entity;
//libGDX stuff
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntArray;

import io.github.ethanBostick.ecs.components.ActiveTool;
import io.github.ethanBostick.ecs.components.Build;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Cleanser;
import io.github.ethanBostick.ecs.components.Complete;
import io.github.ethanBostick.ecs.components.Cultivator;
import io.github.ethanBostick.ecs.components.Dead;
import io.github.ethanBostick.ecs.components.Density;
import io.github.ethanBostick.ecs.components.Depth;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Extraction;
import io.github.ethanBostick.ecs.components.FactoryEmitter;
import io.github.ethanBostick.ecs.components.FloatingText;
import io.github.ethanBostick.ecs.components.Green;
import io.github.ethanBostick.ecs.components.Health;
import io.github.ethanBostick.ecs.components.MouseState;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
import io.github.ethanBostick.ecs.components.MultiTileSprite;
import io.github.ethanBostick.ecs.components.Name;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.NutrientFlow;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.PriorityRole;
import io.github.ethanBostick.ecs.components.Reclaim;
import io.github.ethanBostick.ecs.components.Region;
import io.github.ethanBostick.ecs.components.Resource;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.ecs.components.ToxicWave;
import io.github.ethanBostick.ecs.components.VectorArrow;
import io.github.ethanBostick.ecs.components.Vision;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.RegionType;
import io.github.ethanBostick.map.ResourceType;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;

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

    public Entity createRenderable(int q, int r, int renderLayer, int depth, TilePosition tilePosition, String texturePath, String name){
		Entity entity = this.createRenderable(q, r, renderLayer, depth, tilePosition, texturePath);
		Name nameComp = this.engine.createComponent(Name.class);

		nameComp.name = name;

		entity.add(nameComp);

		return entity;
    }

	// --- Shared Base Builder ---

    private Entity createBaseMycelium(int q, int r, String texturePath, int density,
                                      int carbonCap, int mineralCap, int maxThroughput,
                                      int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim) {
        Entity e = createRenderable(q, r, 3, 1, TilePosition.MYCELIUM, texturePath);

        Direction direction = this.engine.createComponent(Direction.class);
        NutrientCapacity nutrientCapacity = this.engine.createComponent(NutrientCapacity.class);
        NutrientFlow nutrientFlow = this.engine.createComponent(NutrientFlow.class);
        Health health = this.engine.createComponent(Health.class);
		Vision vision = this.engine.createComponent(Vision.class);

        health.health = 100;
        nutrientCapacity.carbonCapacity = carbonCap;
        nutrientCapacity.mineralCapacity = mineralCap;
        nutrientFlow.maxThroughput = maxThroughput;
		nutrientFlow.mineralRole = PriorityRole.NONE;
		nutrientFlow.carbonRole = PriorityRole.NONE;

		e.add(vision);
        e.add(direction);
        e.add(nutrientCapacity);
        e.add(nutrientFlow);
        e.add(health);

        addDensity(e, density);
        addNutrients(e, initCarbon, initMineral, carbonReclaim, mineralReclaim);

        return e;
    }

    // --- Specialized Entity Builders ---

    public Entity createMycelium(int q, int r, int density, int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim) {
        String texturePath = (density == 3) ? "myceliumD3.png" : (density == 2) ? "myceliumD2.png" : "myceliumD1.png";
        
        Entity e = createBaseMycelium(q, r, texturePath, density,
                10 * density, 5 * density, density,
                initCarbon, initMineral, carbonReclaim, mineralReclaim);


        return e;
    }

    public Entity createMyceliumStorage(int q, int r, int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim) {
        String texturePath = "storageMycelium.png";
        
        Entity e = createBaseMycelium(q, r, texturePath, 4,
                60, 45, 2,
                initCarbon, initMineral, carbonReclaim, mineralReclaim);

        NutrientFlow flow = e.getComponent(NutrientFlow.class);
		flow.mineralRole = PriorityRole.SURPLUS_CAPACITOR;
		flow.carbonRole = PriorityRole.SURPLUS_CAPACITOR;

        return e;
    }

    public Entity createMyceliumExtractor(int q, int r, int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim) {
        Entity e = createBaseMycelium(q, r, "extractorMycelium.png", 4,
                30, 15, 3,
                initCarbon, initMineral, carbonReclaim, mineralReclaim);

        Extraction extraction = this.engine.createComponent(Extraction.class);
        extraction.carbonRate = 5;
        extraction.mineralRate = 1;
        extraction.radius = 0;
        e.add(extraction);

        return e;
    }

    public Entity createMyceliumCultivator(int q, int r, int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim) {
        Entity e = createBaseMycelium(q, r, "cultivatorMycelium.png", 4,
                20, 20,2,
                initCarbon, initMineral, carbonReclaim, mineralReclaim);

        NutrientFlow flow = e.getComponent(NutrientFlow.class);
		Cultivator cultivatorComp = this.engine.createComponent(Cultivator.class);

		flow.mineralRole = PriorityRole.UPKEEP;
		flow.carbonRole = PriorityRole.UPKEEP;
		cultivatorComp.radius = 1;

		e.add(cultivatorComp);

        return e;
    }

    public Entity createMyceliumCleanser(int q, int r, int initCarbon, int initMineral, int carbonReclaim, int mineralReclaim) {
        Entity e = createBaseMycelium(q, r, "cleanser.png", 4,
                20, 20, 2,
                initCarbon, initMineral, carbonReclaim, mineralReclaim);

        NutrientFlow flow = e.getComponent(NutrientFlow.class);
		flow.mineralRole = PriorityRole.UPKEEP;
		flow.carbonRole = PriorityRole.UPKEEP;

        Cleanser cleanser = this.engine.createComponent(Cleanser.class);
        cleanser.carbonCost = 1;
        cleanser.mineralCost = 1;
        cleanser.rate = 10f;
        cleanser.radius = 1;
        e.add(cleanser);

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
		targetHighlight.revealed = true;
		targetPosition.q = endQ;
		targetPosition.r = endR;
		targetPosition.layer = 99; //so it renders on top
		depth.depth = 1;

		startAndEnd.points = new IntArray(false,4);
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
		Depth depth = this.engine.createComponent(Depth.class);

		position.q = q;
		position.r = r;
		position.layer = 3;
		position.tilePosition = TilePosition.MYCELIUM;

		depth.depth = 1;

		nutrientCapacity.carbonCapacity = carbonCost;
		nutrientCapacity.mineralCapacity = mineralCost;

		nutrientFlow.carbonRole = (carbonCost > 0)? PriorityRole.TOP: PriorityRole.NONE;
		nutrientFlow.mineralRole = (mineralCost > 0)? PriorityRole.TOP: PriorityRole.NONE;

		nutrientFlow.maxThroughput = 0;

		buildTarget.buildType = buildType;
		sprite.texture = TextureUtils.pathToTexture(buildType.spritePath);
		
		build.add(depth);
		build.add(nutrients);
		build.add(position);
		build.add(sprite);
		build.add(nutrientCapacity);
		build.add(nutrientFlow);
		build.add(buildTarget);

		return build; 
	}

	public Entity createGreen(int q, int r, int level){
		//render lower than mountains
		Entity green = this.createRenderable(q, r, 1, 0, TilePosition.SURFACE_ENTITY, "greenS" + level +".png");
		NutrientCapacity nutrientCapacity = this.engine.createComponent(NutrientCapacity.class);
		Nutrients nutrients = this.engine.createComponent(Nutrients.class);
		Health health = this.engine.createComponent(Health.class);
		Green greenComp = this.engine.createComponent(Green.class);

		greenComp.mineralUsage = CommonValues.GREEN_DECAY_MINERAL_PER_TICK.value;
		greenComp.carbonOutput = (CommonValues.GREEN_BASE_CARBON_OUTPUT_PER_HEX_PER_TICK.value * level); 
		nutrientCapacity.mineralCapacity = (int)(CommonValues.GREEN_BASE_LEVEL_SCALAR.value * (level - 1));
		health.health = 100;

		this.addDensity(green, level);
		green.add(health);
		green.add(nutrients);
		green.add(nutrientCapacity);
		green.add(greenComp);

		return green;
	}

	public Entity createWaveEmitter(int q, int r, RegionType hazardType, int cleanseRequirement, int maxSpreadRadius, int pulseIntervalTicks){
		//render below mountains (L2), on surface depth
		Entity factory = this.createRenderable(q, r, 1, 0, TilePosition.SURFACE_ENTITY, "factory.png");
		FactoryEmitter emitter = this.engine.createComponent(FactoryEmitter.class);

		emitter.cleanseRequirement = cleanseRequirement;
		emitter.maxSpreadRadius = maxSpreadRadius;
		emitter.pulseIntervalTicks = pulseIntervalTicks;
		emitter.hazardType = hazardType;

		factory.add(emitter);
		return factory;
	}

	public Entity createToxicWave(int emitterQ, int emitterR, RegionType hazardType, int maxSteps, int displayDepth){
		Entity e = this.engine.createEntity();
		ToxicWave wave = this.engine.createComponent(ToxicWave.class); 
		MultiTilePosition wavePosition = this.engine.createComponent(MultiTilePosition.class);
		Position emitterPosition = this.engine.createComponent(Position.class);
		MultiTileSprite waveSprite = this.engine.createComponent(MultiTileSprite.class);
		Depth depth = this.engine.createComponent(Depth.class);

		String texturePath = (hazardType == RegionType.RADIOACTIVE)? "radioactiveWave.png": (hazardType == RegionType.POLLUTED)? "pollutedWave.png": "barrenWave.png";
		depth.depth = displayDepth;
		waveSprite.textureHead = TextureUtils.pathToTexture(texturePath);
		wavePosition.points = new IntArray(false,64);
		emitterPosition.q = emitterQ;
		emitterPosition.r = emitterR;
		wavePosition.layer = 1;
		wave.visitedHexes = new IntArray(false, 64);
		wave.maxSteps = maxSteps;
		wave.hazardType = hazardType;

		e.add(depth);
		e.add(wave);
		e.add(wavePosition);
		e.add(emitterPosition);
		e.add(waveSprite);

		return e;
	}

	public Entity createFloatingText(int q, int r, int displayDepth, String text, Color color, boolean randomDeviate) {
		Entity e = this.engine.createEntity();
		
		FloatingText ft = this.engine.createComponent(FloatingText.class);
		Depth depth = this.engine.createComponent(Depth.class);

		depth.depth = displayDepth;
		ft.text = text;

		//random deviation 
        float XDeviate = (randomDeviate)?(float)(3d * Map.instance().random.nextGaussian()):0f;
        float YDeviate = (randomDeviate)?(float)(3d * Map.instance().random.nextGaussian()):0f;

		ft.worldX = HexUtils.getPixelX(q) + 18f + XDeviate;
		ft.worldY = HexUtils.getPixelY(q, r) + 30f + YDeviate;
		ft.color.set(color);
		
		e.add(depth);
		e.add(ft);
		this.addToEngine(e);; 
		return e;
	}

    public Entity initHighlighter(){
		Entity entity = this.engine.createEntity();
		Sprite s = this.engine.createComponent(Sprite.class);
		Position p = this.engine.createComponent(Position.class);
		p.q = 0;
		p.r = 0;
		p.layer = 99;
		s.revealed = true;
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

		mtp.points = new IntArray(false,12);

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
		Nutrients nutrients = this.engine.createComponent(Nutrients.class);
		NutrientCapacity maxNutrients = this.engine.createComponent(NutrientCapacity.class);
		Entity entity = this.engine.createEntity();

		entity.add(maxNutrients);
		entity.add(nutrients);
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

	public void addName(Entity e, String name){
		Name nameComp = this.engine.createComponent(Name.class);
		nameComp.name = name;

		e.add(nameComp);
	}

	public void addResourceType(Entity e, ResourceType rt){
		Resource r = this.engine.createComponent(Resource.class);
		r.resourceType = rt;

		e.add(r);
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
