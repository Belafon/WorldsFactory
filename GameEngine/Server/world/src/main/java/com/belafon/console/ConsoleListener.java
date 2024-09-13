package com.belafon.console;

import com.belafon.server.Server;
import com.belafon.server.sendMessage.PlayersMessageSender;
import com.belafon.world.maps.weather.Weather;
import com.belafon.world.time.DailyLoop;
import com.belafon.world.visibles.creatures.Player;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources.NamesOfTypesOfResources;
import java.util.Scanner;

/**
 * Handles console inputs from server provider.
 * It is used primarry for debuging.
 * 
 * Messages with ~log are used to log some info about the
 * first created game.
 * 
 * Other messages are automatically send to all clients
 * in the game.
 */
public class ConsoleListener implements Runnable {
    private Server server;

    public ConsoleListener(Server server) {
        this.server = server;
        new Thread(this).start();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("ConsoleListener");
        ConsolePrint.serverInfo("Listener is waiting for text in console input...");
        Scanner sc = new Scanner(System.in);
        while (server.isServerRunning) {
            String message = "";
            try {
                while (sc.hasNextLine()) {
                    message = sc.nextLine().toString();
                    if (!message.equals(""))
                        if (message.split(" ")[0].equals("log")) {
                            try {
                                show_log(message.split(" "));
                            } catch (Exception e) {
                                ConsolePrint.error_small("Wrong log input!");
                            }
                        } else {
                            for (Player player : server.games.get(0).players)
                                player.client.writer.sendLetter(message, PlayersMessageSender.TypeMessage.other);
                        }
                }
                Thread.sleep(500);
            } catch (Exception e) {
                sc.close();
                e.printStackTrace();
                break;
            }
        }
        sc.close();
    }

    private void show_log(String[] message) {
        if (message.length < 2)
            return;

        String log = "LOG --> " + message[1] + " --> ";

        if (server.games.size() != 0)
            log = processLogMessage(message, log);

        ConsolePrint.success("ConsoleListener", log);
    }

    private String processLogMessage(String[] message, String log) {
        String messageType = message[1];

        log = switch (messageType) {
            case "day" -> processDayMessage(message, log);
            case "creaturesStats" -> processCreaturesStatsMessage(message);
            case "date" -> server.games.get(0).time.logDate();
            case "clouds" -> processCloudsMessage(message, log);
            case "weather" -> processWeatherMessage(message, log);
            case "partDay" -> processPartOfDayMessage(message);
            case "wind" -> server.games.get(0).maps.maps[0].sky.strengthOfWind + "";
            case "windDirection" -> processWindDirectionMessage(message, log);
            case "health" -> server.games.get(0).players.get(0).abilityCondition.getHealth() + "";
            case "abilityStats" -> processAbilityStatsMessage(message);
            case "move" -> {
                // TODO: Implement move logic

                yield "not implemented";
            }
            case "calendar" -> processCalendarMessage(message);
            case "map" -> processMapMessage(message);
            case "temp" -> server.games.get(0).maps.maps[0].logTemperature() + "";
            case "addResource" -> processAddResourceMessage(message);
            case "place" -> processPlaceMessage(message);
            case "time" -> {
                Server.clocks.setDelay(Long.parseLong(message[2]));
                yield "done";
            }
            default -> "Wrong log input!";
        };
        return log;
    }

    private String processDayMessage(String[] message, String log) {
        if (message.length < 3)
            throw new IllegalArgumentException("Wrong log input!");

        switch (message[2]) {
            case "stop":
                PlayersMessageSender.setPrintCyclesStatsToConsole(false);
                break;
            case "loop":
                PlayersMessageSender.setPrintCyclesStatsToConsole(true);
                break;
            default: return "Wrong log input!";    
        }
        return "done";
    }

    private String processCloudsMessage(String[] message, String log) {
        if (message.length >= 3) {
            boolean printCloudsInLoop = message[2].equals("loop");
            server.games.get(0).maps.maps[0].sky.printCloudsInLoop = printCloudsInLoop;
        } else if (Integer.parseInt(message[2]) >= 0 && Integer.parseInt(message[2]) <= 8) {
            // Implement logic for sending map clouds message
        } else {
            log += server.games.get(0).maps.maps[0].sky.printClouds();
        }
        return log;
    }

