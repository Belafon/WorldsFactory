# World
Discrete Simulation of the World

## Getting Started

The aim of my program is to create a gaming world. To construct a space formed by a map composed of individual tiles (Places). Each Place then has its properties (elevation, natural resources, weather, etc.).
On this map, creatures move, capable of performing various activities. Additionally, there are items on the map that creatures can interact with.

Another crucial part of the world is time, linked to the daily cycle, changing all character, item, and activity statistics.

The entire project is divided into a Server and a Client, where the server is a specific program, while the client can be any program sending messages to the server following its protocol. The client always controls a specific creature in the world.

```bash
java -jar ./Server/world/target/world-0.1-jar-with-dependencies.jar
```
