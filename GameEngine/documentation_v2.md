

The mobile client code itself uses Java version 17 with Gradle 8.0.2.

To run, both the server and client must be started on the same local network.

Launching the mobile client should be possible by transferring the file `MobileClient/apk/debug/app-debug.apk` to a mobile device and then executing it. Note that Android may try to prevent this.

Alternatively, the application can be run through Android Studio, which requires Android Studio with Java version 17.

The server can be started as a JAR application ideally from the command line:

```bash
java -jar ./Server/world/target/world-0.1-jar-with-dependencies.jar
```

Documentation can be generated using the `doxygen` command in the doc directory for the project.

### Mechanics of Behaviors

Any creature can perform a behavior only if it meets all the requirements defined by BehavioursPossibleRequirements and their quantity. For example, to execute the Move behavior, meeting the REQUIREMENT_IS_REACHABLE requirement once is necessary.

Subsequently, any creature can fulfill these requirements using BehavioursPossibleIngredients. For instance, UnboundedPlace implements BehavioursPossibleIngredient, meaning it must implement the method `List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature)`. This method takes a creature as a parameter and returns a list of requirements that the creature can fulfill using the given ingredient.

Then, if UnboundedPlace returns a list for a specific creature containing REQUIREMENT_IS_REACHABLE, that creature can perform the Move behavior.

It is not possible to fulfill multiple BehavioursPossibleRequirements with one ingredient.

## Server Logs

New log messages have been added, detailed in `Server/README.md` for the server application. Log messages have been added for manually setting the current time of day, weather, or clouds at the player's current position.

Additionally, a log message, "log time x," has been added to speed up or slow down the overall game speed and environment speed. The variable x indicates the delay of the game loop.

For easier tracking, it is recommended to accelerate time using the `log time 20` command in the server console. If you want to monitor the weather conditions, slow down time using `log time 3000` to avoid frequent changes in the server logs.

### MobileClient

After turning on and connecting to the server, the application will automatically attempt to start the game, with the server set up for a single-player game. It will generate the world and create a player located on the map at position 0, 0.

Simultaneously, the server is configured to generate items, resources, and creatures on the same tile as the player, visible from the beginning. This is because the game behavior for finding items or resources is not yet implemented.

The entire game is in one activity, GameActivity, which contains all the necessary components for the game in various fragments:

- **Character statistics**
- **Lists of visibles** divided into three columns:
  - Creatures in the vicinity on the same tile as the player
  - Items on the same place as the player
  - Resources similarly
- **List of achievable behaviors**
- **Map of the surroundings** showing tiles within a radius of 3 tiles. Each tile can be clicked to see detailed information about it.
- **Inventory** showing all items the player has.
- **Survey of the current tile** – This is only for the player to get a better look at the background image.

In the background of the activity is an ImageView that changes based on the current Place where the player is. Just before it is a partially transparent view that changes its color based on the current weather and time of day.

There is only one filter, and its color is calculated in the WeatherFragment, where a set of colorViewTransitions is used for the calculation.

It runs at approximately 50 FPS by default.

So far, the filter is influenced by two types of events:
- **Time of day**, repeating once a day.
- **Weather**:
  - **During rain**, it darkens and brightens.
  - **During a storm**, it darkens, and lightning may occur.
  - **In overcast conditions**, it depends on the size of the clouds. If it's not completely overcast, occasional darkening and subsequent brightening may occur. Conversely, if it's entirely overcast, the filter darkens until the clouds change.

For the transition for a part of the day, `PartOfDayColorViewTransition` uses a queue for individual transitions. If the queue is longer, the transitions intentionally speed up. These requirements are defined using BehaviourPossibleIngredients. However, the transition does not accelerate in the middle but only at the beginning. If the time accelerates too much, one transition may proceed at a normal pace, but in the meantime, the queue fills up, so the next transmission almost immediately ends, leading to flickering. This only happens at very high time acceleration.

Currently, only three types of behaviors can be performed:
- **Move**, can be done through the map fragment or the behaviors list fragment (applies to all behaviors and fragments about specific ingredients).
- **Eat**, can only be done from the inventory or the behaviors list fragment.
- **Pick up an item**, can be done from the behaviors list fragment or the list of visibles.

## Unfinished Sections

Certain code currently has no impact on the final application as it is not yet complete. Examples include:

- **WelcomingActivity**, where WelcomingActivity should only be displayed at the first launch of the application and contain some welcome information. It is partially finished, but I did not know what else to add besides selecting a name.
- **MenuActivity** contains basic menu functionality, but currently, only the button to start the game works, so the game automatically starts.
- **Music**, controlled through ClipsAdapter and Pool in the sound directory. The pool is used for short sounds, and ClipsAdapter is used for longer music or sound.
- **Equipment**, or PlayersGear, should be in the inventory with behaviors for deployment. Equipped clothing should add stats to the character.
- **Behavior of finding resources on a tile** - theoretically completed but not refined. Searching and observing other visibles work based on the visibility value. In the case of resources, it is the mass value. Then, when a creature searches for something, the most visible visibles are immediately visible, and the fewer visibles visible compared to other visibles, the harder it is to find them. However, once a visible is found, the creature should remember its position, and until the visible changes its position, it should be visible to that creature.
