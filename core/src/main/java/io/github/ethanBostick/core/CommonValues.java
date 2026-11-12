package io.github.ethanBostick.core;

public enum CommonValues {
//game settings
    TICK_SPEED(0.25f),
    //DELTA_WINDOW_SIZE(30f), //unused, prev for calculating mineral and carbon gains over window size
    SIGHT_RADIUS(3f),
    EXTRACTION_RADIUS(0f),
    FACTORY_CLEANSE_MINERAL_THROUGHPUT_PER_TICK(1f),
    DEGENERATION_CARBON_PER_TICK(0.025f),
    DEGENERATION_HEALTH_PER_TICK(5f),
    DEGENERATION_MINERAL_PER_TICK(0f),
    GREEN_DECAY_MINERAL_PER_TICK(0.025f),
    GREEN_BASE_CARBON_OUTPUT_PER_HEX_PER_TICK(0.5f),
    GREEN_BASE_LEVEL_SCALAR(20f),
    GREEN_DECAY_HEALTH_PER_TICK(5f),
    CULTIVATE_MINERAL_PER_TILE(1f),
    TOXIC_WAVE_DMG(10f),
    TOXIC_WAVE_NUTRIENT_LEACH(10f),
    TOXIC_WAVE_CARBON_BLOCK_COST(2f),
    TOXIC_WAVE_MINERAL_BLOCK_COST(1f),
    CARBON_SYNTHESIS_RATE(0.5f),
    MINERAL_SYNTHESIS_RATE(0.25f),
    SPORE_RADIUS(12f),
    SPORES_PER_MUSHROOM(3f)
    ;


    public float value;

    CommonValues(float value){
        this.value = value;
    }

    public static void dispose(){
        TICK_SPEED.value=0.25f;
        SIGHT_RADIUS.value=3f;
        EXTRACTION_RADIUS.value=0f;
        FACTORY_CLEANSE_MINERAL_THROUGHPUT_PER_TICK.value=1f;
        DEGENERATION_CARBON_PER_TICK.value=0.025f;
        DEGENERATION_HEALTH_PER_TICK.value=5f;
        DEGENERATION_MINERAL_PER_TICK.value=0f;
        GREEN_DECAY_MINERAL_PER_TICK.value=0.025f;
        GREEN_BASE_CARBON_OUTPUT_PER_HEX_PER_TICK.value=0.5f;
        GREEN_BASE_LEVEL_SCALAR.value=20f;
        GREEN_DECAY_HEALTH_PER_TICK.value=5f;
        CULTIVATE_MINERAL_PER_TILE.value=1f;
        TOXIC_WAVE_DMG.value=10f;
        TOXIC_WAVE_NUTRIENT_LEACH.value=10f;
        TOXIC_WAVE_CARBON_BLOCK_COST.value=2f;
        TOXIC_WAVE_MINERAL_BLOCK_COST.value=1f;
        CARBON_SYNTHESIS_RATE.value=0.5f;
        MINERAL_SYNTHESIS_RATE.value=0.25f;
    }
}
