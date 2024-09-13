package com.belafon.console;


import com.belafon.server.Client;

public class ConsolePrint {
    public static void error_small(String message){
        System.out.println(ConsoleColors.RED_BRIGHT + message + ConsoleColors.RESET);
    }
    public static void error(String message){
        System.out.println(ConsoleColors.RED + message + ConsoleColors.RESET);
    }
    public static void error_big(String message){
        System.out.println(ConsoleColors.RED_BOLD + "ERROR: " + message + ConsoleColors.RESET);
    }
    public static void serverInfo(String message){
        System.out.println(ConsoleColors.YELLOW + message + ConsoleColors.RESET);
    }
    public static void gameInfo(String message){
        System.out.println(ConsoleColors.CYAN + message + ConsoleColors.RESET);
    }

    public static void warning(String message){
        System.out.println(ConsoleColors.ORANGE + message + ConsoleColors.RESET);
    }
    public static void success(String TAG, String message){
        System.out.println(ConsoleColors.WHITE_BOLD + TAG + ": " + ConsoleColors.GREEN + message + ConsoleColors.RESET);
    }

    public static void new_message(String message, Client sender) {
        if (sender.name == null) {
            System.out.println(ConsoleColors.CYAN + "GET MESSAGE :: " + ConsoleColors.CYAN_BOLD + message
                    + ConsoleColors.GREEN_BRIGHT + " from " + sender.ipAddress + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.CYAN + "GET MESSAGE :: " + ConsoleColors.CYAN_BOLD + message
                    + ConsoleColors.GREEN_BRIGHT + " from " + sender.name + ConsoleColors.RESET);
        }
        
    }
    public static void message_to_player(String message, String receaver){
        System.out.println(ConsoleColors.PURPLE + "SEND MESSAGE :: " + ConsoleColors.PURPLE_BOLD + message 
                + ConsoleColors.GREEN_BRIGHT + " to " + receaver + ConsoleColors.RESET);
        
    }
}
