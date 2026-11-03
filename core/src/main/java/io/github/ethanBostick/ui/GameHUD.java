package io.github.ethanBostick.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import io.github.ethanBostick.core.Adaptations;
import io.github.ethanBostick.core.Observer;
import io.github.ethanBostick.core.OverlayMap;
import io.github.ethanBostick.ecs.components.BuildType;
import io.github.ethanBostick.ecs.components.Direction;
import io.github.ethanBostick.ecs.components.Mappers;
import io.github.ethanBostick.ecs.components.MutagenMass;
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
import io.github.ethanBostick.utils.TextureUtils;

public class GameHUD implements Observer {
    public final Stage stage;
    private final Skin uiSkin;

    // Overlays & Skill Tree
    private Table adaptationTreeOverlay;
    private AdaptationTreeCanvas adaptationCanvas;

    // Radio Groups for Overlays and Tools
    // [PNG ART SWITCH]: Change ButtonGroup<TextButton> to ButtonGroup<Button> when using ImageButtons
    private final ButtonGroup<CheckBox> overlayRadioGroup = new ButtonGroup<>();
    private final ButtonGroup<TextButton> toolRadioGroup = new ButtonGroup<>();
    private TextButton priorityButton;
    private boolean suppressOverlayEvents = false;

    // Modular tracking for build/tool buttons gated behind Adaptations
    private static class UnlockableToolEntry {
        final Adaptations requiredAdaptation;
        final TextButton button;
        final Cell<TextButton> cell;
        boolean unlockedInUI = false;

        UnlockableToolEntry(Adaptations requiredAdaptation, TextButton button, Cell<TextButton> cell) {
            this.requiredAdaptation = requiredAdaptation;
            this.button = button;
            this.cell = cell;
        }
    }
    private final Array<UnlockableToolEntry> unlockableTools = new Array<>();
    private Table toolBarTable;

    // Player / Network References
    private final Nutrients networkNutrients;
    private final NutrientCapacity networkCap;
    private final MutagenMass networkMutagens;

    // Panels
    private Table networkPanel;
    private Table infoPanel;

    // Network Labels
    private Label networkMutagensLabel;
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
        this.networkMutagens = Mappers.mutagenMassCMap.get(Registry.player);

        // Configure shared Tool Radio Group (0 or 1 active tool at any time)
        this.toolRadioGroup.setMaxCheckCount(1);
        this.toolRadioGroup.setMinCheckCount(0);
        this.toolRadioGroup.setUncheckLast(true);

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
        this.toolBarTable = buildToolBar();
        Table bottomButtons = buildBottomBar();
        Table menuButtons = buildMenusBar();

        // Group top-right telemetry panels into a vertical stack so they align cleanly
        Table rightSidebar = new Table();
        rightSidebar.top().right();
        rightSidebar.add(networkPanel).fillX().padBottom(10).row();
        rightSidebar.add(infoPanel).fillX();

        // Row 1: Top Left (Overlays), Top Center/Right (Menus), & Top Right (Telemetry Sidebar)
        rootTable.add(overlayOptions).top().left().pad(10);
        rootTable.add(menuButtons).expandX().top().right().pad(10);
        rootTable.add(rightSidebar).top().right().pad(10);
        rootTable.row();

        // Row 2: Bottom Left (Build Tools) & Bottom Right (Depth & Priority Controls)
        rootTable.add(toolBarTable).expandY().bottom().left().pad(10);
        rootTable.add(bottomButtons). colspan(2).bottom().right().pad(10);

        stage.addActor(rootTable);

