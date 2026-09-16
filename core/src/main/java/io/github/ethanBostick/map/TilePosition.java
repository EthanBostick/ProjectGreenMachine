package io.github.ethanBostick.map;

public enum TilePosition {
    UNDERGROUND(0),
    SURFACE(1),
    INANIMATE(2);

    private int position;

    TilePosition(int p){
        this.position = p;
    }

    public int value(){
        return this.position;
    }
}
