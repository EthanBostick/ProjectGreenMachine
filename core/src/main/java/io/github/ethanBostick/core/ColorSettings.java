package io.github.ethanBostick.core;

import com.badlogic.gdx.graphics.Color;

public enum ColorSettings {
    CARBON_GAINS(Color.GREEN),
    CARBON_LOSSES(Color.ORANGE),
    MINERAL_GAINS(Color.CYAN),
    MINERAL_LOSSES(Color.PURPLE),
    HEALTH_LOSSES(Color.RED);

    public Color color;

    ColorSettings(Color color){
        this.color = color;
    }
}
