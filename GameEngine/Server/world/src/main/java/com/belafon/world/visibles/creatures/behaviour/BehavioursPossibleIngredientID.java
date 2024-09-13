package com.belafon.world.visibles.creatures.behaviour;

import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;

public record BehavioursPossibleIngredientID(Class<? extends BehavioursPossibleIngredient> type, String id) {
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BehavioursPossibleIngredientID customKey = (BehavioursPossibleIngredientID) o;
        
            if (type == null
                || customKey.type == null)
            throw new NullPointerException("Type of visible is null");

        if (!id.equals(customKey.id)
                || type != customKey.type)
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result
                + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return type.getSimpleName() + "|" + id;
    }
}
