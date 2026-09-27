package io.github.ethanBostick.ecs.components;

public enum BuildType {
    MYCELIUM_D1("myceliumD1Build.png"),
    MYCELIUM_D2("myceliumD2Build.png"),
    MYCELIUM_D3("myceliumD3Build.png"),
    EXTRACTOR("extractorMyceliumBuild.png");

    private String spritePath;

    BuildType(String spritePath){
        this.spritePath = spritePath;
    }

    public String spritePath(){
        return this.spritePath;
    }
}
