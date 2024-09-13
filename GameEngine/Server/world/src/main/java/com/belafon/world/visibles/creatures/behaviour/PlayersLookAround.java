package com.belafon.world.visibles.creatures.behaviour;

import com.belafon.likeliness.Dice;
import com.belafon.server.messages.playerMessages.SurroundingPlayerMessages;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.Creature;

public class PlayersLookAround {
    public static final int radiusOfView = 3;
    // the middle is the position of the viewer, "Relative map"
    private int[][] placesChangedAltitudes = new int[radiusOfView * 2 + 1][radiusOfView * 2 + 1];
    public Place[][] visiblePlaces = new Place[radiusOfView * 2 + 1][radiusOfView * 2 + 1];

    private PlayersLookAround() {
    }

    /**
     * This function returns string for message for a player.
     * This message contains info about surrounding around concrete place
     * (viewers position).
     * 
     * The principle of the algorithm:
     * 
     * 1. In the first step, we look around the viewer’s position by a radius of 3.
     * This means that we are looking at a ring of places around the viewer’s
     * position, whose radius is 3. The viewer is always in the middle of the map.
     * The radius of the viewer’s view is always 3. This means that the player sees
     * the places that are in the circle with the radius of 3. The radius of the
     * view is the radius of the circle that is created by the viewer and the places
     * that he can see. The radius of the view is used to calculate the altitude of
     * the places that the player can’t see. The player can see places that are in
     * the view radius and that are higher than the altitude of the places that are
     * between the player and the place. The altitude is calculated by the altitude
     * of the place and the altitude of the place that is between the player and the
     * place. The altitude of the place that is between the player and the place is
     * calculated by the altitude of the place and the distance from the player.
     * 
     * @param game
     * @param player
     * @param viewersPosition
     * @return the map of places, which the player cen see
     */
    public static PlayersLookAround look(UnboundedPlace uviewersPosition) {
        // watching surrounding places in radius of 3
        PlayersLookAround lookAround = new PlayersLookAround();
        if (uviewersPosition instanceof Place viewersPosition) {

            // we will look to ring of places around the players place
            // and get know visiable places
            // it goes from the smallest ring around players place to bigger rings
            for (int numberOfCircle = 0; numberOfCircle <= radiusOfView; numberOfCircle++) {

                // we go through all of the places in the square
                for (int x = viewersPosition.positionX - numberOfCircle; x <= viewersPosition.positionX
                        + numberOfCircle; x++) {
                    for (int y = viewersPosition.positionY - numberOfCircle; y <= viewersPosition.positionY
                            + numberOfCircle; y++) {

                        // cordinates of current place in relative map, squared map (size according
                        // sizeOfView), which middle point is viewers place
                        int xInRelativeMap = x - viewersPosition.positionX + radiusOfView;
                        int yInRelativeMap = y - viewersPosition.positionY + radiusOfView;

                        // we have to pay attention to bounderies of map
                        if (x >= 0 && y >= 0 && x < viewersPosition.map.sizeX && y < viewersPosition.map.sizeY) {

                            // we need further coordinate
                            int biggerCoordinate = Math.abs(y - viewersPosition.positionY);
                            if (biggerCoordinate < Math.abs(x - viewersPosition.positionX))
                                biggerCoordinate = Math.abs(x - viewersPosition.positionX);

                            // we have to skip all places inside the ring
                            if (biggerCoordinate == numberOfCircle) {
                                int xCoordinateOfObstructedPlace = 0;
                                int yCoordinateOfObstructedPlace = 0;

                                // this will get coordinates of place, which could stand in the view of player,
                                // so
                                // the player can't see this place (according the altitude of places)
                                if (biggerCoordinate == Math.abs(y - viewersPosition.positionY)) {
                                    if (y - viewersPosition.positionY > 0)
                                        yCoordinateOfObstructedPlace = y - viewersPosition.positionY - 1;
                                    else if (y - viewersPosition.positionY < 0)
                                        yCoordinateOfObstructedPlace = y - viewersPosition.positionY + 1;
                                } else
                                    yCoordinateOfObstructedPlace = Math.abs(y - viewersPosition.positionY);

                                if (biggerCoordinate == Math.abs(x - viewersPosition.positionX)) {
                                    if (x - viewersPosition.positionX > 0)
                                        xCoordinateOfObstructedPlace = x - viewersPosition.positionX - 1;
                                    else if (x - viewersPosition.positionX < 0)
                                        xCoordinateOfObstructedPlace = x - viewersPosition.positionX + 1;
                                } else
                                    xCoordinateOfObstructedPlace = Math.abs(x - viewersPosition.positionX);

                                // the smallest ring the players allways see, there is nothing else between the
                                // player and the place
                                // +sizeOfView because, the player sees +sizeOfView circle places around him
                                // [3,3] is actual position of viewer
                                if (numberOfCircle == 1 || numberOfCircle == 0) {
                                    lookAround.visiblePlaces[xInRelativeMap][yInRelativeMap] = viewersPosition.map.places[x][y];
                                    lookAround.placesChangedAltitudes[xInRelativeMap][yInRelativeMap] = viewersPosition.map.places[x][y].altitude;
                                } else {
                                    if (viewersPosition.map.places[x][y].altitude > lookAround.placesChangedAltitudes[xCoordinateOfObstructedPlace
                                            + radiusOfView][yCoordinateOfObstructedPlace + radiusOfView]
                                            || viewersPosition.altitude > lookAround.placesChangedAltitudes[xCoordinateOfObstructedPlace
                                                    + radiusOfView][yCoordinateOfObstructedPlace + radiusOfView]) {
                                        // player sees
                                        lookAround.placesChangedAltitudes[xInRelativeMap][yInRelativeMap] = viewersPosition.map.places[x][y].altitude;
                                        lookAround.visiblePlaces[xInRelativeMap][yInRelativeMap] = viewersPosition.map.places[x][y];
                                    } else {
                                        // player doesn't see
                                        // relative altitude = altitude of higher place + altitude of higher place / 10
                                        // * distance from the player
                                        lookAround.placesChangedAltitudes[xInRelativeMap][yInRelativeMap] = lookAround.placesChangedAltitudes[xCoordinateOfObstructedPlace
                                                + radiusOfView][yCoordinateOfObstructedPlace + radiusOfView]
                                                + (((lookAround.placesChangedAltitudes[xCoordinateOfObstructedPlace
                                                        + radiusOfView][yCoordinateOfObstructedPlace + radiusOfView]
                                                        / 10) * numberOfCircle));
                                        lookAround.visiblePlaces[xInRelativeMap][yInRelativeMap] = null;
                                    }
                                }
                            }
                        } else {
                            // place out of the map ->
                            lookAround.visiblePlaces[xInRelativeMap][yInRelativeMap] = null;
                        }
                    }
                }
            }
        }
        return lookAround;
    }