        // Modal Overlays
        this.adaptationTreeOverlay = buildAdaptationTreeModal();
        stage.addActor(adaptationTreeOverlay);
    }

    private Table buildNetworkPanel() {
        Table panel = createStyledPanel();
        networkCarbonLabel = addLabelRow(panel, "Network Carbon: 0/0");
        networkMineralLabel = addLabelRow(panel, "Network Mineral: 0/0");
        networkMutagensLabel = addLabelRow(panel, "Mutagen Mass: 0");
        return panel;
    }

    private Table buildMenusBar() {
        Table table = new Table();
        table.defaults().padRight(5);

        addActionButton(table, "Adaptations", () -> toggleSkillTree(true));

        return table;
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
        overlayRadioGroup.setMaxCheckCount(1);
        overlayRadioGroup.setMinCheckCount(0);
        overlayRadioGroup.setUncheckLast(true);

        // Easily add new overlays here with a single line
        addOverlayCheckBox(table, overlayRadioGroup, "region", OverlayMap.REGION);
        addOverlayCheckBox(table, overlayRadioGroup, "greenery", OverlayMap.GREEN);

        return table;
    }

    private Table buildToolBar() {
        Table table = new Table();

        // Always-unlocked tools:
        addToolToggleButton(table, "Extractor", ToolType.BUILD, BuildType.EXTRACTOR);

        // Modular unlockable tools (hidden until the corresponding Adaptation is unlocked):
        addUnlockableToolButton(table, "Transport", ToolType.BUILD, BuildType.CONDUIT, Adaptations.TRANSPORT_HYPHAE);

        // Always-unlocked tools:
        addToolToggleButton(table, "Carbon Synthesizer", ToolType.BUILD, BuildType.CARBON_SYPHON);
        addToolToggleButton(table, "Cleanser", ToolType.BUILD, BuildType.CLEANSER);
        addToolToggleButton(table, "Cultivator", ToolType.BUILD, BuildType.CULTIVATOR);
        addToolToggleButton(table, "Storage", ToolType.BUILD, BuildType.STORAGE);
        addToolToggleButton(table, "Delete", ToolType.DELETE, null);

        return table;
    }

    private Table buildBottomBar() {
        Table table = new Table();
        table.defaults().padLeft(5);

        // Depth is a stateless action button, NOT part of the tool radio group
        addActionButton(table, "Depth", () -> {
            DepthChangeEvent event = EventFactory.instance().depthChangeEventPool.obtain();
            event.newDepthDir = 1;
            EventBus.instance().publish(event);
        });

        // Priority is part of the shared toolRadioGroup and manages the Priority Overlay
        this.priorityButton = addPriorityToggleButton(table, "Priority");

        return table;
    }

    private Table buildAdaptationTreeModal() {
        Table modalOverlay = new Table(uiSkin);
        modalOverlay.setFillParent(true);
        modalOverlay.setBackground("default-pane");
        modalOverlay.setVisible(false);

        // Swallow all clicks, scrolls, & keys on the backdrop so MapController never sees them
        modalOverlay.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
        modalOverlay.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                return true;
            }
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    toggleSkillTree(false);
                }
                return true;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return true;
            }
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                return true;
            }
        });

        Texture lineTex = TextureUtils.pathToTexture("dragArrow.png");
        this.adaptationCanvas = new AdaptationTreeCanvas(uiSkin, lineTex);
        ScrollPane scrollPane = new ScrollPane(adaptationCanvas);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setOverscroll(false, false);

        Table header = new Table();
        header.add(new Label("Mycelium Adaptations", uiSkin)).expandX().left();
        addActionButton(header, "Close [X]", () -> toggleSkillTree(false));

        modalOverlay.add(header).fillX().pad(20).row();
        modalOverlay.add(scrollPane).expand().fill().pad(20);

        scrollPane.layout();
        scrollPane.setScrollPercentX(0.5f);
        scrollPane.setScrollPercentY(0.5f);

        return modalOverlay;
    }

    // =========================================================================
    // Reusable UI Factory Helpers
    // =========================================================================

    private Table createStyledPanel() {
        Table panel = new Table(uiSkin);
        panel.setBackground("default-pane");
        panel.pad(10);
        panel.defaults().left();
        return panel;
    }

    private Label addLabelRow(Table parent, String initialText) {
        Label label = new Label(initialText, uiSkin);
        parent.add(label).left().row();
        return label;
    }

    private void addHeaderRow(Table parent, String headerText) {
        parent.add(new Label(headerText, uiSkin)).center().padTop(5).row();
    }

    /** Creates a generic stateless click button wired to any Runnable callback. */
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

    /**
     * Creates a radio-toggle tool button (without a checkbox graphic) registered to toolRadioGroup.
     * Clicking an unchecked button activates the tool; clicking it again untoggles it to ToolType.DEFAULT.
     */
    private Cell<TextButton> addToolToggleButton(Table parent, String label, ToolType tool, BuildType buildTarget) {
        /*
         * [PNG ART SWITCH - BUILD & TOOL BUTTONS]:
         * When you have custom PNGs for tool buttons, replace TextButton with ImageButton:
         * 
         * ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
         * style.up = new TextureRegionDrawable(toolNormalTex);
         * style.over = new TextureRegionDrawable(toolHoverTex);
         * style.checked = new TextureRegionDrawable(toolActiveTex); // Conveys active toggle state!
         * ImageButton button = new ImageButton(style);
         */
        TextButton button = new TextButton(label, uiSkin);
        button.setChecked(false);
        toolRadioGroup.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Update visual tint to clearly convey active vs inactive state
                // (Can be removed once ImageButtonStyle.checked PNGs are used)
                button.setColor(button.isChecked() ? Color.CYAN : Color.WHITE);

                // If another tool button in the group was just checked, it will publish its own ToolChangeEvent;
                // only publish ToolType.DEFAULT when no tool in the group is checked anymore.
                if (!button.isChecked()) {
                    if (toolRadioGroup.getChecked() == null) {
                        publishToolChange(ToolType.DEFAULT, null);
                    }
                    return;
                }

                publishToolChange(tool, buildTarget);
            }
        });

        return parent.add(button).padRight(5);
    }

    /**
     * Modular helper to register a tool button that stays completely hidden (and takes 0 layout width)
     * until its required Adaptation is unlocked.
     */
    private void addUnlockableToolButton(Table parent, String label, ToolType tool, BuildType buildTarget, Adaptations requiredAdaptation) {
        Cell<TextButton> cell = addToolToggleButton(parent, label, tool, buildTarget);
        TextButton button = cell.getActor();

        if (!requiredAdaptation.unlocked) {
            button.setVisible(false);
            cell.width(0).height(0).padRight(0); // Collapse cell so hidden button leaves no gap
        }

        unlockableTools.add(new UnlockableToolEntry(requiredAdaptation, button, cell));
    }

    /**
     * Creates the Priority radio toggle button.
     * - When toggled ON: unchecks top-left overlays, activates ToolType.PRIORITY and OverlayMap.PRIORITY.
     * - When toggled OFF: activates ToolType.DEFAULT (if no other tool was clicked) and OverlayMap.NONE.
     */
    private TextButton addPriorityToggleButton(Table parent, String label) {
        // [PNG ART SWITCH - PRIORITY TOOL]: Swap TextButton for ImageButton with up/over/checked drawables
        TextButton button = new TextButton(label, uiSkin);
        button.setChecked(false);
        toolRadioGroup.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                button.setColor(button.isChecked() ? Color.CYAN : Color.WHITE);

                if (button.isChecked()) {
                    // 1. Untoggle any active top-left overlays without firing a redundant OverlayMap.NONE event
                    suppressOverlayEvents = true;
                    overlayRadioGroup.uncheckAll();
                    suppressOverlayEvents = false;

                    // 2. Publish Priority Tool and Priority Overlay
                    publishToolChange(ToolType.PRIORITY, null);
                    publishOverlayChange(OverlayMap.PRIORITY);
                } else {
                    // Priority was untoggled (either clicked again or replaced by another tool)
                    publishOverlayChange(OverlayMap.NONE);

                    if (toolRadioGroup.getChecked() == null) {
                        publishToolChange(ToolType.DEFAULT, null);
                    }
                }
            }
        });

        parent.add(button);
        return button;
    }

    /** Creates a mutually-exclusive overlay CheckBox that is disallowed while the Priority tool is active. */
    private CheckBox addOverlayCheckBox(Table parent, ButtonGroup<CheckBox> group, String label, OverlayMap targetOverlay) {
        CheckBox checkBox = new CheckBox(label, uiSkin);
        checkBox.setChecked(false);
        group.add(checkBox);
        parent.add(checkBox).top().left().padRight(10);

        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (suppressOverlayEvents) {
                    return;
                }

                // Disallow toggling top-left overlays if the player is actively using the Priority tool
                if (priorityButton != null && priorityButton.isChecked()) {
                    if (checkBox.isChecked()) {
                        suppressOverlayEvents = true;
                        checkBox.setChecked(false);
                        suppressOverlayEvents = false;
                    }
                    return;
                }

                // Avoid firing duplicate events when ButtonGroup unchecks the previous box
                if (group.getChecked() != null && !checkBox.isChecked()) {
                    return;
                }

                publishOverlayChange(checkBox.isChecked() ? targetOverlay : OverlayMap.NONE);
            }
        });
        return checkBox;
    }

    private void publishToolChange(ToolType tool, BuildType buildTarget) {
        ToolChangeEvent event = EventFactory.instance().toolChangeEventPool.obtain();
        event.tool = tool;
        event.buildTarget = buildTarget;
        EventBus.instance().publish(event);
    }

    private void publishOverlayChange(OverlayMap overlay) {
        OverlayChangeEvent overlayEvent = EventFactory.instance().overlayChangeEventPool.obtain();
        overlayEvent.selectedOverlay = overlay;
        EventBus.instance().publish(overlayEvent);
    }

    // =========================================================================
    // Frame Updates & Event Handling
    // =========================================================================

    public void update() {
        updateNetworkPanel();
        updateInfoPanel();
        updateUnlockableTools();

        // Update the Adaptation Tree nodes whenever the modal is open
        if (adaptationTreeOverlay.isVisible() && networkMutagens != null) {
            adaptationCanvas.update(networkMutagens.amount);
        }
    }

    /** Checks if any locked build buttons have had their Adaptation unlocked and reveals them. */
    private void updateUnlockableTools() {
        boolean layoutChanged = false;
        for (int i = 0; i < unlockableTools.size; i++) {
            UnlockableToolEntry entry = unlockableTools.get(i);
            if (!entry.unlockedInUI && entry.requiredAdaptation.unlocked) {
                entry.unlockedInUI = true;
                entry.button.setVisible(true);
                // Restore the button's natural preferred size and spacing in the toolbar
                entry.cell.width(entry.button.getPrefWidth())
                          .height(entry.button.getPrefHeight())
                          .padRight(5);
                layoutChanged = true;
            }
        }
        if (layoutChanged && toolBarTable != null) {
            toolBarTable.invalidateHierarchy();
        }
    }

    private void updateNetworkPanel() {
        if (networkMutagensLabel != null && networkMutagens != null) {
            networkMutagensLabel.setText("Mutagen Mass: " + networkMutagens.amount);
        }

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

    private void toggleSkillTree(boolean open) {
        adaptationTreeOverlay.setVisible(open);
        if (open) {
            if (networkMutagens != null) {
                adaptationCanvas.update(networkMutagens.amount);
            }
            stage.setKeyboardFocus(adaptationTreeOverlay);
            stage.setScrollFocus(adaptationTreeOverlay);
        } else {
            stage.setKeyboardFocus(null);
            stage.setScrollFocus(null);
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
        EventBus.instance().unsubscribeAll(this);
        stage.dispose();
    }
}