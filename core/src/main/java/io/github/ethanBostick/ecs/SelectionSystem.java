package io.github.ethanBostick.ecs;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class SelectionSystem extends IteratingSystem{
    private Entity selectTool = null;
    private Entity dragTool = null;

    public SelectionSystem() {
        super(Family.all(MouseState.class, Position.class).get());
        this.selectTool = Registry.selectTool;
        this.dragTool = Registry.dragTool;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position mousePosition = Mappers.positionCMap.get(entity);
        MouseState mouseState = Mappers.mouseStateCMap.get(entity);
        MultiTilePosition mouseDragPosition = Mappers.multiTilePositionCMap.get(entity);

        Position selectedPosition = Mappers.positionCMap.get(this.selectTool);
        Sprite selectedSprite = Mappers.spriteCMap.get(this.selectTool);

        MultiTileSprite dragSprites = Mappers.multiTileSpriteCMap.get(this.dragTool);
        MultiTilePosition dragPosition = Mappers.multiTilePositionCMap.get(this.dragTool);

        // esc key clears selection 
        if(mouseState.clearSelect){
            mouseState.clearSelect = false;
            selectedSprite.texture = null;
            return;
        }

        // regular select logic
        if (mouseState.pressedDown && !Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r)){
            //pressed the same hex again
            if(mousePosition.q == selectedPosition.q && mousePosition.r == selectedPosition.r && selectedSprite.texture != null){
                //increment the selection
                selectedPosition.tilePosition = TilePosition.values()[(selectedPosition.tilePosition.value()+1) % TilePosition.MAX_POSITIONS.value()];
            }
            selectedPosition.q = mousePosition.q;
            selectedPosition.r = mousePosition.r;
            selectedSprite.texture = TextureUtils.pathToTexture("selected.png");

            Entity selectedTile = Map.instance().getEntityAt(selectedPosition.q, selectedPosition.r, selectedPosition.tilePosition);
            if(selectedTile != null){
                Nutrients selectedNutrients = Mappers.nutrientsCMap.get(selectedTile);
                int carbonAmount = (selectedNutrients != null)? selectedNutrients.carbons : 0;
                int mineralAmount = (selectedNutrients != null)? selectedNutrients.minerals : 0;
                System.out.println("--- info "+ selectedPosition.tilePosition +  " ---");
                System.out.println("carbons: "+ carbonAmount);
                System.out.println("minerals: "+ mineralAmount);
                System.out.println("--- END INFO ---");
            }

            mouseState.pressedDown = false;
        }
        else if (Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r)){
            selectedSprite.texture = null;
            mouseState.pressedDown = false;
        }

        // drag tool logic
        if(mouseState.pressedUp){
            dragPosition.points.clear();
            dragSprites.texture.clear();
            mouseDragPosition.points.clear();
            mouseState.pressedUp = false;
        }
        else if (mouseState.heldDown && !Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r)){
            if (mouseDragPosition.points.size > 0){
                int prevQ = mouseDragPosition.points.items[mouseDragPosition.points.size - 2];
                int prevR = mouseDragPosition.points.items[mouseDragPosition.points.size -1];
                if(mousePosition.q != prevQ || mousePosition.r != prevR){
                    int[] dirVec = {mousePosition.q - prevQ, mousePosition.r - prevR};
                    dragPosition.points.add(prevQ);
                    dragPosition.points.add(prevR);
                    dragSprites.texture.add(TextureUtils.dirToTexture(0, dirVec));
                    dragSprites.textureHead = TextureUtils.dirToTexture(0, dirVec);

                    mouseDragPosition.points.add(mousePosition.q);
                    mouseDragPosition.points.add(mousePosition.r);
                }
            }
            else{
                mouseDragPosition.points.add(mousePosition.q);
                mouseDragPosition.points.add(mousePosition.r);
            }
        }
    }
}


