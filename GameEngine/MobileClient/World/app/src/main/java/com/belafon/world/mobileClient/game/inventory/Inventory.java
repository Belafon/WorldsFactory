package com.belafon.world.mobileClient.game.inventory;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.belafon.world.mobileClient.game.Fragments;
import com.belafon.world.mobileClient.game.behaviours.Behaviours;
import com.belafon.world.mobileClient.game.behaviours.BehavioursFragment;
import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.visibles.Visibles;
import com.belafon.world.mobileClient.game.visibles.creatures.PlayableCreature;
import com.belafon.world.mobileClient.game.visibles.items.Clothes;
import com.belafon.world.mobileClient.game.visibles.items.Item;
import com.belafon.world.mobileClient.game.visibles.items.Item.Food;

public class Inventory {
    public final Map<Integer, Item> items = new Hashtable<>();
    public final Set<Clothes> clothes = new HashSet<>();
    public final PlayersGear gear = new PlayersGear();

    // TODO add method for adding item, that is already known
    /**
     * Adds an item to the players inventory.
     * 
     * @param id
     * @param type
     * @param name
     * @param weight
     * @param visibility
     * @param toss
     * @param args
     */
    public void addItem(Fragments fragments, int id, String type,
            String name, int weight, int visibility,
            int toss, String[] args, Behaviours behaviours) {
        Set<BehavioursRequirement> possibleBehaviours = new HashSet<>();

        if(args.length > 11){
             possibleBehaviours = Visibles.extractRequirementsFromArgs(behaviours, args[11]);
        }

        Item item = switch (type) {
            case "FoodTypeItem" -> new Food(id, name, weight, visibility, toss, Integer.parseInt(args[8]),
                    Integer.parseInt(args[9]), Integer.parseInt(args[10]), possibleBehaviours);
            case "CLothesTypeItem" -> new Clothes.Builder()
                    .setId(id)
                    .setName(name)
                    .setWeight(weight)
                    .setVisibility(visibility)
                    .setToss(toss)
                    .setDescription(args[8])
                    .setPartOfBody(gear.getPartOfBodyByName(args[9]))
                    .setDescription(args[10])
                    .build();
            case "QuestTypeItem" -> new QuestItem(id, name, weight, visibility, toss);
            default -> throw new IllegalArgumentException("Unexpected value: " + type);
        };
        items.put(id, item);
        if(item instanceof Clothes clothes)
            this.clothes.add(clothes);

        synchronized (PlayableCreature.allIngredients){
            PlayableCreature.allIngredients.add(item);
        }
        if(fragments != null)
            fragments.inventory.addItemToInventory(item);
    }
    
    public void removeItem(Fragments fragments, int id) {
        Item removeItem = items.get(id);
        synchronized (PlayableCreature.allIngredients){
            PlayableCreature.allIngredients.remove(removeItem);
        }
        items.remove(id);
        if(removeItem instanceof Clothes clothes)
            this.clothes.remove(clothes);

        BehavioursFragment.update(fragments.stats.behaviours);
        fragments.inventory.removeItemFromInventory(removeItem);
    }
}
