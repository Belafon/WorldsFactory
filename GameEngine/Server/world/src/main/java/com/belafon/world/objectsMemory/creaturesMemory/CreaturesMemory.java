package com.belafon.world.objectsMemory.creaturesMemory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.belafon.world.maps.place.Place;
import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.objectsMemory.ObjectsMemoryCell;
import com.belafon.world.objectsMemory.Visible;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.resources.TypeOfResource;

public class CreaturesMemory {
    private final List<ObjectsMemoryCell<String>> name = new ArrayList<>();
    public final ReentrantLock mutexName = new ReentrantLock();
    private final List<ObjectsMemoryCell<Place>> position = new ArrayList<>();
    public final ReentrantLock mutexPosition = new ReentrantLock();
    private final List<ObjectsMemoryCell<String>> appearance = new ArrayList<>();
    public final ReentrantLock mutexAppearance = new ReentrantLock();

    /**
     * It comes handy, when creature returns to known place.
     * It does not need to find knowen visibles again.
     */
    private final Hashtable<UnboundedPlace, List<ObjectsMemoryCell<Visible>>> lastVisiblesPositionWhenVisionLost = new Hashtable<>();
    private final ReentrantLock mutexlastVisiblesPositionWhenVisionLost = new ReentrantLock();

    private final List<ObjectsMemoryCell<Visible>> visibleObjectSpotted = new ArrayList<>();
    public final ReentrantLock mutexVisibleObjectSpotted = new ReentrantLock();

    private final List<ObjectsMemoryCell<Integer>> weight = new ArrayList<>();
    public final ReentrantLock mutexWeight = new ReentrantLock();

    private volatile int lastSizeOfClouds = 0;
    private volatile int lastWeather = 0;
    public final Set<TypeOfResource> typeResources = new HashSet<>();

    public ObjectsMemoryCell<String> getName(int i) {
        mutexName.lock();
        try {
            return name.get(i);
        } finally {
            mutexName.unlock();
        }
    }

    public ObjectsMemoryCell<Place> getPosition(int i) {
        mutexPosition.lock();
        try {
            return position.get(i);
        } finally {
            mutexPosition.unlock();
        }
    }

    public ObjectsMemoryCell<Place> getLatestPosition() {
        mutexPosition.lock();
        try {
            if (position.size() > 0)
                return position.get(position.size() - 1);
            else
                return null;
        } finally {
            mutexPosition.unlock();
        }
    }

    public ObjectsMemoryCell<String> getAppearance(int i) {
        mutexAppearance.lock();
        try {
            return appearance.get(i);
        } finally {
            mutexAppearance.unlock();
        }
    }

    @FunctionalInterface
    public interface ActionGetLostVisibleObject {
        void doJob(Hashtable<UnboundedPlace, List<ObjectsMemoryCell<Visible>>> visibleObjectSpotted);
    }

    public void getVisibleObjectLostFromSight(ActionGetLostVisibleObject lostVisibleObject) {
        mutexlastVisiblesPositionWhenVisionLost.lock();
        try {
            lostVisibleObject.doJob(lastVisiblesPositionWhenVisionLost);
        } finally {
            mutexlastVisiblesPositionWhenVisionLost.unlock();
        }
    }

    public ObjectsMemoryCell<Visible> getVisibleObjectSpotted(int i) {
        mutexVisibleObjectSpotted.lock();
        try {
            return visibleObjectSpotted.get(i);
        } finally {
            mutexVisibleObjectSpotted.unlock();
        }
    }

    @FunctionalInterface
    public interface ActionGetObjectSpotted {
        void doJob(List<ObjectsMemoryCell<Visible>> visibleObjectSpotted);
    }

    public void getVisibleObjectSpotted(ActionGetObjectSpotted visibles) {
        mutexVisibleObjectSpotted.lock();
        try {
            visibles.doJob(visibleObjectSpotted);
        } finally {
            mutexVisibleObjectSpotted.unlock();
        }
    }

    public ObjectsMemoryCell<Integer> getWeight(int i) {
        mutexWeight.lock();
        try {
            return weight.get(i);
        } finally {
            mutexWeight.unlock();
        }
    }

    public void addName(ObjectsMemoryCell<String> value) {
        mutexName.lock();
        try {
            name.add(value);
        } finally {
            mutexName.unlock();
        }
    }

    public void addPosition(ObjectsMemoryCell<Place> value) {
        mutexPosition.lock();
        try {
            position.add(value);
        } finally {
            mutexPosition.unlock();
        }
    }

    public void addAppearance(ObjectsMemoryCell<String> value) {
        mutexAppearance.lock();
        try {
            appearance.add(value);
        } finally {
            mutexAppearance.unlock();
        }
    }

    public void addVisibleObjectLostFromSight(ObjectsMemoryCell<Visible> value, UnboundedPlace place,
            Creature creature) {
        mutexlastVisiblesPositionWhenVisionLost.lock();
        try {
            if (!lastVisiblesPositionWhenVisionLost.containsKey(place))
                lastVisiblesPositionWhenVisionLost.put(place, new ArrayList<>());
            lastVisiblesPositionWhenVisionLost.get(place).add(value);

        } finally {
            mutexlastVisiblesPositionWhenVisionLost.unlock();
        }
    }

    public int getVisibleObjectSpottedSize() {
        mutexlastVisiblesPositionWhenVisionLost.lock();
        try {
            return lastVisiblesPositionWhenVisionLost.size();
        } finally {
            mutexlastVisiblesPositionWhenVisionLost.unlock();
        }
    }

    public void addVisibleObjectSpotted(ObjectsMemoryCell<Visible> value) {
        mutexVisibleObjectSpotted.lock();
        try {
            visibleObjectSpotted.add(value);
        } finally {
            mutexVisibleObjectSpotted.unlock();
        }
    }

    public void addWeight(ObjectsMemoryCell<Integer> value) {
        mutexWeight.lock();
        try {
            weight.add(value);
        } finally {
            mutexWeight.unlock();
        }
    }

    public int getLastSizeOfClouds() {
        return lastSizeOfClouds;
    }

    public void setLastSizeOfClouds(int lastSizeOfClouds) {
        this.lastSizeOfClouds = lastSizeOfClouds;
    }

    public int getLastWeather() {
        return lastWeather;
    }

    public void setLastWeather(int lastWeather) {
        this.lastWeather = lastWeather;
    }

}
