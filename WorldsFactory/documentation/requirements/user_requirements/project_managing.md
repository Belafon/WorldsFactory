
#### After boot up
- [ ] User can see new updates of the software
  - `so the user can be informed about new features`
- [ ] User can see information about any part of the softwre easily
  - `so the user can learn how to use the software easily`
- [x] User can see the list of all the projects
  - `so the user can easily find the project he/she wants to work on`
- [x] User can see the list of projects he/she was working on recently
  - `so the user can easily find the project he/she wants to work on `
- [x] User can look up a project in directory manager
  - `so the user can open a project that is not created by him/her`
- [x] User can open a project
  - `so the user can work on the project`
- [x] User can create a new project
    - `so all the project information is stored in a predefined place`

#### After opening a project

- [ ] User can change the basic project information, like name, description, etc.
  - `so the user doesn't have to create new project if he/she wants to change something.`

#### Project overview
- [ ] User can see overview of the project. (It can also work as a navigation bar)
  - `so the user can see the project's structure`
    - It should contain all main things:
      -  World
        - [x] Library, shows list of all classes 
        - [x] Objects, shows list of all objects
        - [x] Events, shows list of all events
        - [x] Visualisations
      - List of Works

##### Library manager
- [x] User can define new type with original name.
- [x] User can add a function to a type with name, return type and parameters.
- [x] User can add a property to a type with concrete type and name, the name has to be original in the context of the type.
- [ ] User can define a static function to a type.
- [ ] User can define a static property to a type.
- [x] User can make the type to extend another type, only one inheritance for a type and cycles prohibited.
  - `so the user can create a type hierarchy`
- [ ] User can implement a function of a type (basic, or static). But the player doesn't have to implement it at all. User can also redefine the function. 
  - `so the user can bind the function with the implementation of a game engine`

- [ ] User can extend the library with an external library, so the library is dependent on the external library.
  - `the user doesn't have to implement the whole library, just the parts that are not implemented in the external library`
- [x] User can delete a type (and all children).
- [x] User can delete a function.
- [x] User can delete a property.  

###### Tags
- [ ] User can set a set of tags for a class, property, function, event... anything.

###### Visualization
- [ ] User can see the type hierarchy of the library in a tree view.
- [ ] User can see the library hierarchy of the dependencies in a tree view.

##### Object manager
- [x] User can create an object from a type with original name.
- [ ] User can filter the objects by type (if is parent), or name.
- [x] User can delete an object.
- [x] User can rename an object. 

##### Event manager
###### event

- [x] User can create an event with original name, or id, and defines condition function and execution function.
- [ ] User can extend a type with an event type, so the event can be related with additional information.
- [x] User can delete an event.
- [x] User can rename an event or redefine the functions.
- [x] User can group events by sets. between that can be also set relationships and that also respects events relationships.

###### relationships

- [ ] User can set a set of events for an event, that can be executed after this event, but other events cannot be executed. 

###### linear event

- [ ] User can create a linear event. He has to define the time type property with time getter function (so the time can be computed without any bounderies), it has to be one comparable number. The user also has to define duration of the event. 
- [ ]  User can define a subevent to a linear event. The subevent is also a linear event.
- [ ]  User can delete any linear event.

##### Visualisation

###### visualisation of events
- [ ] User can see a graph of relationships of all events.
- [ ] User can see a graph created from a linear event and its subevents. this graph shows a timeline with the time.
- [ ] The events can be filtered by getters and setters that are used in event's functions.

- [ ] User can see the state of the world while he defines a path of events that happened.
- [ ] User can filter displayed objects by filter functions, while the event path is set.
- [ ] User can display a custom graph, where the vertices are objects defined by a list of types, or list of objects. The edges are defined by a custom function, that has an object as a parameter and returns an array of other objects, while the event path is set.
- [ ] User can display a map, or a line, of a property. While the event path is set, in the map or line, there are displayed all objects, that have the property and they are displayd in the property's value (lately set) position.



# Questions
- id by strings
