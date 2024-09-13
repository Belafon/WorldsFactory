### Server Changes
- Date changed from int to long

Overflow of time is no longer a concern.

- History of visibles added

"Visible" now includes any Creature, Item, or Resource. It now stores information about its history.

- Memory of creatures added

Each creature now retains information about what it perceives around itself. It stores information about what a visible has seen and when. The current state of a visible at a given moment can be obtained from the history of that visible.

- Unbounded place added

A place not bound to any map. "Place" inherits from "UnboundedPlace" and is associated with a specific map.

- Space item added

A special type of item associated with an UnboundedPlace. It can be an entrance to another map or an item with a place not bound to any map, such as a backpack or a chest.

- Knowledges added

Allows specifying what a given creature is capable of.

- Creatures BehavioursPossibleIngredients added

BehavioursPossibleIngredients are specific visibles or knowledge required for a behavior. Each has a list of BehaviourPossibleRequirements it fulfills.

- Behaviours Possible requirement added

A requirement necessary to fulfill a behavior. Each BehaviourType specifies how many of each BehaviourPossibleRequirement is needed. For example, creating a fire may require obtaining a flammable object, so the behavior "buildFire" will have a requirement indicating the need for a certain number of flammable visibles.

- Tools Utilization added

A BehaviourPossibleRequirement for a tool.

- Memory of visible items split into currentlyVisibleObjects, lastVisiblesPositionWhenVisionLost, and visibleObjectSpotted.

Each creature now has a list of currently visible visibles. It also remembers when a visible was last seen at a specific location, useful when a creature returns to a place it has been before. It also keeps a list of when it spotted a particular visible, which can have various uses.

- Comments added

- New feasible behaviours added

A list of currently achievable behaviors for a given creature. The list changes with changes in BehavioursPossibleIngredients.

- Some synchronization conflicts resolved

Primarily lists accessed from multiple threads.

- Messages added

Added the sending of messages and information to a creature. If the creature is a player, the information is sent to the client.

### PC Client Changes

This was created entirely. It uses the Swing library for rendering the user interface.

It receives messages from the server and tries to display them clearly to the client.

The client automatically appears when the server is turned on in the corner of the map. Only places within a distance of 2 are displayed, and only those that the player sees. The visibility depends on the altitude of the place. If one place obstructs the view of another place, places behind it are not visible unless there is a higher place behind it.