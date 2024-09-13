package com.belafon.server.messages;

public interface ServerMessages {
    public default void setNumberOfPlayersInQueue(int number) {
    }
    public default void startGame() {
    }
}
