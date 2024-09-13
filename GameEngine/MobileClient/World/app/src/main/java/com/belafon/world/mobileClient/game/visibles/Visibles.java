package com.belafon.world.mobileClient.game.visibles;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.belafon.world.mobileClient.game.behaviours.BehavioursFragment;
import com.belafon.world.mobileClient.game.visibles.creatures.Creature;
import com.belafon.world.mobileClient.game.visibles.creatures.PlayableCreature;
import com.belafon.world.mobileClient.game.visibles.items.Item;
import com.belafon.world.mobileClient.game.visibles.items.Item.Food;
import com.belafon.world.mobileClient.game.visibles.resources.Resource;
import com.belafon.world.mobileClient.game.visibles.resources.ResourceTypes;
import com.belafon.world.mobileClient.game.Fragments;
import com.belafon.world.mobileClient.game.behaviours.Behaviours;
import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.maps.Place;

/**
 * Holds all information about visibles, views nothing.
 * Catches messages about visibles from the server and resolves them.
 */
public class Visibles {
    public final Hashtable<Integer, Item> items = new Hashtable<>();
    public final Hashtable<Integer, Creature> creatures = new Hashtable<>();
    public final Hashtable<Integer, Resource> resources = new Hashtable<>();
    private final Hashtable<String, TypeOfResource> typesOfresources = new Hashtable<>();
    private final Set<Place> places = new HashSet<>();

    /**
     * It resolves the message from server
     * and tries to add item into the items list.
     * It means that the player now know about this item.
     * 
     * @param args
     * @param fragments
     * @param behaviours
     */
    public void addItem(String[] args, Fragments fragments, Behaviours behaviours) {
        int id = Integer.parseInt(args[2]);
        String className = args[3];
        String itemName = args[4];
        int regularWeight = Integer.parseInt(args[5]);
        int visibility = Integer.parseInt(args[6]);
        int toss = Integer.parseInt(args[7]);

        Item item = null;
        switch (className) {
            case "FoodTypeItem" -> {
                int freshness = Integer.parseInt(args[8]);
                int filling = Integer.parseInt(args[9]);
                int warm = Integer.parseInt(args[10]);
                Set<BehavioursRequirement> requirements = extractRequirementsFromArgs(behaviours, args[11]);

                item = new Food(id, itemName, regularWeight, visibility, toss, freshness, filling, warm,
                        requirements);

                if(fragments.visibles.items != null)
                    fragments.visibles.items.addVisiblesTitle(item);

                synchronized (PlayableCreature.allIngredients){
                    PlayableCreature.allIngredients.add(item);
                }
            }
            default ->
                throw new IllegalArgumentException("Invalid class name for item type: " + className);
        }
        synchronized (items) {
            items.put(id, item);
        }
    }

    /**
     * 
     * It resolves the message from server
     * and tryes to add creature into the creature list.
     * It means that the player now know about this creature.
     * 
     * @param args
     * @param fragments
     */
    public void addCreature(String[] args, Fragments fragments, Behaviours behaviours) {
        int id = Integer.parseInt(args[2]);
        String name = args[3];
        String appearance = args[4].replaceAll("_", " ");
        switch (args[5]) {
            case "place" -> {
                int mapId = Integer.parseInt(args[6]);
                int positionX = Integer.parseInt(args[7]);
                int positionY = Integer.parseInt(args[8]);
            }
            case "itemPlace" -> {
            }
        }
        // TODO arg[9] ... current behaviour
        Set<BehavioursRequirement> requirements = extractRequirementsFromArgs(behaviours, args[10]);

        Creature creature = new Creature(name, id, appearance, requirements);


        if(fragments != null
                && fragments.visibles.creatures != null)
            fragments.visibles.creatures.addVisiblesTitle(creature);
        synchronized (creatures) {
            creatures.put(id, creature);
        }

        
        synchronized (PlayableCreature.allIngredients) {
            PlayableCreature.allIngredients.add(creature);
        }

    }

