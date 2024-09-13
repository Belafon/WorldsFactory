package com.belafon.world.maps.place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.belafon.console.ConsolePrint;
import com.belafon.world.World;
import com.belafon.world.maps.placeEffects.PlaceEffect;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.BehavioursPossibleIngredientID;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.resources.Resource;
import com.belafon.world.visibles.resources.TypeOfResource;
import com.belafon.world.visibles.resources.TypeOfResourceOfTypeOfPlace;

public abstract class UnboundedPlace implements BehavioursPossibleIngredient {
    public static final BehavioursPossibleRequirement REQUIREMENT_IS_VISIBLE = new BehavioursPossibleRequirement(
            "A place is visible") {
    };



    public final List<Creature> creatures = Collections.synchronizedList(new ArrayList<>());
    public final List<Item> items = Collections.synchronizedList(new ArrayList<>());
    public Hashtable<TypeOfResource, Resource> resources = new Hashtable<>();
    public final List<PlaceEffect> effects = Collections.synchronizedList(new ArrayList<>());
    public TypeOfPlace typeOfPlace;
    public volatile Resource[] resourcesSorted;
    public World game;
    protected volatile int visibility = 0;
    public final String music;
    public final String picture;
    public final int id;

    public UnboundedPlace(TypeOfPlace typeOfPlace, World game) {
        this.typeOfPlace = typeOfPlace;
        this.game = game;
        setStartResources(typeOfPlace);
        music = typeOfPlace.getMusic();
        picture = typeOfPlace.getPicture();
        if (game != null)
            id = game.visibleIds.getPlaceId();
        else
            id = Integer.MIN_VALUE;
    }

    public void recountResourcesDurationOfFinding() {
        resourcesSorted[0].durationOfFinding = 0;
        int sum = resourcesSorted[0].getConspicuousness();
        for (int i = 1; i < resourcesSorted.length; i++) {

            // sum of differences of consp.
            resourcesSorted[i].durationOfFinding = sum - resourcesSorted[i].getConspicuousness() * i;
            sum += resourcesSorted[i].getConspicuousness();
        }
    }

    // TODO can not be called, when amount of some resource is changed
    /**
     * Adds new resource to the place.
     * The list of resources has to be resorted according resources
     * conspicuousness and amount.
     */
    public Resource addResource(TypeOfResource typeOfResource, int amount) {
        Resource resource = new Resource(typeOfResource, amount, game, this);

        // we have to chack if there is resource with that typeOfResource
        if (resources.containsKey(typeOfResource)) {
            ConsolePrint.warning("UnboundedPlace: Method addResource() was called to add resource with identical type!");
            return resources.get(typeOfResource);
        }

        // now we need to insert it to the resources array
        resources.put(typeOfResource, resource);
        Resource[] newArray = new Resource[resourcesSorted.length + 1];
        int index = binarySearchTheClosestLower(resourcesSorted, resource);
        for (int i = 0; i < newArray.length; i++)
            if (i < index)
                newArray[i] = resourcesSorted[i];
            else if (i > index)
                newArray[i] = resourcesSorted[i - 1];
            else if (i == index)
                newArray[i] = resource;
        resourcesSorted = newArray;
        recountResourcesDurationOfFinding();
        return resource;
    }

    /**
     * Adds an item into the list of the place.
     * 
     * @param item
     */
    public void addItem(Item item) {
        synchronized (items) {
            items.add(item);
        }
    }

    /**
     * Adds a place effect into list of place effects.
     * Finds all creatures watching this place and sends info to them
     * about the new place effect.
     * 
     * @param effect
     */
    public void addEffect(PlaceEffect effect) {
        effects.add(effect);
        // TODO find surrounding creatures and send info
    }

    /*
     * generates resources in the Place, sorts them by conspicuousness and sets
     * duartionOfFinding value of each generated resource
     */
    protected void setStartResources(TypeOfPlace typeOfPlace2) {
        for (TypeOfResourceOfTypeOfPlace typeOfResource : typeOfPlace.typeOfResources) // TODO needs debug
            if (typeOfResource.isHereResourceGenerate())
                resources.put(typeOfResource.typeOfResource,
                        new Resource(typeOfResource.typeOfResource, typeOfResource.startAmount, game, this));
        resourcesSorted = resources.values().toArray(new Resource[resources.values().size()]);

        if (resourcesSorted.length < 1)
            return;
        Arrays.sort(resourcesSorted, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return o2.getConspicuousness() - o1.getConspicuousness();
            }
        });
        recountResourcesDurationOfFinding();
    }

    /**
     * Finds the closest lower resource by its conspicuousness.
     * 
     * @param array
     * @param resource
     * @return
     */
    public static int binarySearchTheClosestLower(Resource[] array, Resource resource) {
        if (array.length == 0)
            return 0;
        int index = binarySearchRecursion(array, resource, 0, array.length - 1, -1);
        if (resource.getConspicuousness() > array[0].getConspicuousness()
                || resource.getConspicuousness() < array[array.length - 1].getConspicuousness()
                || index + 1 > array.length - 1)
            return index;
        return index + 1;
    }

    private static int binarySearchRecursion(Resource[] array, Resource resource, int i, int j, int lastAverage) {
        int average = (i + j) / 2;
        if (average == lastAverage)
            return average;
        if (array[average].getConspicuousness() > resource.getConspicuousness())
            return binarySearchRecursion(array, resource, average + 1, j, average);
        else if (array[average].getConspicuousness() < resource.getConspicuousness())
            return binarySearchRecursion(array, resource, i, average - 1, average);
        return average;
    }

    /**
     * returns how easy is to watch through the space of the place.
     */
    public abstract int getTemperature();

    /**
     * Adds a creature to the creatres list.
     * Also sneds info about the creature, if
     * the craeture has been spoted by another.
     */
    public void addCreature(Creature creature) {
        creatures.add(creature);
        // TODO send info about the creature to other creatures, if it is visible to
        synchronized (creatures) {
            for (Creature c : creatures)
                if (c != creature)
                    c.addVisible(creature);
        }
    }

    /**
     * Says the rate of visibility through the space of the place.
     * 
     * @return
     */
    public int getVisibility() {
        return visibility;
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        List<BehavioursPossibleRequirement> list = new ArrayList<>();
        if (creature.surroundingPlaces == null)
            list.add(REQUIREMENT_IS_VISIBLE);
        else if (creature.surroundingPlaces.isPlaceVisible(this))
            list.add(REQUIREMENT_IS_VISIBLE);
        return list;
    }

    @Override
    public BehavioursPossibleIngredientID getBehavioursPossibleIngredientID() {
        return new BehavioursPossibleIngredientID(UnboundedPlace.class, getId());
    }

    public String getId() {
        return "UnboundedPlace$" + id;
    }

}
