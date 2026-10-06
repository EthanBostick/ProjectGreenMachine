package io.github.ethanBostick.core;

public enum CommonValues {
    DEGENERATION_CARBON_PER_TICK(1f),
    DEGENERATION_MINERAL_PER_TICK(0f);

    public float value;

    CommonValues(float value){
        this.value = value;
    }
}
