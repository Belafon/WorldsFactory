package com.belafon.world.mobileClient.game.behaviours;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.belafon.world.mobileClient.client.Client;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

/**
 * If the behaviour is in feasible behaviours, 
 * then it can be executed.
 */
public class Behaviour {
    public final String messagesName;
    public final String name;

    public Set<BehavioursRequirementDetail> requirements;
    public final String description;
    private int duration = -1;

    private Behaviour(String messagesName, String name, String description,
            Set<BehavioursRequirementDetail> requirements) {
        this.messagesName = messagesName;
        this.name = name;
        this.description = description;
        this.requirements = Collections.unmodifiableSet(requirements);
    }

    public static class BehaviourBuilder {
        private final String messagesName;
        private String name;
        public String description;

        private Set<BehavioursRequirementDetail> requirements = new HashSet<>();

        public BehaviourBuilder(String messagesName) {
            this.messagesName = messagesName;
        }

        public BehaviourBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public BehaviourBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        /*
         * public BehaviourBuilder addRequirement(
         * BehavioursRequirementDetail requirement) {
         * requirements.add(requirement);
         * return this;
         * }
         */
        public Behaviour build() {
            if (description == null)
                throw new Error("Some behaviour does not have description.");

            if (name == null)
                throw new Error("Some behaviour does not have a name.");
            return new Behaviour(messagesName, name, description, requirements);
        }

        public BehaviourBuilder addRequirement(
                BehavioursRequirement behavioursRequirement,
                String description,
                int numOfConcreteIngredient,
                int numOfGeneralIngredient) {
            requirements.add(new BehavioursRequirementDetail(
                    behavioursRequirement,
                    description,
                    numOfConcreteIngredient,
                    numOfGeneralIngredient));
            return this;
        }
    }

    /**
     * Requirement extended by number of ingredients that are needed.
     */
    public static class BehavioursRequirementDetail {
        public BehavioursRequirement requirement;
        public final String description;
        public final int numOfConcreteIngredient;
        public final int numOfGeneralIngredient;

        public BehavioursRequirementDetail(BehavioursRequirement requirement, String description,
                int numOfConcreteIngredient, int numOfGeneralIngredient) {
            if(requirement == null)
                throw new NullPointerException("The requirement is null in constructor");

            this.requirement = requirement;
            this.description = description;
            this.numOfConcreteIngredient = numOfConcreteIngredient;
            this.numOfGeneralIngredient = numOfGeneralIngredient;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BehavioursRequirement other) {
                return requirement.equals(other);
            }
            return false;
        }
    }

    public void execute(List<BehavioursPossibleIngredient> selectedIngredients) {
        // we have to send the message to the server
        Client.sender.behaviours.executeBehaviour(selectedIngredients, this);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getDuration() {
        return duration;
    }

    // write to string method
    @Override
    public String toString() {
        return name;
    }
}
