package io.github.ethanBostick.core;

public enum OverlayMap {
    NONE(-1),
    REGION(0),
    GREEN(1)
    ;
    
    public int value;
    OverlayMap(int value){
        this.value = value;
    }
}
