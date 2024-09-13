package com.belafon.world.visibles.creatures;

import java.util.ArrayList;
import java.util.List;

import com.belafon.server.Client;
import com.belafon.world.World;
import com.belafon.world.maps.Map;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.BehavioursPossibleIngredientID;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;
import com.belafon.world.visibles.creatures.condition.knowledge.Knowledge;
import com.belafon.world.visibles.creatures.inventory.Inventory;
import com.belafon.world.visibles.creatures.inventory.PlayersGear;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources;
import com.belafon.world.visibles.resources.Resource;

public class Player extends Creature {
    public final Client client;
    public volatile boolean isReady = false;

    public Player(Client client, World game, String name, Place position, String appearence) {
        super(game, name, position, appearence, client.writer.sender, 100); // TODO weight
        this.client = client;
        // super(position, client.name, game, id);
        setAbilityCondition(0, 0, 300, 600, 100, 0, 100);
        sendInfoAboutMaps();
    }

    /**
     * Sends info about all maps in the game to the client.
     * It sends ids and sizes only.
     */
    private void sendInfoAboutMaps() {
        for (Map map : game.maps.maps) {
            writer.surrounding.setNewMap(map, map.sizeX, map.sizeY);
        }
    }

    @Override
    protected void setInventory(UnboundedPlace position) {
        inventory = new Inventory(new PlayersGear(), this, position);
    }

    public void setupAllRequirementsAndPossibleBehaviours() {
        var requirements = BehaviourType.getAllRequirmentes();
        synchronized (requirements) {
            for (var requirement : requirements) {
                client.writer.behaviour.setupPossibleReqirement(requirement);
            }
        }

        for (var behaviourType : BehaviourType.ALL_BEHAVIOUR_TYPES.values()) {
            client.writer.behaviour.setupBehaviour(behaviourType);
        }
    }

    /**
     * This is called when new game is starting.
     */
    public void gameStart() {
        for (var resorceType : ListOfAllTypesOfResources.typesOfResources.values()) {
            client.writer.surrounding.setNewResourceType(resorceType, this);
            synchronized (memory.typeResources) {
                memory.typeResources.add(resorceType);
            }
        }

        client.writer.surrounding.setInfoAboutSurrounding(surroundingPlaces);

        if(location instanceof Place place){
            client.writer.surrounding.setWeather(place.getWeather());
            client.writer.surrounding.setClouds(place);
        }

        // setBehaviour(new Move(game, this, game.maps.maps[0].places[7][7]));
    }

    public boolean isDisconnected() {
        return client.isDisconnected();
    }

    public List<BehavioursPossibleIngredient> getIngredients(BehaviourType behaviourType, String[] ingredientsIds) {
        List<BehavioursPossibleIngredient> ingredients = new ArrayList<>();
        for (var ingredientIdMessge : ingredientsIds) {
            var ingredientId = ingredientIdMessge.split("\\|");

            var ingredient = switch (ingredientId[0]) {
                case "Item" -> getIDFromMessage(ingredientId, Item.class);
                case "Creature" -> getIDFromMessage(ingredientId, Creature.class);
                case "Resource" -> getIDFromMessage(ingredientId, Resource.class);
                case "Knowledge" -> getIDFromMessage(ingredientId, Knowledge.class);
                case "UnboundedPlace" -> getIDFromMessage(ingredientId, UnboundedPlace.class);
                default -> throw new IllegalArgumentException("There is no ingredient type: " + ingredientId[0]);
            };
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    private BehavioursPossibleIngredient getIDFromMessage(String[] ingredientId,
            Class<? extends BehavioursPossibleIngredient> clazz) {
        var ingredientID = new BehavioursPossibleIngredientID(clazz, ingredientId[1]);
        var ingredient = behaviourCondition.allIngredients.get(ingredientID);
        if (ingredient == null) {
            throw new IllegalArgumentException("There is no ingredient with id: " + ingredientId[1]);
        }
        return ingredient;
    }
}
