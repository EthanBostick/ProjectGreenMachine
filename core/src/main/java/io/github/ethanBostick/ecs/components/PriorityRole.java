package io.github.ethanBostick.ecs.components;

public enum PriorityRole {
    TOP(0, 4),
    UPKEEP(15, 3),
    LOW(50, 2),
    SURPLUS_CAPACITOR(100, 1),
    NONE(Integer.MAX_VALUE, 0);

    public int value;
    public int index;

    PriorityRole(int value, int index){
        this.value = value;
        this.index = index;
    }
    public PriorityRole intToRole(int i){
        switch(i){
            case 0:
                return NONE;
            case 1:
                return SURPLUS_CAPACITOR;
            case 2:
                return LOW;
            case 3:
                return UPKEEP;
            case 4:
                return TOP;
        }
        return NONE;
    }
}
