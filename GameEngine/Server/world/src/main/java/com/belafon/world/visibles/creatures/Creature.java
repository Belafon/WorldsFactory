package com.belafon.world.visibles.creatures;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.ArrayList;

import com.belafon.server.sendMessage.MessageSender;
import com.belafon.world.World;
import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.objectsMemory.ObjectsMemoryCell;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.objectsMemory.creaturesMemory.CreaturesMemory;
import com.belafon.world.visibles.creatures.behaviour.Behaviour;
import com.belafon.world.visibles.creatures.behaviour.BehaviourCondition;
import com.belafon.world.visibles.creatures.behaviour.PlayersLookAround;
import com.belafon.world.visibles.creatures.behaviour.VisiblesID;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleRequirement;
import com.belafon.world.visibles.creatures.condition.AbilityCondition;
import com.belafon.world.visibles.creatures.condition.ActualCondition;
import com.belafon.world.visibles.creatures.condition.knowledge.Knowledge;
import com.belafon.world.visibles.creatures.inventory.Inventory;
import com.belafon.worldsfactory.api.WorldsFactoryStoryManager;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryClass;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryObjectsName;
import com.belafon.worldsfactory.api.annotations.WorldsFactoryPropertySetter;
import com.belafon.world.visibles.items.Item;

@WorldsFactoryClass(className = "Creature")
public abstract class Creature extends Visible {
    /**
     * this is a requirement that says any creature is required for some behaviour
     * execution
     */
    public static final BehavioursPossibleRequirement REQUIREMENT = new BehavioursPossibleRequirement(
            "Creature is visible.") {
    };

    @WorldsFactoryObjectsName
    public String name;

    protected UnboundedPlace location;
    public final String appearence;
    public final AbilityCondition abilityCondition;
    public final ActualCondition actualCondition;
    public final BehaviourCondition behaviourCondition;
    public final InfluencingActivities influencingActivities;

    @FunctionalInterface
    public static interface ActionChangeIngredient {
        void doJob(BehavioursPossibleIngredient ingredient);
    }

    public final void getAllNotVisibleIngredients(ActionChangeIngredient action) {
        // work with all visibles
        synchronized (currentlyVisibleObjects) {
            for (Visible visible : currentlyVisibleObjects.values()) {
                action.doJob(visible);
            }
        }

        // work with all knowledge
        synchronized (knowledge) {
            for (Knowledge knowledge : knowledge) {
                action.doJob(knowledge);
            }
        }

        // work with all items in inventory
        inventory.getAllItems((item) -> {
            action.doJob(item);
        });
    }

    /**
     * null means idle, each creature can do just one behaviour,
     * whose duration is not 0, in time.
     */
    public Behaviour currentBehaviour = null; // idle
    public Inventory inventory;
    public final World game;
    public final int id;
    public final MessageSender writer;
    private volatile int weight;
    private final Set<Knowledge> knowledge = new ConcurrentSkipListSet<>();

    public final List<Item> storyInventory = new ArrayList<>();
    
    @WorldsFactoryPropertySetter(name = "inventory")
    public void setStoryInventory(int index, Item item) {
        if (index >= storyInventory.size())
            storyInventory.add(item);
        else {
            if(storyInventory.get(index) != null){
                inventory.removeItem(storyInventory.get(index));
            }
            storyInventory.set(index, item);

        }

        if(item != null){
            inventory.addItem(item);
        }

        WorldsFactoryStoryManager.setProperty("inventory[" + index + "]", item, this.name);
    }

    protected final Map<VisiblesID, Visible> currentlyVisibleObjects = new HashMap<>();

    public final CreaturesMemory memory = new CreaturesMemory();

    public PlayersLookAround surroundingPlaces;

