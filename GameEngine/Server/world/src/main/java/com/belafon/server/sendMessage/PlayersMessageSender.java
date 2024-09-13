package com.belafon.server.sendMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

import com.belafon.console.ConsolePrint;
import com.belafon.server.Client;
import com.belafon.server.messages.playerMessages.BehavioursPlayersMessages;
import com.belafon.server.messages.playerMessages.ConditionPlayerMessages;
import com.belafon.server.messages.playerMessages.CreatureVisiblePlayerMessages;
import com.belafon.server.messages.playerMessages.InventoryPlayerMessages;
import com.belafon.server.messages.playerMessages.ItemVisiblePlayerMessages;
import com.belafon.server.messages.playerMessages.ResourceVisiblePlayerMessages;
import com.belafon.server.messages.playerMessages.ServerPlayerMessages;
import com.belafon.server.messages.playerMessages.StoryPlayerMessages;
import com.belafon.server.messages.playerMessages.SurroundingPlayerMessages;

/**
 * Devides messages from server to concrete client
 * to meaningful groups.
 */
public class PlayersMessageSender {
    public PrintWriter output;
    public final Client client;
    public volatile Socket clientSocket;
    public final ServerPlayerMessages server = new ServerPlayerMessages(this);
    public final SurroundingPlayerMessages surrounding = new SurroundingPlayerMessages(this);
    public final CreatureVisiblePlayerMessages creatureVisibles = new CreatureVisiblePlayerMessages(this);
    public final ItemVisiblePlayerMessages itemVisibles = new ItemVisiblePlayerMessages(this);
    public final ResourceVisiblePlayerMessages resoruceVisibles = new ResourceVisiblePlayerMessages(this);
    public final ConditionPlayerMessages condition = new ConditionPlayerMessages(this);
    public final InventoryPlayerMessages inventory = new InventoryPlayerMessages(this);
    public final BehavioursPlayersMessages behaviour = new BehavioursPlayersMessages(this);
    public final StoryPlayerMessages story = new StoryPlayerMessages(this);
    public final MessageSender sender;

    public PlayersMessageSender(Socket clientSocket, Client client) {
        sender = MessageSender.getBuilder()
                .setServer(server)
                .setBehavioursMessages(behaviour)
                .setCondition(condition)
                .setInventory(inventory)
                .setSurrounding(surrounding)
                .setItemVisible(itemVisibles)
                .setCreatureVisible(creatureVisibles)
                .setResourceVisible(resoruceVisibles)
                .setStory(story)
                .build();
        this.clientSocket = clientSocket;
        this.client = client;
        try {
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
        } catch (IOException e) {
            ConsolePrint.error("SendMessage: Message ERROR in players connection :: " + client.name + " :: ->\n " + e);
            e.printStackTrace();
        }
    }

    /**
     * Sends message to the client through the server.
     */
    public synchronized void sendLetter(String string, TypeMessage type) {
        if (printCyclesStatsToConsole || type != TypeMessage.dailyLoop)
            if (printCreatureStatsToConsole || type != TypeMessage.actualStats)
                if (printCreatureAbilityStatsToConsole || type != TypeMessage.abilityStats)
                    ConsolePrint.message_to_player(string, client.name);
        output.println(string); // blank line between headers and content, very important !
        output.flush(); // flush character output stream buffer

    }

    public enum TypeMessage {
        other,
        dailyLoop,
        actualStats, abilityStats
    }

    private static boolean printCyclesStatsToConsole = false;
    private static boolean printCreatureStatsToConsole = false;
    private static boolean printCreatureAbilityStatsToConsole = false;

    private static final String FILENAME = "config.properties";

    public synchronized static boolean isPrintCyclesStatsToConsole() {
        return printCyclesStatsToConsole;
    }

    public synchronized static void setPrintCyclesStatsToConsole(boolean printCyclesStatsToConsole) {
        PlayersMessageSender.printCyclesStatsToConsole = printCyclesStatsToConsole;
        saveConfig();
    }

    public synchronized static void setPrintCreatureStatsToConsole(boolean printCreatureStats) {
        PlayersMessageSender.printCreatureStatsToConsole = printCreatureStats;
        saveConfig();
    }

    public synchronized static void setPrintCreaturesAbuilityStatsToConsole(boolean printCreatureStats) {
        PlayersMessageSender.printCreatureAbilityStatsToConsole = printCreatureStats;
        saveConfig();
    }

    static {
        try {
            loadConfig();
        } catch (IOException e) {
            // file doesn't exist, use default values
        }
    }

    private static void loadConfig() throws IOException {
        File file = new File(FILENAME);
        if (!file.exists()) {
            return;
        }
        try (FileInputStream input = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(input);
            printCyclesStatsToConsole = Boolean.parseBoolean(props.getProperty("printCyclesStatsToConsole"));
            printCreatureStatsToConsole = Boolean.parseBoolean(props.getProperty("printCreatureStatsToConsole"));
            printCreatureStatsToConsole = Boolean.parseBoolean(props.getProperty("printCreatureAbilityStatsToConsole"));
        }
    }

    private static void saveConfig() {
        try (FileOutputStream output = new FileOutputStream(FILENAME)) {
            Properties props = new Properties();
            props.setProperty("printCyclesStatsToConsole", Boolean.toString(printCyclesStatsToConsole));
            props.setProperty("printCreatureStatsToConsole", Boolean.toString(printCreatureStatsToConsole));
            props.setProperty("printCreatureAbilityStatsToConsole",
                    Boolean.toString(printCreatureAbilityStatsToConsole));
            props.store(output, "Game configuration");
        } catch (IOException e) {
            // failed to save config
            e.printStackTrace();
        }
    }
}