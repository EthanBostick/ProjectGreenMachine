package io.github.ethanBostick.ecs.components;

public enum PriorityRole {
    TOP(0),
    UPKEEP(15),
    LOW(50),
    SURPLUS_CAPACITOR(100),
    NONE(Integer.MAX_VALUE);

    public int value;

    PriorityRole(int value){
        this.value = value;
    }
}
