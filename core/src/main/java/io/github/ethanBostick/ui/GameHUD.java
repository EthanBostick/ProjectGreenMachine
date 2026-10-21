package io.github.ethanBostick.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.core.OverlayMap;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.NutrientCapacity;
import io.github.ethanBostick.ecs.components.Nutrients;
import io.github.ethanBostick.ecs.components.Position;
import io.github.ethanBostick.ecs.components.Registry;
import io.github.ethanBostick.ecs.components.ToolType;
import io.github.ethanBostick.events.DepthChangeEvent;
import io.github.ethanBostick.events.Event;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventFactory;
import io.github.ethanBostick.events.EventType;
import io.github.ethanBostick.events.OverlayChangeEvent;
import io.github.ethanBostick.events.TileSelectEvent;
import io.github.ethanBostick.events.ToolChangeEvent;
import io.github.ethanBostick.map.Map;
import io.github.ethanBostick.map.TilePosition;

public class GameHUD implements Observer {
    public final Stage stage;
    private final Skin uiSkin;

    // Player / Network References
    private final Nutrients networkNutrients;
    private final NutrientCapacity networkCap;

    // Panels
    private Table networkPanel;
    private Table infoPanel;

    // Network Labels
    private Label networkCarbonLabel;
    private Label networkMineralLabel;

    // Selected Tile Labels
    private Position selectedTilePosition;
    private Label positionLabel;
    private Label myceliumCarbonLabel;
    private Label myceliumMineralLabel;
    private Label directionLabel;
    private Label nodeCarbonLabel;
    private Label nodeMineralLabel;

    public GameHUD() {
        EventBus.instance().subscribe(EventType.TILE_SELECTED, this);

        this.stage = new Stage(new ExtendViewport(1920, 1080));
        this.uiSkin = Registry.uiSkin;

        this.networkNutrients = Mappers.nutrientsCMap.get(Registry.player);
        this.networkCap = Mappers.nutrientCapacityCMap.get(Registry.player);

        buildUI();
    }

    // =========================================================================
    // UI Layout Assembly
    // =========================================================================

    private void buildUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        // rootTable.setDebug(true);

        Table overlayOptions = buildOverlayPanel();
        this.networkPanel = buildNetworkPanel();
        this.infoPanel = buildInfoPanel();
        Table buildOptions = buildToolBar();
        Table bottomButtons = buildBottomBar();

        // Group top-right telemetry panels into a vertical stack so they align cleanly
        Table rightSidebar = new Table();
        rightSidebar.top().right();
        rightSidebar.add(networkPanel).fillX().padBottom(10).row();
        rightSidebar.add(infoPanel).fillX();

        // Row 1: Top Left (Overlays) & Top Right (Telemetry Sidebar)
        rootTable.add(overlayOptions).top().left().pad(10);
        rootTable.add(rightSidebar).expandX().top().right().pad(10);
        rootTable.row();

        // Row 2: Bottom Left (Build Tools) & Bottom Right (Depth Control)
        rootTable.add(buildOptions).expandY().bottom().left().pad(10);
        rootTable.add(bottomButtons).bottom().right().pad(10);

