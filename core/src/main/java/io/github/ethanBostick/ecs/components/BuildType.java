package io.github.ethanBostick.ecs.components;

public enum BuildType {
    MYCELIUM_D1("myceliumD1Build.png", 3, 1),
    MYCELIUM_D2("myceliumD2Build.png", 3, 1),
    MYCELIUM_D3("myceliumD3Build.png", 3, 1),
    CLEANSER("cleanserMyceliumBuild.png", 6, 2),
    EXTRACTOR("extractorMyceliumBuild.png", 9, 3);

    private String spritePath;
    private int carbonCost;
    private int mineralCost;

    BuildType(String spritePath, int cCost, int mCost){
        this.spritePath = spritePath;
        this.carbonCost = cCost;
        this.mineralCost = mCost;
    }

    public String spritePath(){
        return this.spritePath;
    }
    public int carbonCost(){
        return this.carbonCost;
    }
    public int mineralCost(){
        return this.mineralCost;
    }
}
