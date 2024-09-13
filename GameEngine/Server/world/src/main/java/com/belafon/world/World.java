package com.belafon.world;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.belafon.console.ConsolePrint;
import com.belafon.server.Server;
import com.belafon.world.calendar.Calendar;
import com.belafon.world.maps.Maps;
import com.belafon.world.maps.place.Place;
import com.belafon.world.story.Story;
import com.belafon.world.time.CalendaryLoop;
import com.belafon.world.time.DailyLoop;
import com.belafon.world.time.Time;
import com.belafon.world.visibles.VisibleIDs;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.NPC;
import com.belafon.world.visibles.creatures.Player;
import com.belafon.world.visibles.creatures.races.NPC.NPCMessages;
import com.belafon.world.visibles.items.ListOfAllItemTypes;
import com.belafon.world.visibles.items.ListOfAllItemTypes.NamesOfFoodItemTypes;
import com.belafon.world.visibles.items.ListOfAllItemTypes.NamesOfQuestItemTypes;
import com.belafon.world.visibles.items.itemsSpecialStats.SpecialFoodsProperties;
import com.belafon.world.visibles.items.types.Food;
import com.belafon.world.visibles.items.types.QuestItem;
import com.belafon.worldsfactory.api.EventGraphCondition;
import com.belafon.worldsfactory.api.StoryInitializer;
import com.belafon.worldsfactory.api.WorldsFactoryStory;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;

public class World implements Runnable {
    public volatile boolean isRunning = false;
    public final Server server;
    public final VisibleIDs visibleIds = new VisibleIDs();
    public final List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
    public final List<Creature> creatures = Collections.synchronizedList(new ArrayList<Creature>());
    public Time time;
    public final CalendaryLoop calendarsLoop = new CalendaryLoop(this);
    public final Calendar calendar = new Calendar(this);
    public Story story;
    public WorldsFactoryStory worldsFactoryStory;

    private static Scanner inputScanner = new Scanner(System.in);
    private static String storyFileName = null;

    public final DailyLoop dailyLoop = new DailyLoop(this);

    public final Maps maps;

    static {
        // load user input to select story
        var storyNames = new String[] { "derek_and_wolfs.py", "looking_for_sheep.py" };
        int storyNumber = -1;
        do {
            storyNumber = 1;
            ConsolePrint.gameInfo("""
                    SELECT STORY:
                        1. Derek and wolfs
                        2. Trade
                    """);

            try {
                var nextLine = inputScanner.nextLine();
                storyNumber = Integer.parseInt(nextLine) - 1;
            } catch (Exception e) {
                ConsolePrint.error_big("Wrong input. Please select number of story.");
            }

            if (storyNumber > storyNames.length - 1) {
                ConsolePrint.error_big("Too heigh input. Please select number of story.");
            }

            if (storyNumber < 0) {
                ConsolePrint.error_big("Too low input. Please select number of story.");
            }

        } while (storyNumber < 0 || storyNumber > storyNames.length - 1);
        storyFileName = storyNames[storyNumber];

        inputScanner.close();
    }

    {
        var resource = getClass().getResource("/story/" + storyFileName);
        if (resource == null) {
            ConsolePrint.error_big("World: cannot find resource /story/" + storyFileName);
            throw new RuntimeException("World: cannot find resource /story/" + storyFileName);
        } else {
            try {
                // Read the content of the file as a string
                String content = new String(resource.openStream().readAllBytes(), StandardCharsets.UTF_8);

                // Create a temporary file with the content
                File tempFile = File.createTempFile("story", ".py");
                tempFile.deleteOnExit(); // This file will be deleted when the JVM exits
                Files.write(tempFile.toPath(), content.getBytes(StandardCharsets.UTF_8));

                var path = tempFile.getAbsolutePath();
                var story = new StoryInitializer()
                        .withStoryName("MainStory")
                        .withPathToStoryData(path)
                        .withDebugMode(true)
                        .withEventGraphCondition(EventGraphCondition.MOVE_MAX_BY_ONE)
                        .withCheckingConditionsAfterEachSet(true)
                        .build();

                story.join().tryToMoveInEventGraph();

                maps = new Maps(this);
                this.worldsFactoryStory = story.join();
            } catch (IOException e) {
                ConsolePrint.error_big("World: Error reading story file: " + e.getMessage());
                throw new RuntimeException("World: Error reading story file", e);
            }
        }
    }

