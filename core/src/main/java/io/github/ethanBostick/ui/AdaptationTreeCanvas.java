package io.github.ethanBostick.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import io.github.ethanBostick.core.Adaptations;
import io.github.ethanBostick.events.EventBus;
import io.github.ethanBostick.events.EventFactory;
import io.github.ethanBostick.events.UnlockEvent;

public class AdaptationTreeCanvas extends WidgetGroup {
    private final Skin skin;
    private final TextureRegion lineRegion;
    
    // [PNG ART SWITCH - BACKGROUND]: 
    // Add a TextureRegion or Texture field here for your adaptation tree background art:
    // private TextureRegion backgroundRegion;

    private final Array<AdaptationNode> nodes = new Array<>();
    private final Array<SkillConnection> connections = new Array<>();

    /** Tracks a single skill node, its enum data, its UI button, and its parent prerequisites. */
    private static class AdaptationNode {
        final Adaptations adaptation;
        // [PNG ART SWITCH - NODE BUTTON]: Change 'TextButton' to 'Button' or 'ImageButton' when switching to PNGs
        final TextButton button;
        final Array<AdaptationNode> parents = new Array<>();

        AdaptationNode(Adaptations adaptation, TextButton button) {
            this.adaptation = adaptation;
            this.button = button;
        }

        /** Returns true if this is a root node (no parents) or all parent nodes are unlocked. */
        boolean areParentsUnlocked() {
            for (int i = 0; i < parents.size; i++) {
                if (!parents.get(i).adaptation.unlocked) {
                    return false;
                }
            }
            return true;
        }
    }

    /** Simple internal data struct for drawing lines between two nodes. */
    private static class SkillConnection {
        final AdaptationNode parent;
        final AdaptationNode child;

        SkillConnection(AdaptationNode parent, AdaptationNode child) {
            this.parent = parent;
            this.child = child;
        }
    }

    public AdaptationTreeCanvas(Skin skin, Texture lineTexture) {
        this.skin = skin;
        this.lineRegion = new TextureRegion(lineTexture);
        
        // [PNG ART SWITCH - BACKGROUND]:
        // Load or pass in your background texture here:
        // this.backgroundRegion = new TextureRegion(TextureUtils.pathToTexture("adaptationTreeBg.png"));

        // Define the virtual size of the pannable canvas
        setSize(1920, 1080);
        setTouchable(Touchable.enabled); // Allows dragging the canvas by clicking empty space

        buildTree();
    }

    private void buildTree() {
        AdaptationNode rootNode = addSkillNode(
            "Extractor Area +1 [" + Adaptations.EXTRACTOR_RANGE.mutagenCost + "]",
            Adaptations.EXTRACTOR_RANGE,
            960, 100
        );

        AdaptationNode constructs = addSkillNode(
            "Surface Constructs [" + Adaptations.SURFACE_CONSTRUCTS.mutagenCost + "]",
            Adaptations.SURFACE_CONSTRUCTS,
            960 - 200, 200
        );

        AdaptationNode transportNodes = addSkillNode(
            "Transport Hyphae [" + Adaptations.TRANSPORT_HYPHAE.mutagenCost + "]",
            Adaptations.TRANSPORT_HYPHAE,
            960 + 200, 200
        );

        AdaptationNode extractRocks = addSkillNode(
            "Harsh Chemicals [" + Adaptations.HARSH_CHEMICALS.mutagenCost + "]",
            Adaptations.HARSH_CHEMICALS,
            960, 200
        );

        // Link them together (establishes both visual lines and prerequisite parent-child rules)
        connect(rootNode, transportNodes);
        connect(rootNode, constructs);
        connect(rootNode, extractRocks);
    }

