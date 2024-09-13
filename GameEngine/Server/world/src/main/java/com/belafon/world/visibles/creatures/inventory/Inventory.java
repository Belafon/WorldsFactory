package com.belafon.world.visibles.creatures.inventory;

import com.belafon.console.ConsolePrint;
import com.belafon.world.maps.Maps;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.items.ListOfAllItemTypes;
import com.belafon.world.visibles.items.typeItem.ToolTypeItem;
import com.belafon.world.visibles.items.typeItem.tools.ToolsUtilization;
import com.belafon.world.visibles.items.types.*;
import com.belafon.world.visibles.items.types.SpaceItem.BuilderSpaceItem;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;

public class Inventory {
    public volatile int totalWeight;
    public volatile int currentWeight;

    private final SpaceItem backSpace;

    public final Gear gear;
    public final Creature creature;

    public Inventory(Gear gear, Creature creature, UnboundedPlace position) {
        this.gear = gear;
        this.creature = creature;
        backSpace = new BuilderSpaceItem(creature.game,
                ListOfAllItemTypes.spaceItems.get(ListOfAllItemTypes.NamesOfSpaceItemTypes.back_space),
                position)
                .build(creature.game);
    }

    public volatile Tool leftHand;
    public volatile Tool workHand;

    // add an item into inventory
    public synchronized void addItem(Item item) {
        synchronized (creature.behaviourCondition.allIngredients) {
            creature.behaviourCondition.allIngredients.put(item.getBehavioursPossibleIngredientID(), item);
        }

        if (backSpace == null) {
            // TODO call unsupported operation
            ConsolePrint.error("Try to add item to inventory without bag");
        }

        // backSpace.getSpace().addItem(item);
        item.setLocationAndUpdateWatchers(backSpace.getSpace());

        // check feasibility of behaviours
        creature.behaviourCondition.updateFeasibleBehaviours(item);

        if (item instanceof Tool tool) {
            if (tool.type instanceof ToolTypeItem toolType) {
                for (ToolsUtilization toolsUtilization : toolType.toolsUtilizations) {
                    creature.behaviourCondition
                            .addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(toolsUtilization, item);
                }
            }
        }

        // lets send info about it
        creature.writer.inventory.setAddItem(item, creature);
    }

    /**
     * Removes an item from inventory and
     * updates feasible behaviours.
     * 
     * The item is placed into EMPTY_PLACE.
     * 
     * @param item
     */
    public synchronized void removeItem(Item item) {
        synchronized (creature.behaviourCondition.allIngredients) {
            creature.behaviourCondition.allIngredients.remove(item.getBehavioursPossibleIngredientID());
        }

        item.setLocationAndUpdateWatchers(Maps.EMPTY_PLACE);

        // check feasibility of behaviours
        item.getBehavioursPossibleRequirementType(creature).stream().forEach((requirement) -> {
            creature.behaviourCondition.removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(requirement, item);
        });

        // lets send info about it
        creature.writer.inventory.setRemoveItem(item);

        // remove item from story data
        var index = 0;
        for (var itemInStory : creature.storyInventory) {
            if (itemInStory == item) {
                creature.storyInventory.set(index, null);
                WorldsFactoryStoryManager.setProperty("inventory[" + index + "]", null, creature.name);
                break;
            }
            index++;
        }
    }

    public boolean ownsItem(Item item) {
        synchronized (backSpace.getSpace().items) {
            if (backSpace.getSpace().items.contains(item))
                return true;
        }
        return false;
    }

    public UnboundedPlace getPlace() {
        return backSpace.getLocation();
    }

    @FunctionalInterface
    public static interface ActionItem {
        public void doJob(Item item);
    }

    public void getAllItems(ActionItem action) {
        synchronized (backSpace.getSpace().items) {
            for (Item item : backSpace.getSpace().items) {
                action.doJob(item);
            }
        }
    }
}
