package io.github.ethanBostick.core;

public enum CommonValues {
//game settings
    TICK_SPEED(0.25f),
    //DELTA_WINDOW_SIZE(30f), //unused, prev for calculating mineral and carbon gains over window size
    SIGHT_RADIUS(3f),
    DEGENERATION_CARBON_PER_TICK(0.025f),
    DEGENERATION_HEALTH_PER_TICK(5f),
    DEGENERATION_MINERAL_PER_TICK(0f),
    GREEN_DECAY_MINERAL_PER_TICK(0.025f),
    GREEN_BASE_CARBON_OUTPUT_PER_HEX_PER_TICK(0.5f),
    GREEN_BASE_LEVEL_SCALAR(10f),
    GREEN_DECAY_HEALTH_PER_TICK(5f),
    CULTIVATE_MINERAL_PER_TILE(1f),
    TOXIC_WAVE_DMG(10f),
    TOXIC_WAVE_NUTRIENT_LEACH(10f);


    public float value;

    CommonValues(float value){
        this.value = value;
    }
}
