package com.belafon.world.mobileClient.game.visibles.items;

import java.util.HashSet;
import java.util.Set;

import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.inventory.ClothesablePartOfBody;

public class Clothes extends Item {
    public final ClothesablePartOfBody partOfBody;

    private Clothes(int id, String name, String description,
                   int weight, int visibility, int toss, Set<BehavioursRequirement> requirements,
                   ClothesablePartOfBody partOfBody) {
        super(id, name, description, weight, visibility, toss, requirements);
        this.partOfBody = partOfBody;
    }

    public boolean isClothesed() {
        return partOfBody.getClothes() == this;
    }

    public static class Builder {
        private int id;
        private String name;
        private String description;
        private int weight;
        private int visibility;
        private int toss;
        private Set<BehavioursRequirement> requirements = new HashSet<>();
        private ClothesablePartOfBody partOfBody;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder setVisibility(int visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder setToss(int toss) {
            this.toss = toss;
            return this;
        }

        public Builder addRequirement(BehavioursRequirement requirement) {
            this.requirements.add(requirement);
            return this;
        }

        public Builder setPartOfBody(ClothesablePartOfBody partOfBody) {
            this.partOfBody = partOfBody;
            return this;
        }

        public Clothes build() {
            // Validate that all required values are provided
            if (name == null || description == null || partOfBody == null) {
                throw new IllegalStateException("Required values not provided");
            }

            return new Clothes(id, name, description, weight, visibility, toss, requirements, partOfBody);
        }
    }
}
