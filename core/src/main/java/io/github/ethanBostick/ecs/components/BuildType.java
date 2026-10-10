package io.github.ethanBostick.ecs.components;

public enum BuildType {
    MYCELIUM_D1("myceliumD1Build.png", 3, 1),
    MYCELIUM_D2("myceliumD2Build.png", 3, 1),
    MYCELIUM_D3("myceliumD3Build.png", 3, 1),
    CLEANSER("cleanserMyceliumBuild.png", 6, 2),
    EXTRACTOR("extractorMyceliumBuild.png", 9, 3),
    STORAGE("storageMyceliumBuild.png", 9, 3);

    public String spritePath;
    public int carbonCost;
    public int mineralCost;

    BuildType(String spritePath, int cCost, int mCost){
        this.spritePath = spritePath;
        this.carbonCost = cCost;
        this.mineralCost = mCost;
    }
}
