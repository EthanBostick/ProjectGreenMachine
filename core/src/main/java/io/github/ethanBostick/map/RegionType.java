package io.github.ethanBostick.map;

public enum RegionType {
    RADIOACTIVE(ResourceType.CARBON, 1.5f,100, 0.75f),
    POLLUTED(ResourceType.CARBON, 1.75f,60,1.00f),
    BARREN(ResourceType.MINERAL,1.5f,80,1.25f),
    MOUNTAIN(ResourceType.MINERAL,1.75f,40,0),
    RESTORED(ResourceType.CARBON,2f,15,0),
    BLANK(ResourceType.NONE,0f,-1,0);

    private ResourceType resourceType;
    private float resourceScalar;
    private int temp;
    private float cleansingScalar;

    RegionType(ResourceType resourceType, float resourceScalar, int temp, float cleansingScalar){
        this.resourceType = resourceType;
        this.resourceScalar = resourceScalar;
        this.temp = temp;
        this.cleansingScalar = cleansingScalar;
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
    public int tempValue(){
        return this.temp;
    }
}
