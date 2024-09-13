package com.belafon;

import com.belafon.server.Server;
import com.belafon.world.maps.place.ListOfAllTypesOfPlaces;
import com.belafon.world.visibles.creatures.behaviour.BehaviourType;
import com.belafon.world.visibles.items.ListOfAllItemTypes;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources;

public class App {
    public static Server server;
    public static volatile boolean isServerRunning = false;

    public static void main(String[] args) {
        ListOfAllTypesOfResources.setUpAllResources();
        ListOfAllTypesOfPlaces.setUpAllTypesOfPlaces();
        ListOfAllItemTypes.setUpItems();

        BehaviourType.setUpAllBehavioursPossibleRequirements();

        //createLocalClient();
        server = new Server();
    }

    public static void createLocalClient() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Thread.currentThread().setName("ClientStart");

                while (!isServerRunning) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 1; i++) {
                    new Client();
                }
            }

        }).start();
    }
}

/*
 * 
mvn install:install-file -Dfile="/home/belafon/Documents/projects/bc_thesis_tichavsky/WorldsFactoryJavaLibrary/worldsfactory_java_library/target/worldsfactory_java_library-1.0-SNAPSHOT.jar" \
    -DgroupId="com.belafon.worldsfactory" \
    -DartifactId="worldsfactory_java_library" -Dversion="1.0-SNAPSHOT" \
    -Dpackaging=jar
 * 
 */