    public World(Server server) {
        this.server = server;
        server.games.add(this);
        time = new Time(Server.clocks, this);
    }

    @Override
    public void run() {
        ConsolePrint.gameInfo("World: new world starts...");

        synchronized (players) {
            for (Player player : players)
                player.client.writer.server.startGame();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isRunning = true;

        new Thread(calendarsLoop).start();

        synchronized (players) {
            for (Player player : players)
                player.setupAllRequirementsAndPossibleBehaviours();
        }

        loadStoryItems();
        loadStoryCreatures();

        // lets set up surrounding visible places for all creatures
        // so the surrounding places will be visible
        for (var creature : creatures) {
            creature.setupSurroundingVisiblePlacesWhenGameStarts();
        }

        synchronized (players) {
            for (Player player : players)
                player.gameStart();

            story = new Story(players.get(0), "story");
        }

        for (var creature : creatures) {
            creature.behaviourCondition.sendInfoAboutAllBehavioursWithNoRequirements();
        }

        if (time.getPartOfDay() != null) {
            for (var creature : creatures) {
                creature.writer.surrounding.setPartOfDay(time.getPartOfDay().name());
            }
        }

        // dailyLoop.start();
    }

    private void loadStoryCreatures() {
        var playersNames = worldsFactoryStory.getAllObjectNamesOfType("@class:Player");
        var creaturesNames = worldsFactoryStory.getAllObjectNamesOfType("@class:Creature");

        var mainPlayerName = "";
        for (var name : creaturesNames) {
            if (!playersNames.contains(name)) {
                var npc = new NPC.Builder()
                        .setWorld(this)
                        .setName(name)
                        .setLocation(maps.maps[0].places[0][0])
                        .setAppearence("")
                        .setSendMessage(NPCMessages.createMessageSender())
                        .setWeight(100)
                        .build();
                creatures.add(npc);
                WorldsFactoryStoryManager.bindObject(name, npc);
            } else {
                mainPlayerName = name;
            }
        }

        if (mainPlayerName.isEmpty()) {
            ConsolePrint.error_big("World: cannot find main player in the story.");
            throw new RuntimeException("World: cannot find main player in the story.");
        }

        // we can assume that there is only one player
        if (playersNames.size() >= 1) {
            Creature mainPlayer = players.get(0);
            mainPlayer.name = mainPlayerName;
            WorldsFactoryStoryManager.bindObject(mainPlayerName, mainPlayer);
        }
    }

    private void loadStoryItems() {
        var itemsNames = worldsFactoryStory.getAllObjectNamesOfType("@class:Apple");
        for (var name : itemsNames) {
            var item = new Food(this, 5, 100, new SpecialFoodsProperties[] {},
                    ListOfAllItemTypes.foodTypes.get(NamesOfFoodItemTypes.apple), maps.maps[0].places[0][0]);
            WorldsFactoryStoryManager.bindObject(name, item);
        }

        itemsNames = worldsFactoryStory.getAllObjectNamesOfType("@class:Coin");
        for (var name : itemsNames) {
            var item = new QuestItem(this, ListOfAllItemTypes.questTypes.get(NamesOfQuestItemTypes.coin),
                    maps.maps[0].places[0][0]);
            WorldsFactoryStoryManager.bindObject(name, item);
        }
    }

    private World() {
        this.server = null;
        time = new Time(Server.clocks, this);
    }

    /**
     * for testing purposes
     */
    public static World testingWorld() {
        return new World();
    }

    /**
     * Disconnects player from the game
     */
    public void disconnectPlayer() {
        synchronized (players) {
            for (Player player : players) {
                if (!player.isDisconnected())
                    return;
            }
        }

        endTheWorld();
    }

    /**
     * This ends the world. The calendar loop will stop.
     */
    private void endTheWorld() {
        worldsFactoryStory.end();
        isRunning = false;
        server.endTheWorld(this);
    }
}
