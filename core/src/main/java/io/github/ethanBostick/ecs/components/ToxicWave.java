package io.github.ethanBostick.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ToxicWave implements Component, Poolable {
    public int currentStep;
    public int maxSteps;
    public IntArray visitedHexes;

    @Override
    public void reset() {
        currentStep = 0;
        maxSteps = 0;
        visitedHexes.clear();
    }
}
