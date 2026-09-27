package io.github.ethanBostick.map;

public enum RegionType {
    RADIOACTIVE(ResourceType.CARBON, 1.5f,100),
    POLLUTED(ResourceType.CARBON, 1.75f,60),
    BARREN(ResourceType.MINERAL,1.5f,80),
    MOUNTAIN(ResourceType.MINERAL,1.75f,40),
    RESTORED(ResourceType.CARBON,2f,15),
    BLANK(ResourceType.NONE,0f,-1);

    private ResourceType resourceType;
    private float resourceScalar;
    private int temp;

    RegionType(ResourceType resourceType, float resourceScalar, int temp){
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
