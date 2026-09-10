package io.github.ethanBostick.ecs;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class SelectionSystem extends IteratingSystem{
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
        Position position = Mappers.positionCMap.get(entity);
        MouseState state = Mappers.mouseStateCMap.get(entity);

        if (state.pressedDown && !Map.instance().outOfMapBounds(position.q, position.r)){
            selectedPosition.q = position.q;
            selectedPosition.r = position.r;
            selected.texture = TextureUtils.pathToTexture("selected.png");

            Entity selectedTile = Map.instance().getEntityAt(selectedPosition.q, selectedPosition.r, TilePosition.TILE);
            BiomeType biomeType = Mappers.biomeCMap.get(selectedTile).biomeType;
            int biomeTemp = Mappers.biomeCMap.get(selectedTile).temp;

            System.out.println("--- TILE INFO ---");
            System.out.println("Biome type: "+ biomeType);
            System.out.println("temp: "+ biomeTemp);
            System.out.println("--- --------- ---");
        }
        else if (Map.instance().outOfMapBounds(position.q, position.r)){
            selected.texture = null;
        }
    }
}


