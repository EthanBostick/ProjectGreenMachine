package io.github.ethanBostick.core;

public enum CommonValues {
    DELTA_WINDOW_SIZE(30f),
    SIGHT_RADIUS(3f),
    DEGENERATION_CARBON_PER_TICK(0.025f), //about 1.5 carbon every 60 seconds
    DEGENERATION_MINERAL_PER_TICK(0f),
    GREEN_DECAY_MINERAL_PER_TICK(0.025f),
    GREEN_BASE_CARBON_OUTPUT_PER_TICK(1f),
    CULTIVATE_MINERAL_PER_TILE(1f);

    public float value;

    CommonValues(float value){
        this.value = value;
    }
}