    /**
     * @return Returns info message about surrounding places.
     *         It is used for sending info to player
     */
    public String makeMessage(Creature creature) {
        StringBuilder look = new StringBuilder("");
        int numberOfNullInRow = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (visiblePlaces[i][j] != null) {
                    if (numberOfNullInRow > 0) { // data compress dat, when there is more null places in row
                        look.append("x " + numberOfNullInRow + " ; ");
                        numberOfNullInRow = 0;
                    }
                    look.append(getView(visiblePlaces[i][j], creature));
                } else {
                    numberOfNullInRow++;
                }
            }
        }
        if (numberOfNullInRow > 0) // data compress dat, when there is more null places in row
            look.append("x " + numberOfNullInRow + " ; ");
        return look.toString();
    }

    // info about distant place
    private StringBuilder getView(Place place, Creature creature) {
        StringBuilder message = new StringBuilder(place.typeOfPlace.name.name());
        message.append("+" + place.getBehavioursPossibleIngredientID());
        message.append("+" + SurroundingPlayerMessages.getAllPossibleBehavioursReqruiementsAsMessage(place, creature));
        for (int i = 0; i < place.effects.size(); i++) {
            int visibility = place.effects.get(i).visibility;
            if (visibility <= 200 && new Dice(visibility).toss() > 20)
                message.append("+" + place.effects.get(i).name);
        }
        message.append(" ; ");
        return message;
    }

    /**
     * @return Returns viewers position.
     */
    public UnboundedPlace getViewersPosition() {
        return visiblePlaces[radiusOfView][radiusOfView];
    }

    public boolean isPlaceVisible(UnboundedPlace uplace) {
        if (uplace instanceof Place place) {
            if (getViewersPosition() instanceof Place viewersPlace) {
                int diffX = place.positionX - viewersPlace.positionX + radiusOfView;
                int diffY = place.positionY - viewersPlace.positionY + radiusOfView;
                if (areRelativeCordinatesInBounds(diffX, diffY))
                    return visiblePlaces[diffX][diffY] != null;
            }
        }
        return false;
    }

    private boolean areRelativeCordinatesInBounds(int x, int y) {
        if (x >= 0 && x <= radiusOfView * 2)
            if (y >= 0 && y <= radiusOfView * 2)
                return true;
        return false;
    }
}