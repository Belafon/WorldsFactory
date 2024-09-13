package com.belafon.server.matchMakingSystems;

import com.belafon.server.Client;

public abstract class MatchMakingSystem {
    /**
     * Says total number of players waiting for a game.
     */
    public volatile int numberOfPlayers; // in queue

    /**
     * Adds client to the match making system.
     * Inform the system, that the client is 
     * waiting for next game.
     */
    public abstract void addClient(Client client);

    /**
     * Informs about that the clent changed his 
     * mind and doesnt want to play a game.
     * @param client
     */
    public abstract void removeClient(Client client);

    /**
     * Sets the {@link numberOfPlayers}.
     * @param numberOfPalyers
     */
    protected synchronized void setNumberOfPlayers(int numberOfPalyers) {
        this.numberOfPlayers = numberOfPalyers;
    }
 
    public enum Condition {
        idle,
        waitingInQueue,
        playing
    }
}
