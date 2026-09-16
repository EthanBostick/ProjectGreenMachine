package io.github.ethanBostick.map;

public enum TilePosition {
    UNDERGROUND(0),
    MYCELIUM(1),
    SURFACE(2),
    TERRAIN(3);

    private int position;

    TilePosition(int p){
        this.position = p;
    }

    public int value(){
        return this.position;
    }
}
