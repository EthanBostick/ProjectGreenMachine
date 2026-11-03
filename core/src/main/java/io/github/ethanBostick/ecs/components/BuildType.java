package io.github.ethanBostick.ecs.components;

public enum BuildType {
    MYCELIUM_D1("myceliumD1Build.png",null, 3, 1),
    MYCELIUM_D2("myceliumD2Build.png",null, 3, 1),
    MYCELIUM_D3("myceliumD3Build.png",null, 3, 1),
    CLEANSER("cleanserBuild.png","cleanserSurfaceBuild.png", 6, 2),
    CULTIVATOR("cultivatorMyceliumBuild.png","cleanserSurfaceBuild.png", 6, 2),
    EXTRACTOR("extractorMyceliumBuild.png",null, 9, 3),
    CARBON_SYPHON("carbonSyphonBuild.png","carbonSyphonSurfaceBuild.png", 12, 6),
    CONDUIT("hyperNodeBuild.png",null, 6, 2),
    STORAGE("storageMyceliumBuild.png",null, 9, 3);

    public String spritePath;
    public String surfaceSpritePath;
    public int carbonCost;
    public int mineralCost;

    BuildType(String spritePath, String surfaceSpritePath, int cCost, int mCost){
        this.spritePath = spritePath;
        this.surfaceSpritePath = surfaceSpritePath;
        this.carbonCost = cCost;
        this.mineralCost = mCost;
    }
}
