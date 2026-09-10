package io.github.ethanBostick.ecs;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.utils.TextureUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.ComponentMapper;

public class SelectionSystem extends IteratingSystem{
    private static ComponentMapper<MouseState> mMap = ComponentMapper.getFor(MouseState.class);
    private static ComponentMapper<Position> pMap = ComponentMapper.getFor(Position.class);
    private Sprite selected = null;
    private Position selectedPosition = null;

    public SelectionSystem(Sprite selected, Position selectedPosition) {
        super(Family.all(MouseState.class, Position.class).get());
        this.selected = selected;
        this.selectedPosition = selectedPosition;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = pMap.get(entity);
        MouseState state = mMap.get(entity);
        selectedPosition.q = position.q;
        selectedPosition.r = position.r;

        if (state.pressedDown && !Map.instance().outOfMapBounds(position.q, position.r)){
            selected.texture = TextureUtils.pathToTexture("selected.png");
        }
    }
}