    public Creature(World game, String name, UnboundedPlace location,
            String appearence, MessageSender sendMessage,
            int weight) {
        this.id = game.visibleIds.getCreatureId();
        this.game = game;
        this.writer = sendMessage;
        this.location = location;
        this.appearence = appearence;
        this.name = name;
        this.weight = weight;
        influencingActivities = new InfluencingActivities(writer);
        setInventory(location);

        // TODO write a builder
        abilityCondition = new AbilityCondition(this, 100, 100, 100, 100, 100, 100, 100);
        behaviourCondition = new BehaviourCondition(this);
        actualCondition = new ActualCondition(this);

        setLocation(location);
    }

    public void setupSurroundingVisiblePlacesWhenGameStarts() {
        var newLook = PlayersLookAround.look(location);
        updateSurroundingVisiblePlaces(newLook, surroundingPlaces);
        surroundingPlaces = newLook;
        writer.surrounding.setCurrentPositionInfo(getLocation());
    }

    public void setAbilityCondition(int strength, int agility, int speed_of_walk, int speed_of_run, int hearing,
            int observation, int vision) {
        // strength, agility, speed_of_walk, speed_of_run, hearing, observation, vision
        abilityCondition.setStrength(strength);
        abilityCondition.setAgility(agility);
        abilityCondition.setSpeedOfWalk(speed_of_walk);
        abilityCondition.setSpeedOfRun(speed_of_run);
        abilityCondition.setObservation(observation);
        abilityCondition.setHearing(hearing);
        abilityCondition.setVision(vision);
    }

    /**
     * Initializes invertory of some creature.
     * 
     * @param position
     */
    protected abstract void setInventory(UnboundedPlace position);

    /**
     * executes creatures behaviour
     * 
     * @param behaviour
     */
    public void setBehaviour(Behaviour behaviour) {
        writer.condition.setBehaviour(behaviour, behaviour == null ? 0 : behaviour.getDuration());

        if (behaviour == null || behaviour.getDuration() != 0)
            currentBehaviour = behaviour;

        if (behaviour != null)
            behaviour.execute();

        getWatchers((watchers) -> {
            for (Creature creature : watchers)
                creature.influencingActivities.otherCreaturesBehaviourChanged(creature);
        });
    }

    /**
     * Changed creatures position.
     * 
     * @param position
     */
    @WorldsFactoryPropertySetter(name = "location")
    public void setLocationInMap(Place position) {
        PlayersLookAround lastLook = this.surroundingPlaces;
        memory.addPosition(new ObjectsMemoryCell<Place>(game.time.getTime(), position));
        this.surroundingPlaces = PlayersLookAround.look(position);

        writer.surrounding.setCurrentPositionInfo(position);

        updateSurroundingVisiblePlaces(surroundingPlaces, lastLook);

        this.location = position;

        if (!name.equals("")) {
            WorldsFactoryStoryManager.setProperty("location", position, name);
        }

        removeAllVisibles();

        writer.surrounding.setInfoAboutSurrounding(surroundingPlaces);

        // all players watching that have to get notice that
        getWatchers((watchers) -> {
            for (Creature creature : watchers)
                creature.influencingActivities.otherCreaturesPositionChanged(creature);
        });
        memory.getVisibleObjectLostFromSight((lostVisibles) -> {
            if (lostVisibles.containsKey(position)) {
                for (ObjectsMemoryCell<Visible> lostVisible : lostVisibles.get(position)) {
                    if (lostVisible.object().getLocation() == position)
                        addVisible(lostVisible.object());
                }
                lostVisibles.get(position).clear();
            }
        });
    }

    private void updateSurroundingVisiblePlaces(PlayersLookAround newLook, PlayersLookAround lastLook) {
        // lets remove all places in surrounding map from sight
        if (lastLook != null)
            for (int i = 0; i < newLook.visiblePlaces.length; i++) {
                for (int j = 0; j < newLook.visiblePlaces[i].length; j++) {
                    var lastPlace = lastLook.visiblePlaces[i][j];
                    if (lastPlace != null) {
                        synchronized (behaviourCondition.allIngredients) {
                            behaviourCondition.allIngredients.remove(lastPlace.getBehavioursPossibleIngredientID());
                        }
                        removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(lastPlace);
                        writer.surrounding.removePlaceFromSight(lastPlace, i, j);
                    }

                }
            }

        Set<Place> lastPlacesThatAreStillInSight = new HashSet<>(); // TODO remove
        for (int i = 0; i < newLook.visiblePlaces.length; i++) {
            for (int j = 0; j < newLook.visiblePlaces[i].length; j++) {
                updatePlace(newLook.visiblePlaces[i][j], lastPlacesThatAreStillInSight, newLook);
            }
        }
    }