    public static Set<BehavioursRequirement> extractRequirementsFromArgs(Behaviours behaviours,
            String possibleRequirements) {
        Set<BehavioursRequirement> requirements = new HashSet<>();
        for (String possibleRequirementsName : possibleRequirements.split(",")) {
            var possibleRequirement = behaviours.allRequirements.get(possibleRequirementsName);
            if (possibleRequirement == null) {
                // add new requirement into the list of all requirements
                possibleRequirement = new BehavioursRequirement(possibleRequirementsName, possibleRequirementsName);
                behaviours.allRequirements.put(possibleRequirementsName, possibleRequirement);
                requirements.add(possibleRequirement);

                throw new Error("Extracting requirements from args and unrecognized behaviour found: "
                        + possibleRequirementsName);
            } else
                requirements.add(possibleRequirement);
        }
        return requirements;
    }

    /**
     * 
     * It resolves the message from server
     * and tryes to add resource into the resource list.
     * It means that the player now know about this resource.
     * 
     * @param args
     * @param fragments
     * @param behaviours
     */
    public void addResource(String[] args, Fragments fragments, Behaviours behaviours) {
        int id = Integer.parseInt(args[2]);
        String name = args[3];
        var type = ResourceTypes.resorceTypes.get(name);
        int mass = Integer.parseInt(args[4]);
        Set<BehavioursRequirement> requirements = extractRequirementsFromArgs(behaviours, args[5]);

        Resource resource = new Resource(id, type, mass, requirements);

        if(fragments.visibles.resources != null)
            fragments.visibles.resources.addVisiblesTitle(resource);

        synchronized (resources) {
            resources.put(id, resource);
        }

        synchronized (PlayableCreature.allIngredients) {
            PlayableCreature.allIngredients.add(resource);
        }
    }

    /**
     * Tryes to remove an item, so the player
     * should not know about the item anymore.
     * 
     * @param args
     * @param fragments
     */
    public void removeItem(String[] args, Fragments fragments) {
        int id = Integer.parseInt(args[2]);
        if (items.containsKey(id)) {
            var item = items.get(id);

            items.remove(item.getNumId());

            fragments.visibles.items.removeVisible(item);

            synchronized (PlayableCreature.allIngredients){
                PlayableCreature.allIngredients.remove(item);
            }

            BehavioursFragment.update(fragments.stats.behaviours);
        }
    }

    /**
     * Tries to remove a creature, so the player
     * should not know about the creature anymore.
     * 
     * @param args
     * @param fragments
     */
    public void removeCreature(String[] args, Fragments fragments) {
        int id = Integer.parseInt(args[2]);
        if (fragments != null && creatures.containsKey(id)) {
            fragments.visibles.creatures.removeVisible(creatures.get(id));
        }
        synchronized (creatures){
            creatures.remove(id);
        }
    }

    /**
     * Tries to remove an resource, so the player
     * should not know about the resource anymore.
     * 
     * @param args
     * @param fragments
     */
    public void removeResource(String[] args, Fragments fragments) {
        int id = Integer.parseInt(args[2]);
        if (resources.containsKey(id)) {
            fragments.visibles.resources.removeVisible(resources.get(id));
        }
        synchronized (resources) {
            resources.remove(id);
        }
    }

    public void addNewResourceType(String[] args, Behaviours behaviours) {
        String idName = args[2];
        String name = args[3];
        String requirementsMessage = args[4];
        Set<BehavioursRequirement> requirements = extractRequirementsFromArgs(behaviours, requirementsMessage);
        synchronized (typesOfresources) {
            typesOfresources.put(idName, new TypeOfResource(name, idName, requirements));
        }

        synchronized (PlayableCreature.allIngredients) {
            PlayableCreature.allIngredients.add(typesOfresources.get(idName));
        }
    }

    public void addNewPlace(Place place) {
        synchronized (PlayableCreature.allIngredients) {
            PlayableCreature.allIngredients.add(place);
        }

        synchronized (places) {
            places.add(place);
        }
    }

    public void removePlace(Place place, Fragments fragments) {
        synchronized (places) {
            places.remove(place);
        }

        fragments.surroundingPlaces.updateRemovePlace(place);

        synchronized (PlayableCreature.allIngredients){
            PlayableCreature.allIngredients.remove(place);
        }
    }
}

