package com.belafon.world.visibles.creatures.behaviour.behaviours;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.belafon.console.ConsolePrint;
import com.belafon.world.calendar.events.EventBehaviour;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.objectsMemory.ObjectsMemoryCell;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.Behaviour;
import com.belafon.world.visibles.creatures.behaviour.BehaviourBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.BehaviourTypeBuilder;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType.IngredientsCounts;
import com.belafon.world.visibles.resources.Resource;
import com.belafon.world.visibles.resources.TypeOfResource;

public class FindConcreteResource extends Behaviour {
    private final Resource resource;
    private final TypeOfResource typeOfResource;
    private boolean found = false;
    private int durationOfFinding; // -1 will not be found

    public static final BehaviourBuilder builder = (Creature creature,
            List<BehavioursPossibleIngredient> ingredients) -> {
        if (ingredients.size() != 1)
            throw new IllegalArgumentException("FindConcreteResource needs 1 ingredient.");
        return new FindConcreteResource(20, creature.abilityCondition.getObservation(), creature,
                (TypeOfResource) ingredients.get(0));
    };

    public static final BehaviourType type;
    static {
        type = new BehaviourTypeBuilder("Find some nature resource", "Lets find some resource...")
                .setBehaviourBuilder(builder)
                .setBehaviourClass(FindConcreteResource.class)
                .addRequirement(Place.REQUIREMENT_IS_REACHABLE, new IngredientsCounts(null, 1, 0))
                .addRequirement(TypeOfResource.REQUIREMENT_RESOURCE_IS_KNOWN, new IngredientsCounts(null, 1, 0))
                .build();
    }

    public FindConcreteResource(int duration, int bodyStrain, Creature creature,
            TypeOfResource typeResource) {
        super(creature.game, duration, bodyStrain, creature);
        this.typeOfResource = typeResource;
        resource = creature.getLocation().resources.get(typeResource);
    }

    @Override
    public void execute() {
        // 0 -> finds instantly p=1, -1 -> cant find, p=100/(x+100) p=0.5 -> x=100
        // getVisibility -> for example fog ;
        durationOfFinding = -creature.abilityCondition.getVision() + creature.getLocation().getVisibility();

        if (resource != null && resource.durationOfFinding != -1) {
            found = true;

            // lower means I will find it earlier
            durationOfFinding += resource.durationOfFinding;
            // TODO possiable extension gauss function a*e^(-(x-posun_doprava)/2*const^2),
            // shift left by the durationOfFinding
        } else
            durationOfFinding += getDurationOfFindingOfResourceWhichIsNotHere(creature
                    .getLocation(), typeOfResource);
        // duration of when Creature finds out that, there is nothing
        if (durationOfFinding < 0)
            durationOfFinding = 0;
        super.event = new EventBehaviour(game.time.getTime() + durationOfFinding, game, this);
        game.calendar.add(event);
    }

    @Override
    public void interrupt() {
        // TODO maybe save that the creature was finding that some time

    }

    @Override
    public void cease() {
        if (found) {
            ConsolePrint.success("FindConcreteResource", "Resource was found!!!");
            creature.memory.addVisibleObjectLostFromSight(
                    new ObjectsMemoryCell<Visible>(creature.game.time.getTime(), resource), resource.getLocation(),
                    creature);
            creature.writer.surrounding.setResource(resource);
        } else {
            ConsolePrint.success("FindConcreteResource", "Ressource was NOT found!!!");
        }
    }

    // TODO can not be called when addResource() is called
    public static int getDurationOfFindingOfResourceWhichIsNotHere(UnboundedPlace place,
            TypeOfResource typeOfResource) {
        int sum = 0;
        int i = 0;
        int anonymConspicuousnessOfFindingResource = typeOfResource.conspicuousness / 2; // amount of it is 50
        while (i < place.resourcesSorted.length) {
            if (place.resourcesSorted[i].getConspicuousness() <= anonymConspicuousnessOfFindingResource)
                break;
            sum += place.resourcesSorted[i].getConspicuousness();
            i++;
        }
        return sum - i * anonymConspicuousnessOfFindingResource;
    }

    /**
     * 
     * @param place
     * @param resource
     * @throws Exception
     *                   this method sets duration of finding concrete resource in
     *                   concrete place
     */
    public static synchronized void setResourcesDurationOfFinding(UnboundedPlace place, Resource resource)
            throws Exception {
        int lastIndex = Arrays.asList(place.resourcesSorted).indexOf(resource);
        if (lastIndex == -1)
            throw new Exception("Value exception");

        int newIndex = Place.binarySearchTheClosestLower(place.resourcesSorted, resource);

        if (lastIndex < newIndex) {
            Resource last = place.resourcesSorted[newIndex - 1];
            place.resourcesSorted[newIndex - 1] = resource;
            for (int i = newIndex - 2; i >= lastIndex; i--) {
                Resource actual = place.resourcesSorted[i];
                place.resourcesSorted[i] = last;
                last = actual;
            }
        } else if (newIndex < lastIndex) {
            Resource last = resource;
            for (int i = newIndex; i <= lastIndex - 1; i++) {
                Resource actual = place.resourcesSorted[i];

                place.resourcesSorted[i] = last;
                last = actual;
            }
            place.resourcesSorted[lastIndex] = last;
        } // else do nothing
    }

    @Override
    public Map<BehavioursPossibleRequirement, IngredientsCounts> getRequirements() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequirements'");
    }

    @Override
    public BehaviourType getType() {
        return type;
    }

}
