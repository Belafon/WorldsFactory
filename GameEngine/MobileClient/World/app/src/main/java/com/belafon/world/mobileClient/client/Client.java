package com.belafon.world.mobileClient.client;

import android.util.Log;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.dataSafe.DataLibrary;
import com.belafon.world.mobileClient.MainActivity;
import com.belafon.world.mobileClient.game.Game;
import com.belafon.world.mobileClient.game.client.MessageSender;
import com.belafon.world.mobileClient.game.client.chatListener.ChatListener;
import com.belafon.world.mobileClient.gameActivity.GameActivity;
import com.belafon.world.mobileClient.menuScreen.MenuActivity;
import com.belafon.world.mobileClient.Screen;
import com.belafon.world.mobileClient.game.Fragments;
import com.belafon.world.mobileClient.game.Stats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
    private static final String TAG = "Client";

    public static Socket clientSocket;
    public static String ip = "192.168.0.106";
    public static volatile int port = 25555;
    public static volatile boolean flow = false; // controls the network connection

    public static String name = "";
    public static volatile boolean disconnected = true;

    public static final DataLibrary clientsData = new DataLibrary("clientsData");
    public static volatile int condition; // 0 -> connected, 1 -> reconnected, 2 -> first start
    
    public static final int idle = 10;
    public static final int playing = 11;
    public static final int first_start = 12;
    public static volatile int actualGameId = -1;

    public static Stats stats = Game.stats;
    public static Fragments fragments;

    public static final MessageSender sender = new MessageSender();
    public static final ChatListener chatListener = new ChatListener();

    public Client() {
        condition = clientsData.LoadDataInteager(AbstractActivity.getActualActivity(), "clientsCondition");
        name = clientsData.LoadDataString(MainActivity.getActualActivity(), "clientsName");
        if (condition < 10)
            condition = first_start;
        if (condition == playing)
            actualGameId = clientsData.LoadDataInteager(AbstractActivity.getActualActivity(), "gameId");
    }

    public static void setName(String text) {
        if (!name.equals(text)) {
            name = text;
            clientsData.saveDataString(MainActivity.getActualActivity(), name, "clientsName");
            clientsData.saveDataInteager(MainActivity.getActualActivity(), idle, "clientsCondition");
        }
        sendMessage("name " + name);
    }

    // This method sets the connection to server
    public static void connect() {
        Log.d(TAG, "run: CREATE CLIENT --- LETS CONNECT");
        boolean flow = true; // to gets info about the flow
        try {
            try {
                clientSocket = new Socket(ip, port);
            } catch (UnknownHostException e) {
                MainActivity.getActualActivity().runOnUiThread(
                        () -> Screen.info("Wrong IP address format, or unknown host...", 0));
                flow = false;
            } catch (IOException i) {
                MainActivity.getActualActivity().runOnUiThread(
                        () -> Screen.info("Network exception...", 0));
                flow = false;
            }
            if (clientSocket == null)
                flow = false;

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            startListener(in);
        } catch (IOException e) {
            flow = false;
        } catch (Exception e) {
            flow = false;
        }

        Client.flow = flow;

        if (flow) {
            sendMessage = new SendMessage();

            clientsData.saveDataInteager(AbstractActivity.getActualActivity(), port, "port");
            clientsData.saveDataString(AbstractActivity.getActualActivity(), ip, "serverIp");
            disconnected = false;

            if (AbstractActivity.getActualActivity() instanceof MenuActivity)
                new Thread(() -> {
                    AbstractActivity.getActualActivity().runOnUiThread(
                            () -> ((MenuActivity) AbstractActivity.getActualActivity()).showMenuFragment());
                }).start();
        }
    }

    // app stores last ip, which was used
    // this method is called when connect to last ip button is clicked
    // it will try to connect to server with last ip
    public static void connectToLastIp() {
        ip = Client.clientsData.LoadDataString(AbstractActivity.getActualActivity(), "serverIp");
        port = Client.clientsData.LoadDataInteager(AbstractActivity.getActualActivity(), "port");
        new Thread(() -> connect()).start();
    }

    // sets listener of messages from server

    /**
     * The function never ends, when the server stops,
     * it will keep trying to connect
     * @param in
     */
    private static void startListener(final BufferedReader in) {
        Thread thread = new Thread(() -> {
            Thread.currentThread().setName("ClientMessageListener");
            String[] msgArgs = null;
            while (true) {
                String msg = null;
                try {
                    msg = in.readLine();
                    if(msg == null)
                        return;

                    msgArgs = msg.split(" ");
                    var msgArgsInList = new ArrayList<String>(Arrays.asList(msgArgs));
                    if(msgArgs[msgArgs.length - 1].equals("__start__string__")){
                        msgArgsInList.remove(msgArgsInList.size() - 1);
                        msgArgsInList.add(listenLongMessage(in));
                    }
                    msgArgs = msgArgsInList.toArray(new String[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException inEx){
                        continue;
                    }
                }
                final String message = msg;
                if (message != null) {
                    decomposeTheString(msgArgs);
                }
            }
        });
        thread.start();
    }

    private static String listenLongMessage(final BufferedReader in) throws IOException {
        var msg = new StringBuilder();
        var nextLine = "";
        while(!nextLine.equals("__end__string__")){
            msg.append(nextLine + "\n");
            nextLine = in.readLine();
        }
        return msg.toString();
    }

    private static SendMessage sendMessage;

    public static void sendMessage(String message) {
        try {
            sendMessage.write(message);
        } catch (Exception e) {
            Log.d(TAG, "write: ERROR " + e);
            return;
        }
        Log.d(TAG, "write: text was written  ->  " + message);
    }

    public synchronized static void decomposeTheString(String[] value) {
        Thread.currentThread().setName("MessageHandler");

        chatListener.listen(value);
    }

    public static void setFragments(Fragments newFragments) {
        fragments = newFragments;
    }
}
