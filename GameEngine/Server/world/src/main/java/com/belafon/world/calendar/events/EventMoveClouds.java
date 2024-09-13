package com.belafon.world.calendar.events;

import com.belafon.console.ConsolePrint;
import com.belafon.world.World;
import com.belafon.world.maps.Map;
import com.belafon.world.maps.place.Place;
import com.belafon.world.visibles.creatures.Creature;

public class EventMoveClouds extends Event {
    private final Map map;

    /**
     * Constructor for the EventMoveClouds class.
     * 
     * @param date        the date of the event
     * @param game
     * @param map         the map object on which clouds will be moved
     * @param printInLoop a flag to determine whether to print cloud status before
     *                    and after the move
     */
    public EventMoveClouds(long date, World game, Map map, boolean printInLoop) {
        super(date, game);
        this.map = map;
        this.printInLoop = printInLoop;
    }

    private final boolean printInLoop;

    /**
     * This method moves the clouds on the specified map and updates the creatures'
     * memory of cloud size.
     * 
     * @param game the game object
     */
    @Override
    public void action(World game) {
        if (printInLoop) {
            ConsolePrint.gameInfo("before CLOUD change:");
            ConsolePrint.gameInfo(map.sky.printClouds());
        }

        map.sky.moveClouds();

        synchronized (game.creatures) {
            // update creatures' memory of cloud size
            for (Creature creature : game.creatures) {
                if (creature.getLocation() instanceof Place place) {
                    int lastCloud = place.map.sky.getWeather(place).getClouds();
                    if (place.map == this.map
                            && creature.memory.getLastSizeOfClouds() != lastCloud) {
                        creature.writer.surrounding.setClouds(place);
                        creature.memory.setLastSizeOfClouds(lastCloud);
                    }
                }
            }
        }
        // ConsolePrint.success("EventMoveClouds", "at map " + map.id + " clouds
        // moved");
        if (printInLoop) {
            ConsolePrint.gameInfo("after CLOUD change:");
            ConsolePrint.gameInfo(map.sky.printClouds());
        }
    }

    @Override
    public void interrupt(World game) {

    }

}
