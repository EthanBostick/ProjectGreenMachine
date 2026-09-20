package io.github.ethanBostick.map;

public enum BiomeType {
    GRASS_LAND(ResourceType.MINERAL,1.5f,75),
    DESERT(ResourceType.MINERAL, 0.25f,100),
    FOREST(ResourceType.CARBON, 1.5f,65),
    TAIGA(ResourceType.CARBON,1.15f,15),
    BOREAL(ResourceType.CARBON,0.90f,35),
    MOUNTAIN(ResourceType.MINERAL,0.9f,50),
    BLANK(ResourceType.NONE,0f,-1);

    private ResourceType resourceType; //1 = minerals, 0 = carbons
    private float resourceScalar;
    private int temp;

    BiomeType(ResourceType resourceType, float resourceScalar, int temp){
        this.resourceType = resourceType;
        this.resourceScalar = resourceScalar;
        this.temp = temp;
    }

    public ResourceType resourceType(){
        return this.resourceType;
    }
    public float resourceScalar(){
        return this.resourceScalar;
    }
    public int tempValue(){
        return this.temp;
    }
}
