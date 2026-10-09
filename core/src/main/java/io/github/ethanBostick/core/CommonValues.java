package io.github.ethanBostick.core;

public enum CommonValues {
    SIGHT_RADIUS(3f),
    DEGENERATION_CARBON_PER_TICK(0f),
    DEGENERATION_MINERAL_PER_TICK(0f);

    public float value;

    CommonValues(float value){
        this.value = value;
    }
}
