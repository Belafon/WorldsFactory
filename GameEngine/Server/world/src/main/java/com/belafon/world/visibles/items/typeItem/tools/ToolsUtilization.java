package com.belafon.world.visibles.items.typeItem.tools;

import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;

public class ToolsUtilization extends BehavioursPossibleRequirement {
    public final String name;

    public ToolsUtilization(String name) {
        super("It has tool with utilitzation: " + name);
        this.name = name;
    }
    
}
