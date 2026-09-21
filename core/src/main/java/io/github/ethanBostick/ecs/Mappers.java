package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {
    public static final ComponentMapper<Position> positionCMap = ComponentMapper.getFor(Position.class);
    public static final ComponentMapper<Sprite> spriteCMap = ComponentMapper.getFor(Sprite.class);
    public static final ComponentMapper<MouseState> mouseStateCMap = ComponentMapper.getFor(MouseState.class);
    public static final ComponentMapper<Biome> biomeCMap = ComponentMapper.getFor(Biome.class);
    public static final ComponentMapper<Depth> depthCMap = ComponentMapper.getFor(Depth.class);
    public static final ComponentMapper<MultiTileSprite> multiTileSpriteCMap = ComponentMapper.getFor(MultiTileSprite.class);
    public static final ComponentMapper<MultiTilePosition> multiTilePositionCMap = ComponentMapper.getFor(MultiTilePosition.class);
    public static final ComponentMapper<Density> densityCMap = ComponentMapper.getFor(Density.class);
    public static final ComponentMapper<Nutrients> nutrientsCMap = ComponentMapper.getFor(Nutrients.class);
    public static final ComponentMapper<NutrientCapacity> nutrientCapacityCMap = ComponentMapper.getFor(NutrientCapacity.class);
    public static final ComponentMapper<NutrientDraw> nutrientDrawCMap = ComponentMapper.getFor(NutrientDraw.class);
    public static final ComponentMapper<Direction> directionCMap = ComponentMapper.getFor(Direction.class);
}
