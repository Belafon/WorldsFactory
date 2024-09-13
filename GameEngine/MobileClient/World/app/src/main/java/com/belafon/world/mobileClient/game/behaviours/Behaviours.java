package com.belafon.world.mobileClient.game.behaviours;

import android.util.Log;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.game.Fragments;
import com.belafon.world.mobileClient.game.Stats;
import com.belafon.world.mobileClient.game.behaviours.Behaviour.BehaviourBuilder;

/**
 * Handles messages from the server about behaviours,
 * like setup new behaviour, or requirement.
 * 
 * Holds information about feasible behaviours and 
 * currently being carried out.
 */
public class Behaviours {
    private static final String TAG = "Behaviours";
    public final Map<String, Behaviour> allBehaviors = new Hashtable<>();
    public final Map<String, BehavioursRequirement> allRequirements = new Hashtable<>();
    public final Set<Behaviour> feasibles = new HashSet<>();
    public BehavioursFragment listFragment;
    private Behaviour currentBehaviour = null;

    public BehavioursFragment getNewBehaviourListFragment(int fragmentContainerId) {
        listFragment = new BehavioursFragment(fragmentContainerId, feasibles);
        return listFragment;
    }

    public void addNewFeasibleBehaviour(String behavioursName) {
        var behav = allBehaviors.get(behavioursName);
        if (behav == null)
            throw new IllegalArgumentException(
                    "Behaviours: addNewFeasibleBehaviour: behaviour name is unknown: " + behavioursName);
        feasibles.add(behav);
        if(listFragment != null)
            AbstractActivity.getActualActivity().runOnUiThread(() -> {
                listFragment.addItem(behav);
            });
    }

    public void removeFeasibleBehaviour(String behavioursName) {
        var behav = allBehaviors.get(behavioursName);
        if (behav == null)
            throw new IllegalArgumentException(
                    "Behaviours: removeFeasibleBehaviour: behaviour name is unknown: " + behavioursName);
        feasibles.remove(behav);

        if(listFragment != null)
            AbstractActivity.getActualActivity().runOnUiThread(() -> {
                listFragment.removeItem(behav);
            });
    }

    /**
     * Handles message from the server.
     */
    public void setupNewBehaviour(String[] args) {
        String idName = args[2];
        String name = args[3].replaceAll("_", " ");
        String description = args[4].replaceAll("_", " ");
        String[] requirementNames;
        if (args.length < 6)
            requirementNames = new String[0];
        else
            requirementNames = args[5].split(",");

        var behaviour = new BehaviourBuilder(idName);
        behaviour.setDescription(description);
        behaviour.setName(name);
        for (String requirementsName : requirementNames) {
            String[] detailedRequir = requirementsName.split("[|]");

            if(!allRequirements.containsKey(detailedRequir[0])){
                Log.e(TAG, "setupNewBehaviour: NOT SUCH BEHAVIOURS REQUIREMENT");
                return;
            }

            behaviour.addRequirement(
                    allRequirements.get(detailedRequir[0]), // id name
                    detailedRequir[1], // description
                    Integer.parseInt(detailedRequir[2]), // number of specific ingredients
                    Integer.parseInt(detailedRequir[3])); // number of general ingredients

        }
        allBehaviors.put(idName, behaviour.build());
    }

    public void setUpNewRequirement(String[] args) {
        String idName = args[2];
        String name = args[3].replaceAll("_", " ");
        allRequirements.put(idName, new BehavioursRequirement(idName, name));
    }

    public void doBehaviour(String[] args, Stats stats, Fragments fragments) {
        if (currentBehaviour != null)
            currentBehaviour.setDuration(-1);
        if (args[2].equals("null")) {
            currentBehaviour = null;
            return;
        }

        var behaviour = allBehaviors.get(args[2]);
        var duration = Integer.parseInt(args[3]);

        if (behaviour == null)
            throw new IllegalArgumentException("Behaviours: doBehaviour: behaviour name is unknown: " + args[3]);

        if (duration != 0)
            currentBehaviour = behaviour;

        behaviour.setDuration(duration);
    }

    public Behaviour getCurrentBehaviour() {
        return currentBehaviour;
    }
}