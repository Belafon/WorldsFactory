package com.belafon.world.mobileClient.game.visibles.items;

import java.util.Set;

import com.belafon.world.mobileClient.game.behaviours.BehavioursRequirement;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import com.belafon.world.mobileClient.game.visibles.Visible;

public class Item extends Visible {
    private int id;
    private int weight;
    private int visiblity;
    private int toss;
    private String description;
    public Item(int id, String name, String description,
            int weight, int visiblity, int toss, Set<BehavioursRequirement> requirements) {
        super(requirements, name);
        this.id = id;
        this.weight = weight;
        this.visiblity = visiblity;
        this.toss = toss;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getToss() {
        return toss;
    }

    public void setToss(int toss) {
        this.toss = toss;
    }

    @Override
    public String getId() {
        return "" + id;
    }

    public int getNumId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getVisibility() {
        return visiblity;
    }

    public void setVisiblity(int visiblity) {
        this.visiblity = visiblity;
    }
    public static class Food extends Item {

        private int freshness;
        private int filling;
        private int warm;

        public Food(int id, String name, int weight,
                int visiblity, int toss, int freshness, int filling, int warm,
                Set<BehavioursRequirement> requirements) {
            super(id, name, getDescription(name), weight, visiblity, toss, requirements);
            this.freshness = freshness;
            this.filling = filling;
            this.warm = warm;
        }

        private static String getDescription(String name) {
            return "null"; // TODO do description has map
        }

        public int getFreshness() {
            return freshness;
        }

        public void setFreshness(int freshness) {
            this.freshness = freshness;
        }

        public int getFilling() {
            return filling;
        }

        public void setFilling(int filling) {
            this.filling = filling;
        }

        public int getWarm() {
            return warm;
        }

        public void setWarm(int warm) {
            this.warm = warm;
        }
    }

    @Override
    public Runnable getOnTitleClick() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'getOnTitleClick'");
        return () -> {};
    }

    @Override
    public String getVisibleType() {
        return "Item";
    }

    @Override
    public int compareTo(BehavioursPossibleIngredient behavioursPossibleIngredient) {
        return 0;
    }
}
