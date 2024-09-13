package com.belafon.server.messages.playerMessages;

import com.belafon.server.messages.SurroundingMessages;
import com.belafon.server.sendMessage.PlayersMessageSender;
import com.belafon.world.maps.Map;
import com.belafon.world.maps.place.ItemPlace;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.maps.weather.Weather;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.time.DailyLoop.NamePartOfDay;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.PlayersLookAround;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;
import com.belafon.world.visibles.items.Item;
import com.belafon.world.visibles.resources.Resource;
import com.belafon.world.visibles.resources.TypeOfResource;

public class SurroundingPlayerMessages implements SurroundingMessages {
    public final PlayersMessageSender sendMessage;

    public SurroundingPlayerMessages(PlayersMessageSender sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Override
    public void setPartOfDay(NamePartOfDay partOfDay) {
        sendMessage.sendLetter("map partOfDay " + partOfDay.name(), PlayersMessageSender.TypeMessage.dailyLoop);
    }

    @Override
    public void setWeather(Weather weather) {
        sendMessage.sendLetter("map weather " + weather.getWeather(), PlayersMessageSender.TypeMessage.dailyLoop);
    }

    @Override
    public void setClouds(Place place) {
        sendMessage.sendLetter("map clouds " + place.map.sky.getWeather(place).getClouds(),
                PlayersMessageSender.TypeMessage.dailyLoop);
    }

    @Override
    public void setInfoAboutSurrounding(PlayersLookAround surrounding) {
        String message = "map look_arround " + surrounding.makeMessage(sendMessage.client.player);
        sendMessage.client.writer.sendLetter(message, PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void setResources(UnboundedPlace position) {
        StringBuilder message = new StringBuilder("surrounding resources");
        for (Resource resource : position.resourcesSorted)
            if (resource.durationOfFinding == 0)
                message.append(" " + resource.type.name);
            else
                break;
        sendMessage.client.writer.sendLetter(message.toString(), PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void setResource(Resource resource) {
        sendMessage.client.writer.sendLetter("surrounding resource " + resource.id + " " + resource.type.name,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void setResourceNotFound(Resource resource) {
        sendMessage.client.writer.sendLetter("surrounding resourceNotFound " + resource.type.name,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void setItems(UnboundedPlace position) {
        // TODO fill setItems method
    }

     @Override
    public void setTypeOfPlaceInfoDrawableSound(UnboundedPlace position) {
        sendMessage.sendLetter("soundDrawable " + position.music + " " + position.picture,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void setNewMap(Map map, int sizeX, int sizeY) {
        sendMessage.sendLetter("map new_map " + map.id + " " + sizeX + " " + sizeY,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void addVisibleInSight(Visible visible, Creature watcher) {
        StringBuilder behaviours = getAllPossibleBehavioursReqruiementsAsMessage(visible, watcher);
        StringBuilder message = new StringBuilder("surrounding");
        if (visible instanceof Item item) {
            message.append(" add_item_in_sight "
                    + getVisibleItemPropertiesMessage(item, watcher))
                    .append(" " + behaviours);

        } else if (visible instanceof Creature creature) {
            message.append(" add_creature_in_sight "
                    + getVisibleCreaturePropertiesMessage(creature))
                    .append(" " + behaviours);

        } else if (visible instanceof Resource resource) {
            message.append(" add_resource_in_sight "
                    + getVisibleResourcePropertiesMessage(resource))
                    .append(" " + behaviours);
        }

        sendMessage.client.writer.sendLetter(message.toString(), PlayersMessageSender.TypeMessage.other);
    }

    public static StringBuilder getAllPossibleBehavioursReqruiementsAsMessage(BehavioursPossibleIngredient ingredient,
            Creature creature) {
        StringBuilder message = new StringBuilder("");
        boolean firstItem = true;
        for (var requirement : ingredient.getBehavioursPossibleRequirementType(creature)) {
            if (firstItem) {

                message.append(requirement.idName);
                firstItem = false;
            } else {
                message.append("," + requirement.idName);
            }
        }
        return message;
    }

    private StringBuilder getVisibleItemPropertiesMessage(Item item, Creature creature) {
        return new StringBuilder(item.id + " " + item.type.getClass().getSimpleName() + " "
                + item.type.name + " " + item.type.regularWeight + " "
                + item.type.visibility + " " + item.type.toss + " "
                + InventoryPlayerMessages.getItemPropertiesMessage(item));
    }

    private StringBuilder getVisibleResourcePropertiesMessage(Resource resource) {
        return new StringBuilder(resource.id + " " + resource.type.name + " " + resource.getAmount());
    }

    private StringBuilder getVisibleCreaturePropertiesMessage(Creature creature) {
        StringBuilder creaturesBehaviour;
        if (creature.currentBehaviour == null)
            creaturesBehaviour = new StringBuilder("idle");
        else
            creaturesBehaviour = new StringBuilder(creature.currentBehaviour.getType().behaviourClass.getSimpleName());

        return new StringBuilder(creature.id + " " + creature.name + " "
                + creature.appearence.replace(" ", "_")
                + " " + getMessageAboutPlace(creature.getLocation()) + " " + creaturesBehaviour);
    }

    private StringBuilder getMessageAboutPlace(UnboundedPlace place) {
        StringBuilder message = new StringBuilder();
        if (place instanceof Place p) {
            message.append("place " + p.map.id + " " + p.positionX + " " + p.positionX);
        } else if (place instanceof ItemPlace p) {
            message.append("itemPlace " + p.getItem().id);
        }
        return message;
    }

    @Override
    public void removeVisibleFromSight(Visible value) {
        StringBuilder message = new StringBuilder("surrounding");
        if (value instanceof Item item) {
            message.append(" remove_item_in_sight "
                    + item.id);
        } else if (value instanceof Creature creature) {
            message.append(" remove_creature_in_sight "
                    + creature.id);

        } else if (value instanceof Resource resource) {
            message.append(" remove_resource_in_sight "
                    + resource.id);
        }
        sendMessage.client.writer.sendLetter(message.toString(), PlayersMessageSender.TypeMessage.other);
    }

    public void setNewResourceType(TypeOfResource resorceType, Creature creature) {
        // converts resorceType.name this example treeOak to tree_oak
        String name = resorceType.name.name().replaceAll("([A-Z])", "_$1").toLowerCase();
        StringBuilder requirements = getAllPossibleBehavioursReqruiementsAsMessage(resorceType, creature);
        sendMessage.sendLetter("surrounding new_resource_type " + resorceType.name + " " + name + " " + requirements,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void removePlaceFromSight(Place lastPlace, int xInRelativeMap, int yInRelativeMap) {
        sendMessage.sendLetter("map remove_place_in_sight " + lastPlace.getId() + " "
                + xInRelativeMap + " " + yInRelativeMap,
                PlayersMessageSender.TypeMessage.other);
    }

    @Override
    public void setCurrentPositionInfo(UnboundedPlace place) {
        sendMessage.sendLetter("player currentPositionInfo " + place.id + " " 
                + place.getTemperature() + " " 
                + place.typeOfPlace.name + " "  
                + place.picture + " " 
                + place.music + " "
                + (place instanceof Place mapPlace 
                    ? mapPlace.map.id : "null"), PlayersMessageSender.TypeMessage.other);

    }
}
