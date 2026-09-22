package io.github.ethanBostick.ecs;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class VectorRenderSystem extends IteratingSystem{
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    public VectorRenderSystem() {
        //prio 10 so it renders at top
        super(Family.all(VectorArrow.class).get(), 10);
        this.batch = Registry.spriteBatch;
        this.camera = Registry.camera;
    }

    @Override
    public void update(float deltaTime) {

        if (this.camera != null){
            this.batch.setProjectionMatrix(this.camera.combined);
        }
        this.batch.begin();
        super.update(deltaTime);
        this.batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VectorArrow arrow = Mappers.VectorArrowCMap.get(entity);

        if(arrow.arrowTexture == null) return;

        //deltas
        float dx = arrow.endX - arrow.startX;
        float dy = arrow.endY - arrow.startY;

        //Euclidean Distance
        float distance = (float) Math.sqrt((dx * dx) + (dy * dy));

        //converted from radians to degrees for SpriteBatch
        float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;

        TextureRegion arrowRegion = new TextureRegion(arrow.arrowTexture);
        //draw(TextureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        batch.draw(
            arrowRegion, 
            arrow.startX, 
            arrow.startY, 
            0f,                                          
            15f / 2f, //png is 15f pixels tall
            distance,                                   
            15f,      
            1f, 1f,                                     
            angle                                       
        );
    }
}