    /** Factory helper to create, position, and register a clickable skill node. */
    private AdaptationNode addSkillNode(String labelText, Adaptations adaptation, float centerX, float centerY) {
        /* 
         * [PNG ART SWITCH - NODE CREATION]:
         * When you have custom PNGs for each node state, replace TextButton with ImageButton:
         * 
         * ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
         * style.up = new TextureRegionDrawable(purchasableTex);       // Normal purchasable state
         * style.over = new TextureRegionDrawable(hoverTex);           // Mouse hover state
         * style.disabled = new TextureRegionDrawable(lockedTex);      // Grayed-out unpurchasable state
         * style.checked = new TextureRegionDrawable(unlockedTex);     // Unlocked state
         * ImageButton nodeBtn = new ImageButton(style);
         */
        TextButton nodeBtn = new TextButton(labelText, skin);
        nodeBtn.setSize(160, 60);
        nodeBtn.setPosition(centerX, centerY, Align.center);

        AdaptationNode node = new AdaptationNode(adaptation, nodeBtn);

        nodeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Ignore clicks if the button is disabled (already unlocked, parent locked, or unaffordable)
                if (nodeBtn.isDisabled()) {
                    return;
                }
                UnlockEvent unlockEvent = EventFactory.instance().unlockEventPool.obtain();
                unlockEvent.unlockTarget = adaptation;
                EventBus.instance().publish(unlockEvent);
            }
        });

        this.addActor(nodeBtn);
        nodes.add(node);
        return node;
    }

    /** Links two nodes visually and registers 'parent' as a prerequisite for 'child'. */
    private void connect(AdaptationNode parent, AdaptationNode child) {
        child.parents.add(parent);
        connections.add(new SkillConnection(parent, child));
    }

    /**
     * Updates the visual and interactive states of all adaptation nodes based on
     * prerequisite unlocks and the player's current mutagen mass.
     */
    public void update(int currentMutagenMass) {
        for (int i = 0; i < nodes.size; i++) {
            AdaptationNode node = nodes.get(i);
            TextButton btn = node.button;

            if (node.adaptation.unlocked) {
                // 1. UNLOCKED STATE: Convey unlocked status and prevent re-purchasing
                btn.setDisabled(true);
                btn.setTouchable(Touchable.disabled);
                btn.setColor(Color.CYAN);
                // [PNG ART SWITCH]: If using ImageButton with style.checked = unlockedTex:
                // btn.setChecked(true);
            } else if (!node.areParentsUnlocked()) {
                // 2. PREREQUISITE LOCKED STATE: Parent node(s) not yet unlocked
                btn.setDisabled(true);
                btn.setTouchable(Touchable.disabled);
                btn.setColor(Color.DARK_GRAY);
            } else if (currentMutagenMass < node.adaptation.mutagenCost) {
                // 3. UNAFFORDABLE STATE: Prerequisites met, but not enough mutagenMass
                btn.setDisabled(true);
                btn.setTouchable(Touchable.disabled);
                btn.setColor(Color.GRAY);
            } else {
                // 4. PURCHASABLE STATE: Prerequisites met and player can afford it
                btn.setDisabled(false);
                btn.setTouchable(Touchable.enabled);
                btn.setColor(Color.WHITE);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();

        // [PNG ART SWITCH - BACKGROUND]:
        // Draw your canvas background texture before lines and nodes (offset by getX() and getY()):
        // if (backgroundRegion != null) {
        //     batch.setColor(Color.WHITE);
        //     batch.draw(backgroundRegion, getX(), getY(), getWidth(), getHeight());
        // }

        // 1. Draw all connecting lines FIRST (so they sit behind the buttons)
        for (int i = 0; i < connections.size; i++) {
            SkillConnection conn = connections.get(i);

            // Offset by getX() and getY() so panning inside a ScrollPane moves the lines
            float startX = getX() + conn.parent.button.getX(Align.center);
            float startY = getY() + conn.parent.button.getY(Align.center);
            float endX = getX() + conn.child.button.getX(Align.center);
            float endY = getY() + conn.child.button.getY(Align.center);

            float dx = endX - startX;
            float dy = endY - startY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

            // Line lights up CYAN once the parent prerequisite is unlocked
            boolean pathActive = conn.parent.adaptation.unlocked;
            batch.setColor(pathActive ? Color.CYAN : Color.GRAY);
            batch.draw(
                lineRegion,
                startX, startY,
                0, 2f,          // Origin at left-center of the line thickness
                dist, 4f,       // Stretched length = dist, thickness = 4px
                1f, 1f,
                angle
            );
        }
        batch.setColor(Color.WHITE);

        // 2. Draw all child buttons ON TOP of the lines
        super.draw(batch, parentAlpha);
    }

    @Override
    public float getPrefWidth() {
        return 1920;
    }

    @Override
    public float getPrefHeight() {
        return 1080;
    }
}