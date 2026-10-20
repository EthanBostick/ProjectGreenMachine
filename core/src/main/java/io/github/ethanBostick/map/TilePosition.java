package io.github.ethanBostick.map;

public enum TilePosition {
    UNDERGROUND(0),
    RESOURCE_NODE(1),
    UNDERGROUND_TERRAIN(2),
    MYCELIUM(3),
    SURFACE(4),
    SURFACE_MYCELIUM(5),
    SURFACE_ENTITY(6),
    MAX_POSITIONS(7);

    private int position;

    TilePosition(int p){
        this.position = p;
    }

    public int value(){
        return this.position;
    }
}