    private String processWeatherMessage(String[] message, String log) {
        if (message.length >= 3) {
            boolean printWeatherInLoop = message[2].equals("loop");
            server.games.get(0).maps.maps[0].sky.printWeatherInLoop = printWeatherInLoop;
        } else if (message[2].equals("idle")) {
            setWeather(0, 0, 0);
        } else if (message[2].equals("rain")) {
            setWeather(0, 0, 1);
        } else if (message[2].equals("heavy_rain")) {
            setWeather(0, 0, 2);
        } else if (message[2].equals("storm")) {
            setWeather(0, 0, 3);
        } else if (message[2].equals("thunderstorm")) {
            setWeather(0, 0, 4);
        } else {
            log += server.games.get(0).maps.maps[0].sky.printWeathers();
            return log;
        }
        return "done";
    }

    private String setWeather(int red, int green, int blue) {
        server.games.get(0).players.get(0).writer.surrounding.setWeather(new Weather(red, green, blue));
        return " done";
    }

    private String processPartOfDayMessage(String[] message) {
        if (message.length < 3)
            throw new IllegalArgumentException("Wrong log input!");

        String partOfDay = message[2];

        if (partOfDay.matches("after_midnight|sunrise|morning|afternoon|sunset1|sunset2|night")) {
            server.games.get(0).players.get(0).writer.surrounding
                    .setPartOfDay(DailyLoop.NamePartOfDay.valueOf(partOfDay));
            return "done";
        } else if (Integer.parseInt(partOfDay) < 8) {
            server.games.get(0).players.get(0).writer.surrounding
                    .setPartOfDay(DailyLoop.NamePartOfDay.values()[Integer.parseInt(partOfDay)]);
            return "done";
        } else return "Wrong log input!";
    }

    private String processWindDirectionMessage(String[] message, String log) {
        if (message.length >= 3) {
            int direction = Integer.parseInt(message[2]);
            if (direction >= 0 && direction <= 7) {
                server.games.get(0).maps.maps[0].sky.directionOfWind = direction;
                log += "done";
            }
        } else {
            log += server.games.get(0).maps.maps[0].sky.directionOfWind;
        }
        return log;
    }

    private String processAbilityStatsMessage(String[] message) {
        if (message.length > 2) {
            boolean printAbilityStatsToConsole = message[2].equals("loop");
            PlayersMessageSender.setPrintCreaturesAbuilityStatsToConsole(printAbilityStatsToConsole);
            return "done";
        } else return "Wrong log input!";
    }

    private String processAddResourceMessage(String[] message) {
        if (message.length > 5) {
            server.games.get(0).maps.maps[0].places[Integer.parseInt(message[2])][Integer.parseInt(message[3])]
                    .addResource(
                            ListOfAllTypesOfResources.typesOfResources.get(NamesOfTypesOfResources.valueOf(message[4])),
                            Integer.parseInt(message[5]));
            return "done";
        } else return "Wrong log input!";
    }

    private String processPlaceMessage(String[] message) {
        if (message.length >= 3) {
            server.games.get(0).maps.maps[0].places[Integer.parseInt(message[2])][Integer.parseInt(message[3])].log();
            return "done";
        } else return "Wrong log input!";
    }

    private String processMapMessage(String[] message) {
        String log = "";
        if (message.length >= 3) {
            log = server.games.get(0).maps.maps[Integer.valueOf(message[2])].logMap();
        } else {
            log = server.games.get(0).maps.maps[0].logMap();
        }
        return log;
    }

    private String processCreaturesStatsMessage(String[] message) {
        if (message.length > 2) {
            boolean printCreatureStatsToConsole = message[2].equals("loop");
            PlayersMessageSender.setPrintCreatureStatsToConsole(printCreatureStatsToConsole);
            return "done";
        } else return "Wrong log input!";
    }

    private String processCalendarMessage(String[] message) {
        if (message.length >= 3) {
            boolean logLoopThread = message[2].equals("loop");
            server.games.get(0).calendarsLoop.logLoopThread = logLoopThread;
            return "done";
        } else return "Wrong log input!";
    }
}