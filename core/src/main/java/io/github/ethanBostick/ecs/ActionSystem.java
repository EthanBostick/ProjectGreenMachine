package io.github.ethanBostick.ecs;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.TextureUtils;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class ActionSystem extends IteratingSystem{

    private final Entity highlighter;

    public ActionSystem() {
        super(Family.all(MouseState.class, ActiveTool.class).get(),1);
        this.highlighter = Registry.highlighter;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    private void defaultAction(Entity mouse){
        Position mousePosition = Mappers.positionCMap.get(mouse);
        MouseState mouseState = Mappers.mouseStateCMap.get(mouse);
        MultiTilePosition mouseDragPosition = Mappers.multiTilePositionCMap.get(mouse);

        Position selectedPosition = Mappers.positionCMap.get(this.highlighter);
        Sprite selectedSprite = Mappers.spriteCMap.get(this.highlighter);

        // regular select logic
        if (mouseState.pressedDown && !Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r) && !mouseState.heldDown){
            //pressed the same hex again
            if(mousePosition.q == selectedPosition.q && mousePosition.r == selectedPosition.r && selectedSprite.texture != null){
                //increment the selection
                //selectedPosition.tilePosition = TilePosition.values()[(selectedPosition.tilePosition.value()+1) % TilePosition.MAX_POSITIONS.value()];
            }
            selectedPosition.q = mousePosition.q;
            selectedPosition.r = mousePosition.r;
            selectedSprite.texture = TextureUtils.pathToTexture("selected.png");

            Entity selectedTile = Map.instance().getEntityAt(selectedPosition.q, selectedPosition.r, TilePosition.MYCELIUM);
            if(selectedTile != null){
                Direction growthDirection = Mappers.directionCMap.get(selectedTile);
                Nutrients selectedNutrients = Mappers.nutrientsCMap.get(selectedTile);

                int dirQ = (growthDirection != null)? growthDirection.directionVector[0] : 0;
                int dirR = (growthDirection != null)? growthDirection.directionVector[1] : 1;
                int carbonAmount = (selectedNutrients != null)? selectedNutrients.carbons : 0;
                int mineralAmount = (selectedNutrients != null)? selectedNutrients.minerals : 0;
                System.out.println("--- info "+ selectedPosition.tilePosition +  " ---");
                System.out.println("carbons: "+ carbonAmount);
                System.out.println("minerals: "+ mineralAmount);
                System.out.println("direction: "+ dirQ +"," + dirR);
                System.out.println("--- END INFO ---");
            }
        }
        else if (Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r)){
            selectedSprite.texture = null;
        }

        // deploy Runners
        if (mouseDragPosition.points.size < 4) return;
        int startQ = mouseDragPosition.points.get(0);
        int startR = mouseDragPosition.points.get(1);
        int endQ = mouseDragPosition.points.get(2);
        int endR = mouseDragPosition.points.get(3);

        if (Map.instance().outOfMapBounds(startQ, startR)) return;
        if (Map.instance().outOfMapBounds(endQ, endR)) return;
        if(startQ == endQ && startR == endR) return;

        Entity startPosition = Map.instance().getEntityAt(startQ, startR, TilePosition.MYCELIUM);
        if(startPosition == null) return;

        if (mouseState.pressedUp) {
            Entity runner = EntityBuilder.instance().createMyceliumRunner(startQ,startR,endQ,endR);
            EntityBuilder.instance().addToEngine(runner);
        }

        //end runners

    }

    @Override
    protected void processEntity(Entity mouse, float deltaTime) {
        ToolType activeTool = Mappers.activeToolCMap.get(mouse).tool;
        MouseState mouseState = Mappers.mouseStateCMap.get(mouse);

        // esc key clears selection 
        if(mouseState.clearSelect){
            mouseState.clearSelect = false;
            Mappers.spriteCMap.get(this.highlighter).texture = null;
            return;
        }

        switch (activeTool){
            case DEFAULT:
                this.defaultAction(mouse);
                break;
        }

        //update pressed up once its processed
        if (mouseState.pressedUp){
            mouseState.pressedUp = false;
            mouseState.heldDown = false;
            mouseState.pressedDown = false;

            //clear drag tool:
            Mappers.VectorArrowCMap.get(mouse).startX = 0;
            Mappers.VectorArrowCMap.get(mouse).startY = 0;
            Mappers.VectorArrowCMap.get(mouse).endX = 0;
            Mappers.VectorArrowCMap.get(mouse).endY = 0;

            //clear mouse touch points
            Mappers.multiTilePositionCMap.get(mouse).points.clear();
        }

    }
}

