package com.belafon.world.visibles.creatures.behaviour;

import com.belafon.world.objectsMemory.Visible;

public record VisiblesID(Class<? extends Visible> type, int id) {
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        VisiblesID customKey = (VisiblesID) o;
        
        if (type == null
                || customKey.type == null)
            throw new NullPointerException("Type of visible is null");

        if (id != customKey.id
                || type != customKey.type)
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }
}
