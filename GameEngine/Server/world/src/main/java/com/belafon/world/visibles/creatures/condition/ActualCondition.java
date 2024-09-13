package com.belafon.world.visibles.creatures.condition;

import com.belafon.world.World;
import com.belafon.world.calendar.events.EventCreatureActualCondition;
import com.belafon.world.objectsMemory.ObjectsMemoryCell;
import com.belafon.world.objectsMemory.creaturesMemory.ActualConditionMemory;
import com.belafon.world.visibles.creatures.Creature;

/**
 * creatures stats, that are implicitly changeing during the time
 */
public class ActualCondition {
    private int hunger = 100;
    private EventCreatureActualCondition eventHunger;
    private static final int HUNGER_SPEED = 18; // 15 hours, 15 * 20 / 100
    private int fatigueMax; // = barrier for rest, cannot increase fatigue beyond this limit, only through
                            // sleep, about
    private EventCreatureActualCondition eventMaxFatigue;
    // private int energy; // -> can be easily restored through rest, e.g. during
    // combat --- not necessary
    private static final int MAX_FATIGUE_SPEED = 45; // 3 days, 3 * 24 * 20 / 100 = 14.4
    private int heat = 100;
    private EventCreatureActualCondition eventHeat;
    private final int BODY_BORDER_HEAT_IN_CELSIUS = 24; // degree of celsius of ideal temperature
    private final int HEAT_CONST = 1;
    private static final int BODY_HEAT_SPEED = 15;
    private int bleeding = 10;
    private EventCreatureActualCondition eventBleeding;
    private static final int BLEEDING_SPEED = 6;

    // private ArrayList<Disease> diseases;
    // private ArrayList<Injury> injuries;
    private Creature creature;
    public final ActualConditionMemory memory = new ActualConditionMemory();

    public ActualCondition(Creature creature) {
        this.creature = creature;
        // diseases = new ArrayList<>();
        // injuries = new ArrayList<>();
        World game = creature.game;
        long duration = 0;
        if (game.time == null)
            duration = 1;
        else
            duration = game.time.getTime();
        eventHunger = new EventCreatureActualCondition(duration, game, this::setEventHunger);
        eventBleeding = new EventCreatureActualCondition(duration, game, this::setEventBleeding);
        eventHeat = new EventCreatureActualCondition(duration, game, this::setEventHeat);
        eventMaxFatigue = new EventCreatureActualCondition(duration, game, this::setEventMaxFatigue);
        game.calendar.add(eventHunger);
        game.calendar.add(eventBleeding);
        game.calendar.add(eventHeat);
        game.calendar.add(eventMaxFatigue);
    }

    private void setEventHunger(Object object) {
        setHunger(getHunger() - 1);
        World game = creature.game;
        int duration = HUNGER_SPEED;
        if (creature.abilityCondition.getHealth() != 0) {
            eventHunger = new EventCreatureActualCondition(game.time.getTime() + duration, game, this::setEventHunger);
            game.calendar.add(eventHunger);
        }
    }

    public void setEventMaxFatigue(Object object) {
        setFatigueMax(getFatigueMax() - 1);
        World game = creature.game;
        int duration = MAX_FATIGUE_SPEED;
        if (fatigueMax != 0) {
            eventMaxFatigue = new EventCreatureActualCondition(game.time.getTime() + duration, game,
                    this::setEventMaxFatigue);
            game.calendar.add(eventMaxFatigue);
        }
    }

    public void setEventHeat(Object object) {
        World game = creature.game;
        int temperatureDifference = BODY_BORDER_HEAT_IN_CELSIUS + creature.abilityCondition.getCurrentEnergyOutput()
                + creature.inventory.gear.warm - creature.getLocation().getTemperature();

        int speed = temperatureDifference * HEAT_CONST; // TODO / weight of creature
        setHeat(getHeat() - speed);

        if (creature.abilityCondition.getHealth() > 0) {
            eventHeat = new EventCreatureActualCondition(game.time.getTime() + BODY_HEAT_SPEED, game,
                    this::setEventHeat);
            game.calendar.add(eventHeat);
        }
    }

