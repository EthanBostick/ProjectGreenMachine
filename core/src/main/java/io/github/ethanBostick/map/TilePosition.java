package io.github.ethanBostick.map;

public enum TilePosition {
    TILE(0),
    ENTITY(1),
    SELECTED(2);

    private int position;

    TilePosition(int p){
        this.position = p;
    }

    public int value(){
        return this.position;
    }
}
