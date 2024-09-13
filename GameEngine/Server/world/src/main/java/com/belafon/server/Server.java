package com.belafon.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;
import java.util.*;

import com.belafon.App;
import com.belafon.console.ConsoleListener;
import com.belafon.console.ConsolePrint;
import com.belafon.server.matchMakingSystems.BasicMatchMakingSystem;
import com.belafon.server.matchMakingSystems.MatchMakingSystem;
import com.belafon.world.World;
import com.belafon.world.time.Clocks;


public class Server {
	private int port = 25555;
	public volatile boolean isServerRunning = true;

	public final List<World> games = Collections.synchronizedList(new ArrayList<World>());
	public final int numberOfPlayersForStartTheGame = 1;
	public final MatchMakingSystem matchMaking = new BasicMatchMakingSystem(this, numberOfPlayersForStartTheGame);
	public final ConsoleListener consoleListener = new ConsoleListener(this);
	public final Map<String, Client> allClients = new Hashtable<String, Client>(); // is accsessed via more threads 
    public static final Clocks clocks = new Clocks();

    // nuber of threads should be set to 1 to avoid message conflicts
    public ExecutorService executor = Executors.newFixedThreadPool(EXECUTORS_THREAD_COUNT);
    public static final int EXECUTORS_THREAD_COUNT = 1;

    /**
     * It creates the server, which starts to listen clients.
     */
    public Server() {
        printIpAdresses();

        clocks.start();

        ConsolePrint.serverInfo("Executor uses " + EXECUTORS_THREAD_COUNT + " threads.");
        ConsolePrint.serverInfo("Server is waiting for clients...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsolePrint.serverInfo("Server: New Server created on port " + port);
            while (isServerRunning) {
                App.isServerRunning = true;
                Socket socket = serverSocket.accept();
                handleClient(socket);
            }
        } catch (IOException e) {
            ConsolePrint.error_big("Server: Server stoped new client acception! ");
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

    }

    private static String getLocalIpAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
          NetworkInterface iface = interfaces.nextElement();
          Enumeration<InetAddress> addresses = iface.getInetAddresses();
          while (addresses.hasMoreElements()) {
            InetAddress addr = addresses.nextElement();
            if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
              return addr.getHostAddress();
            }
          }
        }
        return null;
      }
    
    /**
     * new message from a client reveived.
     * It checks if it is new connection.
     */
    private void handleClient(Socket clientSocket) {
        String ip = clientSocket.getInetAddress().getHostAddress().toString();
        Client client = allClients.get(ip);
        //if (client == null) {
        ConsolePrint.serverInfo("Server: New device connected :: " + ip);
        client = createClient(clientSocket);
        new MessageReceiver(clientSocket, this, client); // recever of new messages
        /* } else {
            ConsolePrint.serverInfo("Server: Device connected again :: " + ip);
            client.bindNewSendMessage(clientSocket);
            if (client.disconnected)
                new MessageReceiver(clientSocket, this, client);
        }*/
    }
	
    private Client createClient(Socket clientSocket) {
        Client client = new Client(clientSocket, this);
        allClients.put(client.ipAddress, client);
        ConsolePrint.serverInfo("New client created");
        return client;
    }

	private void printIpAdresses() {
		try {
			ConsolePrint.serverInfo("Local ip = " + getLocalIpAddress());
		} catch (SocketException e) {
			ConsolePrint.error("Local ip was not gotten");
		}
		
		try {
			//ConsolePrint.serverInfo("External ip = " + getExternalIp());
		} catch (Exception e) {
			ConsolePrint.error("External ip was not gotten");
		}
	}

    private String getExternalIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Removes world object from list of worlds.
     * @param world
     */
    public void endTheWorld(World world) {
        games.remove(world);
    }


	 
	/*
	 * getter get_type_of_weather nothing
	 * getter get_type_of_weather rain
	 * getter get_type_of_weather heavy_rain
	 * getter get_type_of_weather storm
	 * getter get_type_of_weather thunderstorm
	 * 
	 * getter get_part_of_day sunset_1
	 * getter get_part_of_day sunset_2
	 * getter get_part_of_day after_midnight
	 * getter get_part_of_day sunrise_1
	 * getter get_part_of_day sunrise_2
	 * getter get_part_of_day morning
	 * 
	 * getter get_clouds 0
	 * 
	 * setter putOffGear 3
	 * 
	 * behaviour move 25 1.5f
	 * behaviour eat 2
	 * behaviour explore_surrounding take_notice 20 Items_Food.Apple
	 */
}
