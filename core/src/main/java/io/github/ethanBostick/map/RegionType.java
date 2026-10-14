package io.github.ethanBostick.map;

import com.badlogic.gdx.graphics.Texture;

import io.github.ethanBostick.utils.TextureUtils;

public enum RegionType {
    RADIOACTIVE(ResourceType.CARBON, 1.5f,100, 0.50f,1.50f,"radioactiveTileOverlay.png"),
    POLLUTED(ResourceType.CARBON, 1.75f,60,0.75f,1.25f, "pollutedTileOverlay.png"),
    BARREN(ResourceType.MINERAL,1.5f,80,1.00f,1.15f, "barrenTileOverlay.png"),
    MOUNTAIN(ResourceType.MINERAL,1.75f,40,0,1f, "mountainTileOverlay.png"),
    RESTORED(ResourceType.CARBON,2f,15,0,1f,"restoredTileOverlay.png"),
    BLANK(ResourceType.NONE,0f,-1,0,0,null);

    private ResourceType resourceType;
    private float resourceScalar;
    private int temp;
    private float cleansingScalar;
    private float degenerationScalar;
    public Texture overlayTexture;

    RegionType(ResourceType resourceType, float resourceScalar, int temp, float cleansingScalar, float degenerationScalar,String overlayPath){
        this.resourceType = resourceType;
        this.resourceScalar = resourceScalar;
        this.temp = temp;
        this.cleansingScalar = cleansingScalar;
        this.degenerationScalar = degenerationScalar;
        if (overlayPath != null){
            this.overlayTexture = TextureUtils.pathToTexture(overlayPath);
        }
        else{
            this.overlayTexture = null;
        }
    }

    public ResourceType resourceType(){
        return this.resourceType;
    }
    public float resourceScalar(){
        return this.resourceScalar;
    }
    public float cleansingScalar(){
        return this.cleansingScalar;
    }
    public float degenerationScalar(){
        return this.degenerationScalar;
    }
    public int tempValue(){
        return this.temp;
    }
}
