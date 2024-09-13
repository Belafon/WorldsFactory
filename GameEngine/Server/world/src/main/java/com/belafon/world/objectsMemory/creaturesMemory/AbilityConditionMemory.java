package com.belafon.world.objectsMemory.creaturesMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.belafon.world.objectsMemory.ObjectsMemoryCell;

public class AbilityConditionMemory {
    private final List<ObjectsMemoryCell<Integer>> health = new ArrayList<>();
    private final ReentrantLock mutexHealth = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> strength = new ArrayList<>();
    private final ReentrantLock mutexStrength = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> agility = new ArrayList<>();
    private final ReentrantLock mutexAgility = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> speedOfWalk = new ArrayList<>();
    private final ReentrantLock mutexSpeedOfWalk = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> speedOfRun = new ArrayList<>();
    private final ReentrantLock mutexSpeedOfRun = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> currentSpeed = new ArrayList<>();
    private final ReentrantLock mutexCurrentSpeed = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> hearing = new ArrayList<>();
    private final ReentrantLock mutexHearing = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> observation = new ArrayList<>();
    private final ReentrantLock mutexObservation = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> vision = new ArrayList<>();
    private final ReentrantLock mutexVision = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> loudness = new ArrayList<>();
    private final ReentrantLock mutexLoudness = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> fatigue = new ArrayList<>();
    private final ReentrantLock mutexFatigue = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> attention = new ArrayList<>();
    private final ReentrantLock mutexAttention = new ReentrantLock();
    private final List<ObjectsMemoryCell<Integer>> currentEnergyOutput = new ArrayList<>();
    private final ReentrantLock mutexCurrentEnergyOutput = new ReentrantLock();

    public ObjectsMemoryCell<Integer> getHealth(int i) {
        mutexHealth.lock();
        try {
            return health.get(i);
        } finally {
            mutexHealth.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getStrength(int i) {
        mutexStrength.lock();
        try {
            return strength.get(i);
        } finally {
            mutexStrength.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getAgility(int i) {
        mutexAgility.lock();
        try {
            return agility.get(i);
        } finally {
            mutexAgility.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getSpeedOfWalk(int i) {
        mutexSpeedOfWalk.lock();
        try {
            return speedOfWalk.get(i);
        } finally {
            mutexSpeedOfWalk.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getSpeedOfRun(int i) {
        mutexSpeedOfRun.lock();
        try {
            return speedOfRun.get(i);
        } finally {
            mutexSpeedOfRun.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getCurrentSpeed(int i) {
        mutexCurrentSpeed.lock();
        try {
            return currentSpeed.get(i);
        } finally {
            mutexCurrentSpeed.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getHearing(int i) {
        mutexHearing.lock();
        try {
            return hearing.get(i);
        } finally {
            mutexHearing.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getObservation(int i) {
        mutexObservation.lock();
        try {
            return observation.get(i);
        } finally {
            mutexObservation.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getVision(int i) {
        mutexVision.lock();
        try {
            return vision.get(i);
        } finally {
            mutexVision.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getLoudness(int i) {
        mutexLoudness.lock();
        try {
            return loudness.get(i);
        } finally {
            mutexLoudness.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getFatigue(int i) {
        mutexFatigue.lock();
        try {
            return fatigue.get(i);
        } finally {
            mutexFatigue.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getAttention(int i) {
        mutexAttention.lock();
        try {
            return attention.get(i);
        } finally {
            mutexAttention.unlock();
        }
    }
    
    public ObjectsMemoryCell<Integer> getCurrentEnergyOutput(int i) {
        mutexCurrentEnergyOutput.lock();
        try {
            return currentEnergyOutput.get(i);
        } finally {
            mutexCurrentEnergyOutput.unlock();
        }
    }
    
    public void addHealth(ObjectsMemoryCell<Integer> value) {
        mutexHealth.lock();
        try {
            health.add(value);
        } finally {
            mutexHealth.unlock();
        }
    }
    
    public void addStrength(ObjectsMemoryCell<Integer> value) {
        mutexStrength.lock();
        try {
            strength.add(value);
        } finally {
            mutexStrength.unlock();
        }
    }
    
    public void addAgility(ObjectsMemoryCell<Integer> value) {
        mutexAgility.lock();
        try {
            agility.add(value);
        } finally {
            mutexAgility.unlock();
        }
    }
    
    public void addSpeedOfWalk(ObjectsMemoryCell<Integer> value) {
        try {
            mutexSpeedOfWalk.lock();
            speedOfWalk.add(value);
        } finally {
            mutexSpeedOfWalk.unlock();
        }
    }
    
    public void addSpeedOfRun(ObjectsMemoryCell<Integer> value) {
        try {
            mutexSpeedOfRun.lock();
            speedOfRun.add(value);
        } finally {
            mutexSpeedOfRun.unlock();
        }
    }

    public void addCurrentSpeed(ObjectsMemoryCell<Integer> value) {
        mutexCurrentSpeed.lock();
        try {
            currentSpeed.add(value);
        } finally {
            mutexCurrentSpeed.unlock();
        }
    }
    
    public void addHearing(ObjectsMemoryCell<Integer> value) {
        mutexHearing.lock();
        try {
            hearing.add(value);
        } finally {
            mutexHearing.unlock();
        }
    }
    
    public void addObservation(ObjectsMemoryCell<Integer> value) {
        mutexObservation.lock();
        try {
            observation.add(value);
        } finally {
            mutexObservation.unlock();
        }
    }
    
    public void addVision(ObjectsMemoryCell<Integer> value) {
        mutexVision.lock();
        try {
            vision.add(value);
        } finally {
            mutexVision.unlock();
        }
    }
    
    public void addLoudness(ObjectsMemoryCell<Integer> value) {
        mutexLoudness.lock();
        try {
            loudness.add(value);
        } finally {
            mutexLoudness.unlock();
        }
    }
    
    public void addFatigue(ObjectsMemoryCell<Integer> value) {
        mutexFatigue.lock();
        try {
            fatigue.add(value);
        } finally {
            mutexFatigue.unlock();
        }
    }
    
    public void addAttention(ObjectsMemoryCell<Integer> value) {
        mutexAttention.lock();
        try {
            attention.add(value);
        } finally {
            mutexAttention.unlock();
        }
    }
    
    public void addCurrentEnergyOutput(ObjectsMemoryCell<Integer> value) {
        mutexCurrentEnergyOutput.lock();
        try {
            currentEnergyOutput.add(value);
        } finally {
            mutexCurrentEnergyOutput.unlock();
        }
    }
    

}
