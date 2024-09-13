package com.belafon.world.mobileClient.game.maps;

import androidx.fragment.app.Fragment;
import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.game.Fragments;
import com.belafon.world.mobileClient.game.Stats;
import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlacePanel;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlayersPlaceEffect;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.TypePlace;
import com.belafon.world.mobileClient.game.maps.weather.Weather;
import com.belafon.world.mobileClient.gameActivity.GameActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


/**
 * Handles server messages about maps.
 * Also holds information about all known maps
 */
public class PlayersMaps {
    private Hashtable<Integer, PlayersMap> maps = new Hashtable<>();
    private SurroundingMap surroundingMap;
    // private PlayersMap currentMap;
    // private PlaceFragment currentPosition;

    public final Weather weather = new Weather();

    public PlayersMaps() {
        surroundingMap = new SurroundingMap();
    }

    /**
     * It points that there exists a map in the
     * world with these sizes and with concrete id.
     * 
     * @param key is id of the map
     * @param map
     */
    public void addMap(int key, PlayersMap map) {
        maps.put(key, map);
        for (int x = 0; x < map.sizeX; x++) {
            for (int y = 0; y < map.sizeX; y++) {
                map.places[x][y] = PlacePanel.getUnknownPlace(x, y);
            }
        }
    }

    /**
     * It points that there exists a map in the
     * world with these sizes and with concrete id.
     * 
     * @param args
     * @throws NumberFormatException
     * @throws IndexOutOfBoundsException
     */
    public void addMap(String[] args) throws NumberFormatException, IndexOutOfBoundsException {
        int id = Integer.parseInt(args[2]);
        int sizeX = Integer.parseInt(args[3]);
        int sizeY = Integer.parseInt(args[4]);
        PlayersMap map = new PlayersMap(id, sizeX, sizeY);
        addMap(id, map);
    }

    public void removeMap(int key) {
        maps.remove(key);
    }

    public PlayersMap getMap(int key) {
        return maps.get(key);
    }

    /**
     * This method updates surrounding places
     * with the message from the server.
     * It updates all the places.
     * 
     * @param args
     * @param stats
     */
    public synchronized void lookAroundSurroundingPlaces(String[] args, Stats stats, Fragments fragments) {
        int currentPlace = 0;
        for (int currentArg = 2; currentArg < args.length; currentArg++) {
            switch (args[currentArg]) {
                case "x" -> {
                    currentPlace = setSurroundingAtIdsNull(currentPlace, args[++currentArg]);
                    currentArg++;
                }
                default -> currentArg = setSurroundingAtId(currentPlace++, currentArg, args, stats);
            }
        }
        if(fragments != null && fragments.surroundingPlaces != null){
            AbstractActivity.getActualActivity().runOnUiThread(() -> {
                fragments.surroundingPlaces.update();
            });
        }

    }

    private int setSurroundingAtId(int currentPlace, int currentArg, String[] args, Stats stats) {
        String[] typePlacesInfo = args[currentArg++].split("\\+");

        String typePlacesName = typePlacesInfo[0];
        String id = typePlacesInfo[1];


        // lets handle requirements 
        String requirementsMessage = null;
        if (typePlacesInfo.length > 2)
            requirementsMessage = typePlacesInfo[2];

        Set<BehavioursRequirement> requirements = null;
        if (requirementsMessage != null)
            requirements = getRequirementsFromMessage(requirementsMessage, stats);
        else
            requirements = new HashSet<>();


        // lets handle effects
        String effectsMessage = null;
        if (typePlacesInfo.length > 3)
            effectsMessage = typePlacesInfo[3];

        if (!TypePlace.allTypes.containsKey(typePlacesName))
            throw new IllegalArgumentException("unsupported type place name: " + typePlacesName);

        int xInSurrounding = currentPlace / SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS;
        int yInSurrounding = currentPlace % SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS;

        List<PlayersPlaceEffect> effects = null;
        if (effectsMessage != null)
            effects = getPlaceEffectsFromMessage(effectsMessage,
                    surroundingMap.getPlaceFragment(xInSurrounding, yInSurrounding));
        else
            effects = new ArrayList<>();


        // lets update the fragment that shows the place in the surrounding
        PlacePanel place = new PlacePanel(id, TypePlace.allTypes.get(typePlacesName), requirements, effects, xInSurrounding, yInSurrounding);
        surroundingMap.updatePlace(xInSurrounding, yInSurrounding, place);

        // lets add the place into ingredients
        stats.visibles.addNewPlace(place);

        return currentArg;
    }

    private Set<BehavioursRequirement> getRequirementsFromMessage(String requirementsMessage, Stats stats) {
        Set<BehavioursRequirement> requirements = new HashSet<>();
        for (String requirement : requirementsMessage.split("\\,")) {
            requirements.add(stats.behaviours.allRequirements.get(requirement));
        }
        return requirements;
    }

    private List<PlayersPlaceEffect> getPlaceEffectsFromMessage(String effectsMessage, PlacePanel place) {
        List<PlayersPlaceEffect> effects = new ArrayList<>();
        for (String effect : effectsMessage.split("\\,")) {
            effects.add(PlayersPlaceEffect.allPlaceEffects.get(effect));
        }
        return effects;
    }

    private int setSurroundingAtIdsNull(int currentPlace, String count) throws NumberFormatException {
        int numberOfStackedNullPlaces = Integer.parseInt(count);
        for (int i = 0; i < numberOfStackedNullPlaces; i++) {
            int xInSurrounding = currentPlace / SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS;
            int yInSurrounding = currentPlace % SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS;
            surroundingMap.setPlaceUnknown(xInSurrounding, yInSurrounding);
            currentPlace++;
        }
        return currentPlace;
    }

    /**
     * @return fragment that shows all surrounding
     *         places around the creature in the radius
     *         of 3 places.
     *         The places, that are not visible for the
     *         player are displayed with grey color and
     *         with name as umknown place.
     */
    public SurroundingPlacesFragment getSurroundingPlacesFragment(Fragment lastFragment, int fragmentContainerId) {
        return new SurroundingPlacesFragment(surroundingMap, fragmentContainerId, lastFragment);
    }

    public void removePlaceInSight(String[] args, Stats stats, Fragments fragments) {
        if(fragments == null)
            return;

        String id = args[2];
        int x = Integer.parseInt(args[3]);
        int y = Integer.parseInt(args[4]);
        Place place = surroundingMap.getPlaceFragment(x, y);
        stats.visibles.removePlace(place, fragments);
        surroundingMap.setPlaceUnknown(x, y);
    }

    public void setCurrentPositionInfo(String[] args, Stats stats, Fragments fragments) {
        String id = args[2];
        int temperature = Integer.parseInt(args[3]);
        String typeOfPlaceName = args[4];
        String picture = args[5];
        String music = args[6];
        boolean isMapPlace = !args[7].equals("null");
        int mapId = -1;
        if(isMapPlace)
            mapId = Integer.parseInt(args[7]);

        Place place = surroundingMap.getPlaceFragment(SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS / 2,
                SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS / 2);

        if(AbstractActivity.getActualActivity() instanceof GameActivity gameActivity) {
            AbstractActivity.getActualActivity().runOnUiThread(() ->
                    gameActivity.setPlaceBackground(picture)
            );
        }
    }

}