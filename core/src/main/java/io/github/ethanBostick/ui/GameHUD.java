package io.github.ethanBostick.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.ashley.core.Entity;

import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;
import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.ToolType;
import io.github.ethanBostick.events.DepthChangeEvent;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventFactory;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.TileSelectEvent;
import io.github.ethanBostick.events.ToolChangeEvent;


public class GameHUD implements Observer {
    public Stage stage; 
    private Skin uiSkin;
    
    // 1. The tracked entity and the labels to update
    private Position selectedTilePosition;
    private Label positionLabel;
    private Label carbonLabel;
    private Label mineralLabel;
    private Label directionLabel;
    private Label carbonLabel2;
    private Label mineralLabel2;
    private Table infoPanel;

    public GameHUD() {
        EventBus.instance().subscribe(EventType.TILE_SELECTED, this);
        stage = new Stage(new ScreenViewport());
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

        // 2. Build the permanent Info Panel
        infoPanel = new Table(uiSkin);
        infoPanel.setBackground("default-pane"); // Assuming your skin has a background drawable
        infoPanel.pad(10);
        infoPanel.setVisible(true); // Hide until something is clicked

        // 3. Initialize the dynamic labels
        positionLabel = new Label("q = 0, r = 0", uiSkin);
        carbonLabel = new Label("Carbons: 0", uiSkin);
        mineralLabel = new Label("Minerals: 0", uiSkin);
        directionLabel = new Label("Direction: 0,0", uiSkin );
        carbonLabel2 = new Label("Carbons: 0", uiSkin);
        mineralLabel2 = new Label("Minerals: 0", uiSkin);
        
        infoPanel.add(positionLabel).left().row();
        infoPanel.add(new Label("-- Mycelium --", uiSkin)).row();
        infoPanel.add(carbonLabel).left().row();
        infoPanel.add(mineralLabel).left().row();
        infoPanel.add(directionLabel).left().row();
        infoPanel.add(new Label("-- Resource Node --", uiSkin)).row();
        infoPanel.add(carbonLabel2).left().row();
        infoPanel.add(mineralLabel2).left().row();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        //goes to the top right, expand claims all available vertical and horizontal space for this cell
        //rootTable.setDebug(true); //shows grid lines


        //group buttons in table
        Table buildOptions = new Table();

        TextButton buildExtractorButton = new TextButton("Extractor", uiSkin);
        buildExtractorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
				ToolChangeEvent toolChangeEvent = EventFactory.instance().toolChangeEventPool.obtain();
                toolChangeEvent.tool = ToolType.BUILD;
                toolChangeEvent.buildTarget = BuildType.EXTRACTOR;
				EventBus.instance().publish(toolChangeEvent);			
            }
        });

        TextButton buildCleanserButton = new TextButton("Cleanser", uiSkin);
        buildCleanserButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
				ToolChangeEvent toolChangeEvent = EventFactory.instance().toolChangeEventPool.obtain();
                toolChangeEvent.tool = ToolType.BUILD;
                toolChangeEvent.buildTarget = BuildType.CLEANSER;
				EventBus.instance().publish(toolChangeEvent);			
            }
        });

        TextButton buildStorageButton = new TextButton("Storage", uiSkin);
        buildCleanserButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
				ToolChangeEvent toolChangeEvent = EventFactory.instance().toolChangeEventPool.obtain();
                toolChangeEvent.tool = ToolType.BUILD;
                toolChangeEvent.buildTarget = BuildType.STORAGE;
				EventBus.instance().publish(toolChangeEvent);			
            }
        });

        TextButton buildDeleteButton = new TextButton("Delete", uiSkin);
        buildDeleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
				ToolChangeEvent toolChangeEvent = EventFactory.instance().toolChangeEventPool.obtain();
                toolChangeEvent.tool = ToolType.DELETE;
                toolChangeEvent.buildTarget = null;
				EventBus.instance().publish(toolChangeEvent);			
            }
        });

        // 6. Assemble the Hierarchy: Add Button -> Table -> Stage
        // The pad(10) adds 10 pixels of margin around the button
        buildOptions.add(buildExtractorButton); 
        buildOptions.add(buildCleanserButton); 
        buildOptions.add(buildStorageButton); 
        buildOptions.add(buildDeleteButton);
        //put back together

        Table bottomButtons = new Table();
        // 4. Create your Actors (Widgets)
        TextButton depthButton = new TextButton("Change Depth", uiSkin);
        // 5. Add Input Listeners to the Actors
        depthButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
				DepthChangeEvent depthChangeEvent = EventFactory.instance().depthChangeEventPool.obtain();
				depthChangeEvent.newDepthDir = 1;
				EventBus.instance().publish(depthChangeEvent);			
            }
        });


        bottomButtons.add(depthButton).padRight(5); 
        //put back together
        rootTable.add(buildOptions).expand().top().left().pad(10);
        rootTable.add(infoPanel).top().right().pad(10);
        rootTable.row();
        rootTable.add(bottomButtons).bottom().right().pad(10);

        //only add master root table
        stage.addActor(rootTable);
    }


    public void update() {

        if(selectedTilePosition == null){
            return;
        }
        positionLabel.setText("q = " + selectedTilePosition.q +", r = " + selectedTilePosition.r);

        Entity selectedMycelium = Map.instance().getEntityAt(selectedTilePosition.q, selectedTilePosition.r, TilePosition.MYCELIUM);
        Entity selectedResourceNode = Map.instance().getEntityAt(selectedTilePosition.q, selectedTilePosition.r, TilePosition.RESOURCE_NODE);
        if(selectedMycelium != null && infoPanel.isVisible()){
            Direction growthDirection = Mappers.directionCMap.get(selectedMycelium);
            Nutrients selectedNutrients = Mappers.nutrientsCMap.get(selectedMycelium);

            int dirQ = (growthDirection != null)? growthDirection.directionVector[0] : 0;
            int dirR = (growthDirection != null)? growthDirection.directionVector[1] : 1;

            carbonLabel.setText("Carbons: " + selectedNutrients.carbons);
            mineralLabel.setText("Minerals: " + selectedNutrients.minerals);
            directionLabel.setText("Direction: "+ dirQ +"," + dirR);

        }
        else{
            carbonLabel.setText("Carbons: N/A");
            mineralLabel.setText("Minerals: N/A");
            directionLabel.setText("Direction: N/A");
        }
        if(selectedResourceNode != null && infoPanel.isVisible()){
            Nutrients selectedNutrients = Mappers.nutrientsCMap.get(selectedResourceNode);

            carbonLabel2.setText("Carbons: " + selectedNutrients.carbons);
            mineralLabel2.setText("Minerals: " + selectedNutrients.minerals);

        }
        else{
            carbonLabel2.setText("Carbons: N/A");
            mineralLabel2.setText("Minerals: N/A");
        }
    }
        
	@Override
	public void onEvent(Event event){ //note w/poolable never store ref to event

		switch (event.getType()){
			case TILE_SELECTED:
				TileSelectEvent tse = (TileSelectEvent) event;
                this.selectedTilePosition = tse.position;
				break;
			default:
				System.out.println("unknown event");
		}
	}
    
    // Call this in your Main resize() method to keep UI crisp
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Call this to clean up memory when closing the game
    public void dispose() {
        stage.dispose();
        uiSkin.dispose();
    }
}
