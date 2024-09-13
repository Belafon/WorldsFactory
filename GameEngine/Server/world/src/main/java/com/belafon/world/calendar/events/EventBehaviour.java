package com.belafon.world.calendar.events;

import com.belafon.world.World;
import com.belafon.world.visibles.creatures.behaviour.Behaviour;

public class EventBehaviour extends Event{
    private final Behaviour behaviour;
    public EventBehaviour(long date, World game, Behaviour behaviour) {
        super(date, game);
        this.behaviour = behaviour;
    }

    @Override
    public void action(World game) {
        behaviour.cease();
    }

    @Override
    public void interrupt(World game) {
        behaviour.interrupt(); 
    }
    
}