    private void updatePlace(Place newPlace, Set<Place> lastPlacesThatAreStillInSight, PlayersLookAround newLook) {
        if (newPlace == null)
            return;

        synchronized (behaviourCondition.allIngredients) {
            // add to ingredietns
            behaviourCondition.allIngredients.put(newPlace.getBehavioursPossibleIngredientID(), newPlace);
            addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(newPlace);
        }
    }

    @Override
    protected void setLocation(UnboundedPlace place) {
        place.addCreature(this);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        memory.addWeight(new ObjectsMemoryCell<Integer>(game.time.getTime(), weight));
        this.weight = weight;
    }

    @Override
    public UnboundedPlace getLocation() {
        return location;
    }

    public Set<Knowledge> getKnowledge() {
        synchronized (knowledge) {
            return knowledge;
        }
    }

    public void addKnowledge(Knowledge knowledge) {
        this.knowledge.add(knowledge);
        synchronized (behaviourCondition.allIngredients) {
            behaviourCondition.allIngredients.put(knowledge.getBehavioursPossibleIngredientID(), knowledge);
        }
        behaviourCondition.addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(knowledge.type, knowledge);
        writer.condition.addKnowledge(knowledge);
    }

    public void removeKnowledge(Knowledge knowledge) {
        this.knowledge.remove(knowledge);
        behaviourCondition.removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(knowledge.type, knowledge);
        writer.condition.addKnowledge(knowledge);
    }

    /**
     * Adds new visible object to creatures vision.
     * This information is also saved in
     * {@link Game.ObjectsMemory.CreaturesMemory.CreaturesMemory.visibleObjectSpotted}
     * and also in
     * {@link Game.ObjectsMemory.CreaturesMemory.CreaturesMemory.lastVisiblesPositionWhenVisionLost}
     * Also, the set of currently possible behaviours is updated
     * 
     * Also, the viewed object memorizes the information in
     * {@link Game.ObjectsMemory.Visible.watchers}
     * 
     * @param visible
     */
    public void addVisible(Visible visible) {
        synchronized (behaviourCondition.allIngredients) {
            behaviourCondition.allIngredients.put(visible.getBehavioursPossibleIngredientID(), visible);
        }

        synchronized (currentlyVisibleObjects) {
            currentlyVisibleObjects.put(visible.getId(), visible);
        }

        memory.addVisibleObjectSpotted(new ObjectsMemoryCell<Visible>(game.time.getTime(), visible));

        visible.addWatcher(this);

        this.writer.surrounding.addVisibleInSight(visible, this);

        addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(visible);
    }

    private void addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(BehavioursPossibleIngredient ingredient) {
        for (BehavioursPossibleRequirement requirement : ingredient.getBehavioursPossibleRequirementType(this)) {
            behaviourCondition.addBehavioursPossibleIngredientAndCheckFeasibleBehaviours(requirement, ingredient);
        }
    }

    private void removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(BehavioursPossibleIngredient ingredient) {
        for (BehavioursPossibleRequirement requirement : ingredient.getBehavioursPossibleRequirementType(this)) {
            behaviourCondition.removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(requirement, ingredient);
        }
    }

    @FunctionalInterface
    public interface ActionGetCurremtlyObjectSpotted {
        void doJob(Collection<Visible> visibleObjectSpotted);
    }

    /**
     * This function is for manipulating with visibles list.
     * This aim to require stay safe even with concurent approach.
     */
    public void getCurrentlyVisibleObjectSpotted(ActionGetCurremtlyObjectSpotted visibles) {
        synchronized (currentlyVisibleObjects) {
            visibles.doJob(currentlyVisibleObjects.values());
        }
    }

