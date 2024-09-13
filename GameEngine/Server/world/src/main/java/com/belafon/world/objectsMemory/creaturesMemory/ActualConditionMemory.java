package com.belafon.world.objectsMemory.creaturesMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.belafon.world.objectsMemory.ObjectsMemoryCell;

public class ActualConditionMemory {
    private final List<ObjectsMemoryCell<Integer>> hunger = new ArrayList<>();
    private final ReentrantLock mutexHunger = new ReentrantLock();
    
    private final List<ObjectsMemoryCell<Integer>> fatigueMax = new ArrayList<>();
    private final ReentrantLock mutexFatigueMax = new ReentrantLock();
    
    private final List<ObjectsMemoryCell<Integer>> heat = new ArrayList<>();
    private final ReentrantLock mutexHeat = new ReentrantLock();
    
    private final List<ObjectsMemoryCell<Integer>> bleeding = new ArrayList<>();
    private final ReentrantLock mutexBleeding = new ReentrantLock();
    
    public ObjectsMemoryCell<Integer> getHunger(int i) {
        return hunger.get(i);
    }
    
    public ObjectsMemoryCell<Integer> getFatigueMax(int i) {
        return fatigueMax.get(i);
    }
    
    public ObjectsMemoryCell<Integer> getHeat(int i) {
        return heat.get(i);
    }
    
    public ObjectsMemoryCell<Integer> getBleeding(int i) {
        return bleeding.get(i);
    }
    
    public void addHunger(ObjectsMemoryCell<Integer> value) {
        mutexHunger.lock();
        try {
            hunger.add(value);
        } finally {
            mutexHunger.unlock();
        }
    }
    
    public void addFatigueMax(ObjectsMemoryCell<Integer> value) {
        mutexFatigueMax.lock();
        try {
            fatigueMax.add(value);
        } finally {
            mutexFatigueMax.unlock();
        }
    }
    
    public void addHeat(ObjectsMemoryCell<Integer> value) {
        mutexHeat.lock();
        try {
            heat.add(value);
        } finally {
            mutexHeat.unlock();
        }
    }
    
    public void addBleeding(ObjectsMemoryCell<Integer> value) {
        mutexBleeding.lock();
        try {
            bleeding.add(value);
        } finally {
            mutexBleeding.unlock();
        }
    }
}