    public void setEventBleeding(Object object) {
        creature.abilityCondition.setHealth(creature.abilityCondition.getHealth() - 1);
        setBleeding(getBleeding() - 1);
        World game = creature.game;
        int duration = BLEEDING_SPEED;
        if (bleeding != 0 && creature.abilityCondition.getHealth() > 0) {
            eventBleeding = new EventCreatureActualCondition(game.time.getTime() + duration, game,
                    this::setEventBleeding);
            game.calendar.add(eventBleeding);
        }
    }

    // getters and setters
    public synchronized int getHunger() {
        return hunger;
    }

    public synchronized void setHunger(int hunger) {

        if (hunger > 100)
            hunger = 100;
        if (hunger < 0) {
            creature.abilityCondition.setHealth(creature.abilityCondition.getHealth() + hunger);
            hunger = 0;
        }
        if (hunger != this.hunger)
            creature.writer.condition.setHunger(hunger);
        memory.addHunger(new ObjectsMemoryCell<Integer>(creature.game.time.getTime(), hunger));
        this.hunger = hunger;
    }

    public synchronized int getBleeding() {
        return bleeding;
    }

    public synchronized void setBleeding(int bleeding) {

        if (bleeding > 100)
            bleeding = 100;
        else if (bleeding < 0)
            bleeding = 0;
        if (bleeding != this.bleeding)
            creature.writer.condition.setBleeding(bleeding);
        memory.addBleeding(new ObjectsMemoryCell<Integer>(creature.game.time.getTime(), bleeding));
        this.bleeding = bleeding;
    }

    public synchronized int getHeat() {
        return heat;
    }

    /**
     * When hunger is 0,
     * health is decreasing
     * 
     * @param object
     */
    public synchronized void setHeat(int heat) {
        if (heat > 100)
            heat = 100;
        else if (this.heat == 0 && heat < 0) {
            creature.abilityCondition.setHealth(creature.abilityCondition.getHealth() + heat);
            return;
        } else if (heat < 0)
            heat = 0;
        if (heat != this.heat)
            creature.writer.condition.setHeat(heat);
        this.heat = heat;
    }

    public synchronized int getFatigueMax() {
        return fatigueMax;
    }

    public synchronized void setFatigueMax(int fatigueMax) {

        if (fatigueMax > 100)
            fatigueMax = 100;
        if (fatigueMax < 10) { // pass away
            /*
             * for(Behaviour behaviour : Creature.getCurrentBehaviour()) {
             * game.calendar.deletePlayersCurrentBehaviourAction(Creature,
             * Creature.getBehaviourPositionInArray(behaviour));
             * game.behavior.make_sleep(5 * 20, Creature);
             * }
             */
            fatigueMax = 0;
        }
        if (fatigueMax != this.fatigueMax)
            creature.writer.condition.setFatigueMax(fatigueMax);
        memory.addFatigueMax(new ObjectsMemoryCell<Integer>(creature.game.time.getTime(), fatigueMax));
        this.fatigueMax = fatigueMax;
    }

    public synchronized void cancelHungerEvent() {
        eventHunger.cancelEvent(creature.game);
    }

    public synchronized void cancelMaxFatigueEvent() {
        eventMaxFatigue.cancelEvent(creature.game);
    }

    public synchronized void cancelHeatEvent() {
        eventHeat.cancelEvent(creature.game);
    }

    public synchronized void cancelBleedingEvent() {
        eventBleeding.cancelEvent(creature.game);
    }

    public synchronized void cancelAllEvents() {
        cancelBleedingEvent();
        cancelHeatEvent();
        cancelHungerEvent();
        cancelMaxFatigueEvent();
    }
}
