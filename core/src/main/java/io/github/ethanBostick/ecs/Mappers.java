package io.github.ethanBostick.ecs;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {
    public static final ComponentMapper<Position> positionCMap = ComponentMapper.getFor(Position.class);
    public static final ComponentMapper<Sprite> spriteCMap = ComponentMapper.getFor(Sprite.class);
    public static final ComponentMapper<MouseState> mouseStateCMap = ComponentMapper.getFor(MouseState.class);
    public static final ComponentMapper<Biome> biomeCMap = ComponentMapper.getFor(Biome.class);

}
