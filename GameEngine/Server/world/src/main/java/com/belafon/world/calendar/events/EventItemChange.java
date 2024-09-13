package com.belafon.world.calendar.events;

import com.belafon.world.World;
import com.belafon.world.visibles.items.Item;

/**
 * Handles every type of Items change. It could be for example Foods decay process.
 */
public class EventItemChange extends Event{
    private Item item;
    public EventItemChange(long date, World game, Item item) {
        super(date, game);
        this.item = item;
    }

    @Override
    public void action(World game) {
        int date = item.changeStats(this ,game);
        if(date != 0){
            setDate(date + game.time.getTime());
            game.calendar.add(this);
        }
    }

    @Override
    public void interrupt(World game) {
    }
    
}