        stage.addActor(rootTable);
    }

    private Table buildNetworkPanel() {
        Table panel = createStyledPanel();
        networkCarbonLabel = addLabelRow(panel, "Network Carbon: 0/0");
        networkMineralLabel = addLabelRow(panel, "Network Mineral: 0/0");
        return panel;
    }

    private Table buildInfoPanel() {
        Table panel = createStyledPanel();
        positionLabel = addLabelRow(panel, "q = 0, r = 0");

        addHeaderRow(panel, "-- Mycelium --");
        myceliumCarbonLabel = addLabelRow(panel, "Carbons: N/A");
        myceliumMineralLabel = addLabelRow(panel, "Minerals: N/A");
        directionLabel = addLabelRow(panel, "Direction: N/A");

        addHeaderRow(panel, "-- Resource Node --");
        nodeCarbonLabel = addLabelRow(panel, "Carbons: N/A");
        nodeMineralLabel = addLabelRow(panel, "Minerals: N/A");
        return panel;
    }

    private Table buildOverlayPanel() {
        Table table = new Table();
        ButtonGroup<CheckBox> radioGroup = new ButtonGroup<>();
        radioGroup.setMaxCheckCount(1);
        radioGroup.setMinCheckCount(0);
        radioGroup.setUncheckLast(true);

        // Easily add new overlays here with a single line
        addOverlayCheckBox(table, radioGroup, "region", OverlayMap.REGION);
        addOverlayCheckBox(table, radioGroup, "greenery", OverlayMap.GREEN);

        return table;
    }

    private Table buildToolBar() {
        Table table = new Table();
        table.defaults().padRight(5); // Automatic spacing between all buttons in this bar

        // Easily add new build or action tools here with a single line
        addToolButton(table, "Extractor", ToolType.BUILD, BuildType.EXTRACTOR);
        addToolButton(table, "Cleanser", ToolType.BUILD, BuildType.CLEANSER);
        addToolButton(table, "Cultivator", ToolType.BUILD, BuildType.CULTIVATOR);
        addToolButton(table, "Storage", ToolType.BUILD, BuildType.STORAGE);
        addToolButton(table, "Delete", ToolType.DELETE, null);

        return table;
    }

    private Table buildBottomBar() {
        Table table = new Table();
        table.defaults().padLeft(5);

        addActionButton(table, "Change Depth", () -> {
            DepthChangeEvent event = EventFactory.instance().depthChangeEventPool.obtain();
            event.newDepthDir = 1;
            EventBus.instance().publish(event);
        });

        return table;
    }

    // =========================================================================
    // Reusable UI Factory Helpers
    // =========================================================================

    /** Creates a standard background panel with default padding and left-aligned defaults. */
    private Table createStyledPanel() {
        Table panel = new Table(uiSkin);
        panel.setBackground("default-pane");
        panel.pad(10);
        panel.defaults().left();
        return panel;
    }

    /** Creates a dynamic Label, adds it as a row to the parent table, and returns it. */
    private Label addLabelRow(Table parent, String initialText) {
        Label label = new Label(initialText, uiSkin);
        parent.add(label).left().row();
        return label;
    }

    /** Adds a centered section divider header to a panel. */
    private void addHeaderRow(Table parent, String headerText) {
        parent.add(new Label(headerText, uiSkin)).center().padTop(5).row();
    }

    /** Creates a generic button wired to any Runnable callback. */
    private TextButton addActionButton(Table parent, String text, Runnable onClickAction) {
        TextButton button = new TextButton(text, uiSkin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClickAction.run();
            }
        });
        parent.add(button);
        return button;
    }

    /** Creates a tool selection button that publishes a ToolChangeEvent when clicked. */
    private TextButton addToolButton(Table parent, String label, ToolType tool, BuildType buildTarget) {
        return addActionButton(parent, label, () -> {
            ToolChangeEvent event = EventFactory.instance().toolChangeEventPool.obtain();
            event.tool = tool;
            event.buildTarget = buildTarget;
            EventBus.instance().publish(event);
        });
    }

    /** Creates a mutually-exclusive overlay CheckBox and registers its event listener. */
    private CheckBox addOverlayCheckBox(Table parent, ButtonGroup<CheckBox> group, String label, OverlayMap targetOverlay) {
        CheckBox checkBox = new CheckBox(label, uiSkin);
        checkBox.setChecked(false);
        group.add(checkBox);
        parent.add(checkBox).top().left().padRight(10);

        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Avoid firing duplicate events when ButtonGroup unchecks the previous box
                if (group.getChecked() != null && !checkBox.isChecked()) {
                    return;
                }
                OverlayChangeEvent overlayEvent = EventFactory.instance().overlayChangeEventPool.obtain();
                overlayEvent.selectedOverlay = checkBox.isChecked() ? targetOverlay : OverlayMap.NONE;
                EventBus.instance().publish(overlayEvent);
            }
        });
        return checkBox;
    }

    // =========================================================================
    // Frame Updates & Event Handling
    // =========================================================================

    public void update() {
        updateNetworkPanel();
        updateInfoPanel();
    }

    private void updateNetworkPanel() {
        if (networkNutrients == null) return;

        if (networkCap != null) {
            networkCarbonLabel.setText("Network Carbon: " + networkNutrients.carbons + "/" + networkCap.carbonCapacity);
            networkMineralLabel.setText("Network Mineral: " + networkNutrients.minerals + "/" + networkCap.mineralCapacity);
        } else {
            networkCarbonLabel.setText("Network Carbon: " + networkNutrients.carbons);
            networkMineralLabel.setText("Network Mineral: " + networkNutrients.minerals);
        }
    }

    private void updateInfoPanel() {
        if (selectedTilePosition == null || !infoPanel.isVisible()) return;

        int q = selectedTilePosition.q;
        int r = selectedTilePosition.r;
        positionLabel.setText("q = " + q + ", r = " + r);

        Entity selectedMycelium = Map.instance().getEntityAt(q, r, TilePosition.MYCELIUM);
        updateNutrientLabels(selectedMycelium, myceliumCarbonLabel, myceliumMineralLabel);

        if (selectedMycelium != null) {
            Direction growthDir = Mappers.directionCMap.get(selectedMycelium);
            int dirQ = (growthDir != null) ? growthDir.directionVector[0] : 0;
            int dirR = (growthDir != null) ? growthDir.directionVector[1] : 0;
            directionLabel.setText("Direction: " + dirQ + "," + dirR);
        } else {
            directionLabel.setText("Direction: N/A");
        }

        Entity selectedResourceNode = Map.instance().getEntityAt(q, r, TilePosition.RESOURCE_NODE);
        updateNutrientLabels(selectedResourceNode, nodeCarbonLabel, nodeMineralLabel);
    }

    /** Shared helper to populate Carbon/Mineral label pairs safely for any entity. */
    private void updateNutrientLabels(Entity entity, Label carbonTarget, Label mineralTarget) {
        Nutrients nutrients = (entity != null) ? Mappers.nutrientsCMap.get(entity) : null;
        if (nutrients != null) {
            carbonTarget.setText("Carbons: " + nutrients.carbons);
            mineralTarget.setText("Minerals: " + nutrients.minerals);
        } else {
            carbonTarget.setText("Carbons: N/A");
            mineralTarget.setText("Minerals: N/A");
        }
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case TILE_SELECTED:
                TileSelectEvent tse = (TileSelectEvent) event;
                this.selectedTilePosition = tse.position;
                break;
            default:
                break;
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        EventBus.instance().unsubscribe(EventType.TILE_SELECTED, this);
        stage.dispose();
        // Note: Do not dispose uiSkin here if it is owned globally by Registry.uiSkin
    }
}