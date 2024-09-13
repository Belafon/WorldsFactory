## Examples of what an API might look like:
### Requirements

- Linking the properties of an engine object so that whenever they change, the story object changes as well.
  - Ideally, the user should have to modify the engine code as little as possible. Something like marking classes with an annotation to indicate they are story objects.
- Setting when the conditions for an event should be evaluated. Whether it is on any property change or only on a custom method call.

#### 1. Annotation
Ideal approach ->
```java

@StoryObject()
public class EngineObject {
    @LinkedField()
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

```

This approach has a problem because it doesn't allow setting the object and property names.

#### 2. Inheritance 

Has the problem that the object cannot inherit anything else.

#### 3. Interface

Cannot have a default interface.

#### 4. Annotation + Static Initialization

Seems to be the best option.

The library will have static support with the possibility of extension to support more stories.

The problem is two-way binding. If starting a project from scratch, knowing that this software will be used, there is no problem.

Getters and setters could be marked.

#### 5. Direct referencing of the object by name

If the user wants to have objects only in the library, this allows it.