package io.github.ethanBostick.ecs;

public enum BiomeType {
    GRASS_LAND(.35f),
    DESERT(.15f),
    FOREST(.50f),
    TAIGA(.80f),
    BOREAL(.95f),
    MOUNTAIN(.25f),
    BLANK(0f);

    private float nutrientScalar;

    BiomeType(float nutrientScalar){
        this.nutrientScalar = nutrientScalar;
    }

    public float nutrientScalar(){
        return this.nutrientScalar;
    }
}
