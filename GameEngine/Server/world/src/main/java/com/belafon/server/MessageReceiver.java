package com.belafon.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.belafon.console.ConsolePrint;
import com.belafon.server.matchMakingSystems.MatchMakingSystem;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;

public class MessageReceiver implements Runnable {
    public Client client;
    private Socket clientSocket;
    boolean isInterupted = false;

    public MessageReceiver(Socket clientSocket, Server server, Client client) {
        this.client = client;
        this.clientSocket = clientSocket;
        client.setInitDisconnectedState(false);

        // starts to receave the messages from the client
        new Thread(this).start();
    }

    @Override
    public void run() {
        Thread.currentThread().setName(client.ipAddress + "");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Scanner scanner = new Scanner(in);) {
            while (clientSocket.isConnected()) {
                String message = null;
                try {
                    // lets listen the message from client
                    message = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    // if new line was not found, this listener will be canceled
                    if (client != null) {
                        ConsolePrint.error_small("Client has disconnected");
                        client.disconnect();
                        if (client.queueCondition == MatchMakingSystem.Condition.waitingInQueue)
                            client.getServer().matchMaking.removeClient(client); // remove from queue
                    }
                    break;
                }
                if (message != null)
                    decomposeString(message, clientSocket, client.getServer());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private void decomposeString(String value, Socket clientSocket, Server server) {
        String[] message = value.split(" ");
        ConsolePrint.new_message(value, client);

        switch (message[0]) {
            case "game" -> client.getServer().executor.execute(() -> {
                try {
                    if (client.player == null)
                        throw new IllegalArgumentException("Client has no player!");

                    getGameMessage(message, clientSocket, server);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    ConsolePrint.error_big("Recieved message is not correct! \n" + e.getMessage());
                }

            });
            case "server" -> getServerMessage(message, clientSocket, server);
        }
    }

    private void getServerMessage(String[] message, Socket clientSocket, Server server) {
        switch (message[1]) {
            case "findTheMatch" -> server.matchMaking.addClient(client);
            case "stopFindingTheMatch" -> server.matchMaking.removeClient(client);
            case "name" -> {
                if (message.length > 2) {
                    client.name = message[2];
                }
            }
            case "disconnect" -> {
                switch (client.queueCondition) {
                    case playing -> client.disconnect();
                    case waitingInQueue -> server.matchMaking.removeClient(client);
                    case idle -> {
                        server.allClients.remove(client.ipAddress);
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void getGameMessage(String[] message, Socket clientSocket, Server server) {
        switch (message[1]) {
            // behaviour executeBehaviour PickUpItem Item|8,Item|6,Item|10,
            case "behaviour" -> getBehaviourMessage(message);
            case "story" -> getStoryMessage(message);
        }
    }

    private void getStoryMessage(String[] message) {
        switch (message[2]) {
            case "choosenOption" -> {
                var option = new StringBuilder(message[3]);
                for (int i = 4; i < message.length; i++) {
                    option.append(" " + message[i]);
                }
                client.player.game.story.setLastChoosenOption(option.toString());
            }
        }
    }

    private void getBehaviourMessage(String[] message) {
        switch (message[2]) {
            case "executeBehaviour" -> {
                String behaviourName = message[3];
                BehaviourType behaviourType = BehaviourType.getBehaviourType(behaviourName);
                String[] ingredientsIds = message[4].split(",");
                List<BehavioursPossibleIngredient> ingredients = client.player.getIngredients(behaviourType,
                        ingredientsIds);

                // try to execute the behaviour
                if (client.player == null)
                    throw new IllegalArgumentException("Client has no player!");

                behaviourType.executeBehaviour(client.player, ingredients);

                // client.player.executeBehaviour(behaviourName, ingredients);
            }
        }
    }

}
