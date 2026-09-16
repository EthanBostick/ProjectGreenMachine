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
}
