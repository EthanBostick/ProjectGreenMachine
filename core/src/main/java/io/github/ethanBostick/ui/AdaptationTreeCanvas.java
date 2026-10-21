package io.github.ethanBostick.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class AdaptationTreeCanvas extends WidgetGroup {
    private final Skin skin;
    private final TextureRegion lineRegion;
    private final Array<SkillConnection> connections = new Array<>();

    // Simple internal data struct for drawing lines between two nodes
    private static class SkillConnection {
        final Actor parent;
        final Actor child;
        boolean unlocked = false;

        SkillConnection(Actor parent, Actor child) {
            this.parent = parent;
            this.child = child;
        }
    }

    public AdaptationTreeCanvas(Skin skin, Texture lineTexture) {
        this.skin = skin;
        this.lineRegion = new TextureRegion(lineTexture);
        
        // Define the virtual size of the pannable canvas
        setSize(1920, 1080);

        buildTree();
    }

    private void buildTree() {
        //each passes a runnable for when it is clicked
        TextButton rootNode = addSkillNode("Extract Rocks", 960, 100, () -> {
            System.out.println("Unlocked extract rocks!");
        });

        TextButton constructs = addSkillNode("Surface Constructs", 960-200, 200, () -> {
            System.out.println("Unlocked Density 2!");
        });

        TextButton conduitNodes = addSkillNode("Mycelium Conduits", 960+200, 200, () -> {
            System.out.println("Unlocked Hyper Conduit!");
        });

        TextButton extractorRadius = addSkillNode("Extractor Area +1", 960, 200, () -> {
            System.out.println("Unlocked Deep Cleanse!");
        });

        //Link them together so lines automatically render behind them
        connect(rootNode, conduitNodes);
        connect(rootNode, constructs);
        connect(rootNode, extractorRadius);
    }

    /** Factory helper to create, position, and register a clickable skill node. */
    private TextButton addSkillNode(String name, float centerX, float centerY, Runnable onClick) {
        // You can swap TextButton for ImageButton once you have custom node PNGs!
        TextButton nodeBtn = new TextButton(name, skin);
        nodeBtn.setSize(140, 60);
        // Position the button centered on (centerX, centerY)
        nodeBtn.setPosition(centerX, centerY, Align.center);

        nodeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });

        // Add the button as a child Actor of this WidgetGroup
        this.addActor(nodeBtn);
        return nodeBtn;
    }

    /** Links two nodes visually with a connecting branch line. */
    private void connect(Actor parent, Actor child) {
        connections.add(new SkillConnection(parent, child));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();

        // 1. Draw all connecting lines FIRST (so they sit behind the buttons)
        for (int i = 0; i < connections.size; i++) {
            SkillConnection conn = connections.get(i);

            // Offset by getX() and getY() so panning inside a ScrollPane moves the lines
            float startX = getX() + conn.parent.getX(Align.center);
            float startY = getY() + conn.parent.getY(Align.center);
            float endX = getX() + conn.child.getX(Align.center);
            float endY = getY() + conn.child.getY(Align.center);

            float dx = endX - startX;
            float dy = endY - startY;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

            batch.setColor(conn.unlocked ? Color.CYAN : Color.GRAY);
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
        return 1920; // Must be larger than your 1920x1080 screen to pan horizontally!
    }

    @Override
    public float getPrefHeight() {
        return 1080; // Must be larger than your 1920x1080 screen to pan vertically!
    }
}