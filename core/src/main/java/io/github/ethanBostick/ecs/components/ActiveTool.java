package io.github.ethanBostick.ecs.components;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class ActiveTool implements Component, Poolable{

    public ToolType tool = ToolType.DEFAULT;
    public BuildType buildTarget = null;

    public ActiveTool(){}
    
    @Override
    public void reset(){
        this.tool = ToolType.DEFAULT;
        this.buildTarget = null;
    }
}
