package io.github.ethanBostick.core;

public enum Adaptations {
    SURFACE_CONSTRUCTS(75, false),
    EXTRACTOR_RANGE(50, false),
    TRANSPORT_HYPHAE(75, false), 
    HARSH_CHEMICALS(75, false);    

    public int mutagenCost;
    public boolean unlocked;

    Adaptations(int mutagenCost, boolean unlocked){
        this.mutagenCost = mutagenCost;
        this.unlocked = unlocked;
    }

    public static void dispose(){
        SURFACE_CONSTRUCTS.unlocked = false;
        EXTRACTOR_RANGE.unlocked = false;
        TRANSPORT_HYPHAE.unlocked = false;
        HARSH_CHEMICALS.unlocked = false;

        SURFACE_CONSTRUCTS.mutagenCost = 75;
        EXTRACTOR_RANGE.mutagenCost = 50;
        TRANSPORT_HYPHAE.mutagenCost = 75;
        HARSH_CHEMICALS.mutagenCost = 75;
    }
}
