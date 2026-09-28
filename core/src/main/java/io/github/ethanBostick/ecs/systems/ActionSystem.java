package io.github.ethanBostick.ecs.systems;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.utils.HexUtils;
import io.github.ethanBostick.utils.TextureUtils;
import io.github.ethanBostick.core.EntityBuilder;
import io.github.ethanBostick.ecs.components.ActiveTool;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MouseState;
import io.github.ethanBostick.ecs.components.MultiTilePosition;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.ecs.components.Sprite;
import io.github.ethanBostick.ecs.components.ToolType;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.TileSelectEvent;
import io.github.ethanBostick.events.EventFactory;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

public class ActionSystem extends IteratingSystem{

    private final Entity highlighter;
    private final int[][] hexDirections;

    public ActionSystem(int priority) {
        super(Family.all(MouseState.class).get(),priority);
        this.highlighter = Registry.highlighter;
        this.hexDirections = HexUtils.hexDirections;
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
        if (mouseState.pressedDown && mouseState.pressedUp && !Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r)){
            //pressed the same hex again
            if(mousePosition.q == selectedPosition.q && mousePosition.r == selectedPosition.r && selectedSprite.texture != null){
                //increment the selection
                //selectedPosition.tilePosition = TilePosition.values()[(selectedPosition.tilePosition.value()+1) % TilePosition.MAX_POSITIONS.value()];
            }
            selectedPosition.q = mousePosition.q;
            selectedPosition.r = mousePosition.r;
            selectedSprite.texture = TextureUtils.pathToTexture("selected.png");

            TileSelectEvent selectEvent = EventFactory.instance().tileSelectEventPool.obtain();
            selectEvent.position = selectedPosition;
            EventBus.instance().publish(selectEvent);			
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

    private boolean neighboringMycelium(Position p){
        boolean friendNear = false;
        for (int i = 0; i < 6; i++){
            Entity neighbor = Map.instance().getEntityAt(p.q + this.hexDirections[i][0], p.r + this.hexDirections[i][1], TilePosition.MYCELIUM);
            friendNear = neighbor != null && Mappers.buildCMap.get(neighbor) == null && Mappers.deadCMap.get(neighbor) == null;
            if (friendNear) break;
        }

        return friendNear;
    }

    private void buildAction(Entity mouse, ActiveTool activeTool){
        Position mousePosition = Mappers.positionCMap.get(mouse);
        MouseState mouseState = Mappers.mouseStateCMap.get(mouse);
        MultiTilePosition mouseDragPosition = Mappers.multiTilePositionCMap.get(mouse);
        BuildType targetBuild = activeTool.buildTarget;

        if (targetBuild == null) return;

        int carbonCost = targetBuild.carbonCost();
        int mineralCost = targetBuild.mineralCost();

        if (mouseState.pressedDown && !Map.instance().outOfMapBounds(mousePosition.q, mousePosition.r) && !mouseState.heldDown){

            Entity selectedTile = Map.instance().getEntityAt(mousePosition.q, mousePosition.r, TilePosition.MYCELIUM);

            if(selectedTile != null && neighboringMycelium(mousePosition)){
                Entity newBuild = EntityBuilder.instance().createBuild(targetBuild,mousePosition.q,mousePosition.r,carbonCost,mineralCost);
                EntityBuilder.instance().addToEngine(newBuild);
                Map.instance().setEntityAt(mousePosition.q, mousePosition.r, newBuild, TilePosition.MYCELIUM);       

                //clear old mycelium
                EntityBuilder.instance().addDead(selectedTile);
            }
            //check for atleast one mycelium neighbor
            else if (neighboringMycelium(mousePosition)){
                Entity newBuild = EntityBuilder.instance().createBuild(targetBuild,mousePosition.q,mousePosition.r,carbonCost,mineralCost);
                EntityBuilder.instance().addToEngine(newBuild);
                //add to map so none may build there
                Map.instance().setEntityAt(mousePosition.q, mousePosition.r, newBuild, TilePosition.MYCELIUM);       
            }
        }
    }

    @Override
    protected void processEntity(Entity mouse, float deltaTime) {
        ActiveTool activeTool = Mappers.activeToolCMap.get(Registry.player);
        MouseState mouseState = Mappers.mouseStateCMap.get(mouse);

        // esc key clears selection 
        if(mouseState.clearSelect){
            activeTool.tool = ToolType.DEFAULT;    
            activeTool.buildTarget = null;    
            mouseState.clearSelect = false;
            Mappers.spriteCMap.get(this.highlighter).texture = null;
            return;
        }

        switch (activeTool.tool){
            case DEFAULT:
                //display the drag line
                Mappers.VectorArrowCMap.get(mouse).arrowTexture = TextureUtils.pathToTexture("dragArrow.png");
                this.defaultAction(mouse);
                break;
            case BUILD:
                //hide drag line if its not being used by the active tool
                Mappers.VectorArrowCMap.get(mouse).arrowTexture = null;
                this.buildAction(mouse, activeTool);
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

