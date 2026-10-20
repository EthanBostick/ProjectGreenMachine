package io.github.ethanBostick.map;

import com.badlogic.gdx.graphics.Texture;

import io.github.ethanBostick.utils.TextureUtils;

public enum RegionType {
    RADIOACTIVE("radioactive",ResourceType.CARBON, 1.5f,100, 0.50f,1.50f,"radioactiveTileOverlay.png"),
    POLLUTED("polluted",ResourceType.CARBON, 1.75f,60,0.75f,1.25f, "pollutedTileOverlay.png"),
    BARREN("barren",ResourceType.MINERAL,1.5f,80,1.00f,1.15f, "barrenTileOverlay.png"),
    MOUNTAIN("mountain",ResourceType.MINERAL,1.75f,40,0,1f, "mountainTileOverlay.png"),
    RESTORED("restored",ResourceType.CARBON,2f,15,0,1f,"restoredTileOverlay.png"),
    BLANK(null,ResourceType.NONE,0f,-1,0,0,null);

    public String name;
    private ResourceType resourceType;
    private float resourceScalar;
    private int temp;
    private float cleanserScalar;
    private float degenerationScalar;
    public Texture overlayTexture;

    RegionType(String name, ResourceType resourceType, float resourceScalar, int temp, float cleanserScalar, float degenerationScalar,String overlayPath){
        this.name = name;
        this.resourceType = resourceType;
        this.resourceScalar = resourceScalar;
        this.temp = temp;
        this.cleanserScalar = cleanserScalar;
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
    public float cleanserScalar(){
        return this.cleanserScalar;
    }
    public float degenerationScalar(){
        return this.degenerationScalar;
    }
    public int tempValue(){
        return this.temp;
    }
}
