package com.belafon.world.visibles.creatures.behaviour.behaviours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.belafon.world.calendar.events.EventBehaviour;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.Behaviour;
import com.belafon.world.visibles.creatures.behaviour.BehaviourBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.BehaviourTypeBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType.IngredientsCounts;

public class Move extends Behaviour {
    public final Place destination;
    private ArrayList<Place> jurney = new ArrayList<Place>();
    private int currentPositionOfTravel = 0;

    public static final BehaviourType type;

    public static boolean checkIngredients(List<BehavioursPossibleIngredient> ingredients) {
        if (ingredients.size() != 1)
            throw new IllegalArgumentException("Move behaviour can have only one ingredient.");
        if (!(ingredients.get(0) instanceof UnboundedPlace))
            throw new IllegalArgumentException("Move behaviour can have only food as ingredient.");
        return true;
    }

    public static final BehaviourBuilder builder = (Creature creature,
            List<BehavioursPossibleIngredient> ingredients) -> {
        checkIngredients(ingredients);
        return new Move(creature, (Place) ingredients.get(0));
    };

    static {
        type = new BehaviourTypeBuilder("Move to other place", "Lets go somewhere...")
                .setBehaviourBuilder(builder)
                .setBehaviourClass(Move.class)
                .addRequirement(Place.REQUIREMENT_IS_REACHABLE, new IngredientsCounts(null, 1, 0))
                .build();
    }

    public Move(Creature creature, Place destination) {
        super(creature.game, 0, 0, creature);
        this.destination = destination;
        UnboundedPlace unboundedPlace = creature.getLocation();
        if (unboundedPlace instanceof Place place) {
            int x = place.positionX;
            int y = place.positionY;
            while (place != destination) {
                if (x < destination.positionX)
                    x++;
                else if (x > destination.positionX)
                    x--;
                if (y < destination.positionY)
                    y++;
                else if (y > destination.positionY)
                    y--;
                place = destination.map.places[x][y];
                jurney.add(place);
            }
            duration = getDurationOfTravel(creature, destination.map.getDistanceBetweenPlaces());
        } else {
            // TODO
        }
        type.requirements.size();
    }

    @Override
    public void execute() {
        cease();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void cease() {
        if (currentPositionOfTravel > 0)
            creature.setLocationInMap(jurney.get(currentPositionOfTravel - 1));

        if (currentPositionOfTravel == jurney.size())
            super.cease();

        if (currentPositionOfTravel < jurney.size()) {
            duration = getDurationOfTravel(creature, destination.map.getDistanceBetweenPlaces());
            event = new EventBehaviour(game.time.getTime() + duration, game, this);
            creature.game.calendar.add(event);
            currentPositionOfTravel++;
        }
    }

    @Override
    public String canCreatureDoThis() {
        if (creature.getLocation() instanceof Place place) {
            if (destination.map != place.map)
                return "cant_find_the_way!_The_destination_is_not_in_this_map.";
        }
        return null;
    }

    /**
     * @param creature
     * @param distance in meters
     * Returns duration of travel in minutes.
     */
    public int getDurationOfTravel(Creature creature, float distance) {
        float speed = getCurrentSpeed(creature);

        // distance / speed = time (+ hours have to be transfered to minutes)
        float durationOfTravel = distance / speed;

        // influence by health of player
        durationOfTravel += 150f - (2.1f * (float) creature.abilityCondition.getHealth()) + (0.006f
                * ((float) creature.abilityCondition.getHealth() * (float) creature.abilityCondition.getHealth()));

        // TODO debug purposes the duration is divided by 10
        return (int) durationOfTravel / 10; 
    }

    private float getCurrentSpeed(Creature creature) {
        if (creature.getLocation() instanceof Place place) {
            Place nextPlace = jurney.get(currentPositionOfTravel);

            // influence by Object altitude;
            int differenceOfAltitudes = nextPlace.altitude - place.altitude;
            float averageRoadDegree = (float) Math.atan(((float) differenceOfAltitudes * 3f) / 1000f);

             // Tobler's hiking function
            float averageSpeed = (float) (6 * Math.exp(-(3 / 2) * Math.abs(Math.tan(averageRoadDegree + (1 / 20)))));

            // averageSpeed *= (50/3); // translation killometers per hour to meters per
            // minutes
            // averageSpeed *= (3/5); // hiking through the nature out of the road (its more
            // difficult) ->
            averageSpeed *= 10;
            return averageSpeed;
        } else {
            // TODO
        }
        return 0f;
    }

    @Override
    public Map<BehavioursPossibleRequirement, IngredientsCounts> getRequirements() {
        return type.requirements;
    }

    @Override
    public BehaviourType getType() {
        return type;
    }
}
