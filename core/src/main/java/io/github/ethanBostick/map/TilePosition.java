package io.github.ethanBostick.map;

public enum TilePosition {
    UNDERGROUND(0),
    UNDERGROUND_TERRAIN(1),
    MYCELIUM(2),
    SURFACE(3),
    TERRAIN(4),
    MAX_POSITIONS(5);

    private int position;

    TilePosition(int p){
        this.position = p;
    }

    public int value(){
        return this.position;
    }
}
