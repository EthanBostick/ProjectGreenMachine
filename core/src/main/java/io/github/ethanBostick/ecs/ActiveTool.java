package io.github.ethanBostick.ecs;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.ashley.core.Component;

public class ActiveTool implements Component, Poolable{

    public ToolType tool = ToolType.DEFAULT;

    public ActiveTool(){}
    
    @Override
    public void reset(){
        this.tool = ToolType.DEFAULT;
    }
}
