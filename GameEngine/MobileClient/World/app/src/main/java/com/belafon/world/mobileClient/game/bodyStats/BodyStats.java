package com.belafon.world.mobileClient.game.bodyStats;


import com.belafon.world.mobileClient.game.Fragments;

import java.util.ArrayList;
import java.util.List;

public class BodyStats {
    public List<BodyStat> abilityList;
    public List<BodyStat> actualList;

    // Create the ability list
    BodyStat healthAbility;
    BodyStat strengthAbility;
    BodyStat agilityAbility;
    BodyStat speedOfWalkAbility;
    BodyStat speedOfRunAbility;
    BodyStat currentSpeedAbility;
    BodyStat hearingAbility;
    BodyStat observationAbility;
    BodyStat visionAbility;
    BodyStat loudnessAbility;
    BodyStat attentionAbility;
    BodyStat energyOutputAbility;

    // Create the actual list
    BodyStat hunger;
    BodyStat fatigueMax;
    BodyStat heat;
    BodyStat bleeding;
    
    public void setAdapters(CreatureStatisticsFragment statFrag) {
        healthAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        strengthAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        agilityAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        speedOfWalkAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        speedOfRunAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        currentSpeedAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        hearingAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        observationAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        visionAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        loudnessAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        attentionAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);
        energyOutputAbility.setListAdapter(statFrag.getAbilityAdapter(), statFrag);

        hunger.setListAdapter(statFrag.getActualAdapter(), statFrag);
        fatigueMax.setListAdapter(statFrag.getActualAdapter(), statFrag);
        heat.setListAdapter(statFrag.getActualAdapter(), statFrag);
        bleeding.setListAdapter(statFrag.getActualAdapter(), statFrag);
    }
    
    /**
     * initializes all stats and sets uninitialized values to 0
     */
    public BodyStats() {
        this.healthAbility = new BodyStat("Health", 0);
        this.strengthAbility = new BodyStat("Strength", 0);
        this.agilityAbility = new BodyStat("Agility", 0);
        this.speedOfWalkAbility = new BodyStat("Walk Speed", 0);
        this.speedOfRunAbility = new BodyStat("Run Speed", 0);
        this.currentSpeedAbility = new BodyStat("Current Speed", 0);
        this.hearingAbility = new BodyStat("Hearing", 0);
        this.observationAbility = new BodyStat("Observation", 0);
        this.visionAbility = new BodyStat("Vision", 0);
        this.loudnessAbility = new BodyStat("Loudness", 0);
        this.attentionAbility = new BodyStat("Attention", 0);
        this.energyOutputAbility = new BodyStat("Energy Output", 0);
        

        // Create the actual list
        this.hunger = new BodyStat("Hunger", 0);
        this.fatigueMax = new BodyStat("Fatigue Max", 0);
        this.heat = new BodyStat("Heat", 0);
        this.bleeding = new BodyStat("Bleeding", 0);

        setStatsLists();
    }

    

    private void setStatsLists() {
        abilityList = new ArrayList<>();
        abilityList.add(healthAbility);
        abilityList.add(strengthAbility);
        abilityList.add(agilityAbility);
        abilityList.add(speedOfWalkAbility);
        abilityList.add(speedOfRunAbility);
        abilityList.add(currentSpeedAbility);
        abilityList.add(hearingAbility);
        abilityList.add(observationAbility);
        abilityList.add(visionAbility);
        abilityList.add(loudnessAbility);
        abilityList.add(attentionAbility);
        abilityList.add(energyOutputAbility);

        actualList = new ArrayList<>();
        actualList.add(hunger);
        actualList.add(fatigueMax);
        actualList.add(heat);
        actualList.add(bleeding);
    }

    public void setHealth(String healthAbility, Fragments fragments) throws NumberFormatException {
        this.healthAbility.setValue(healthAbility);
    }

    public void setStrengthAbility(String strengthAbility, Fragments fragments) throws NumberFormatException {
        this.strengthAbility.setValue(strengthAbility);
    }

    public void setAgilityAbility(String agilityAbility, Fragments fragments) throws NumberFormatException {
        this.agilityAbility.setValue(agilityAbility);
    }

    public void setSpeedOfWalkAbility(String speedOfWalkAbility, Fragments fragments) throws NumberFormatException {
        this.speedOfWalkAbility.setValue(speedOfWalkAbility);
    }

    public void setSpeedOfRunAbility(String speedOfRunAbility, Fragments fragments) throws NumberFormatException {
        this.speedOfRunAbility.setValue(speedOfRunAbility);
    }

    public void setCurrentSpeedAbility(String currentSpeedAbility, Fragments fragments) throws NumberFormatException {
        this.currentSpeedAbility.setValue(currentSpeedAbility);
    }

    public void setHearingAbility(String hearingAbility, Fragments fragments) throws NumberFormatException {
        this.hearingAbility.setValue(hearingAbility);
    }

    public void setObservationAbility(String observationAbility, Fragments fragments) throws NumberFormatException {
        this.observationAbility.setValue(observationAbility);
    }

    public void setVisionAbility(String visionAbility, Fragments fragments) throws NumberFormatException {
        this.visionAbility.setValue(visionAbility);
    }

    public void setLoudnessAbility(String loudnessAbility, Fragments fragments) throws NumberFormatException {
        this.loudnessAbility.setValue(loudnessAbility);

    }

    public void setAttentionAbility(String attentionAbility, Fragments fragments) throws NumberFormatException {
        this.attentionAbility.setValue(attentionAbility);

    }

    public void setEnergyOutputAbility(String energyOutputAbility, Fragments fragments) throws NumberFormatException {
        this.energyOutputAbility.setValue(energyOutputAbility);

    }

    public void setHunger(String hungerAbility, Fragments fragments) throws NumberFormatException {
        this.hunger.setValue(hungerAbility);
    }

    public void setBleeding(String bleedingAbility, Fragments fragments) throws NumberFormatException {
        this.bleeding.setValue(bleedingAbility);

    }

    public void setHeat(String heatAbility, Fragments fragments) throws NumberFormatException {
        this.heat.setValue(heatAbility);
    }

    public void setFatigueMax(String fatigueMaxAbility, Fragments fragments) throws NumberFormatException {
        this.fatigueMax.setValue(fatigueMaxAbility);
    }


    public void setCurrentEnergyOutput(String currentEnergyOutputAbility, Fragments fragments) throws NumberFormatException {
        this.energyOutputAbility.setValue(currentEnergyOutputAbility);
    }
}