    /**
     * This method removes some visible from creatures sight.
     * The creature cannot see the visible anymore.
     */
    public void removeVisible(Visible visible) {
        synchronized (behaviourCondition.allIngredients) {
            behaviourCondition.allIngredients.remove(visible.getBehavioursPossibleIngredientID());
        }
        removeVisibleObjectWithoutRemovingFromWatchers_BeforeLocationOfTheVisibleChanges(visible);
        visible.removeWatcher(this);
    }

    public void removeVisible(Iterator<Visible> iterator) {
        if (!iterator.hasNext())
            return;

        var visible = iterator.next();
        synchronized (behaviourCondition.allIngredients) {
            behaviourCondition.allIngredients.remove(visible.getBehavioursPossibleIngredientID());
        }
        removeVisibleObjectWithoutRemovingFromWatchers_BeforeLocationOfTheVisibleChanges(iterator, visible);

        visible.removeWatcher(this);
    }

    public void removeAllVisibles() {
        synchronized (currentlyVisibleObjects) {
            var iterator = currentlyVisibleObjects.values().iterator();
            while (iterator.hasNext()) {
                var visible = iterator.next();
                iterator.remove();
                removeVisibleObjectWithoutRemovingFromWatchers_BeforeLocationOfTheVisibleChanges(visible);
                visible.removeWatcher(this);
            }
        }
    }

    /**
     * Checks if the creature sees the given visible
     * 
     * @param value
     * @return
     */
    public boolean seesVisibleObject(Visible value) {
        synchronized (currentlyVisibleObjects) {
            return currentlyVisibleObjects.values().contains(value);
        }
    }

    @Override
    public List<BehavioursPossibleRequirement> getBehavioursPossibleRequirementType(Creature creature) {
        return Arrays.asList(REQUIREMENT);
    }

    @Override
    public int getVisibility() {
        // TODO needs update
        return abilityCondition.getLoudness();
    }

    @Override
    public Class<? extends Visible> getClassType() {
        return Creature.class;
    }

    @Override
    protected int getIdNumber() {
        return id;
    }

    /**
     * Removes visible from creatures sight.
     * It does not remove the visible from watchers.
     * 
     * It is important to call this method before changing
     * the location of the visible!!!
     * 
     * It is because of the getBehavioursPossibleRequirementType
     * called on the visible. If the posisition of the visible
     * is changed, the method will return different value.
     * 
     * @param visible
     */
    public void removeVisibleObjectWithoutRemovingFromWatchers_BeforeLocationOfTheVisibleChanges(Visible visible) {
        synchronized (currentlyVisibleObjects) {
            currentlyVisibleObjects.remove(visible.getId());
        }

        memory.addVisibleObjectLostFromSight(new ObjectsMemoryCell<Visible>(game.time.getTime(), visible),
                visible.getLocation(), this);

        for (BehavioursPossibleRequirement requirement : visible.getBehavioursPossibleRequirementType(this)) {
            behaviourCondition.removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(requirement, visible);
        }

        this.writer.surrounding.removeVisibleFromSight(visible);
    }

    /**
     * Same as the method above, but it is used when
     * the iterator is used.
     * 
     * @param iterator
     * @param visible
     */
    public void removeVisibleObjectWithoutRemovingFromWatchers_BeforeLocationOfTheVisibleChanges(
            Iterator<Visible> iterator, Visible visible) {

        synchronized (currentlyVisibleObjects) {
            iterator.remove();
        }

        memory.addVisibleObjectLostFromSight(new ObjectsMemoryCell<Visible>(game.time.getTime(), visible),
                visible.getLocation(), this);

        for (BehavioursPossibleRequirement requirement : visible.getBehavioursPossibleRequirementType(this)) {
            behaviourCondition.removeBehavioursPossibleIngredientAndCheckFeasibleBehaviours(requirement, visible);
        }

        this.writer.surrounding.removeVisibleFromSight(visible);
    }
}
