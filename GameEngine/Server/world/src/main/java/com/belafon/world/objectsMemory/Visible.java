package com.belafon.world.objectsMemory;

import java.util.HashSet;
import java.util.Set;

import com.belafon.world.maps.place.UnboundedPlace;
import com.belafon.world.visibles.creatures.Creature;
import com.belafon.world.visibles.creatures.behaviour.BehavioursPossibleIngredientID;
import com.belafon.world.visibles.creatures.behaviour.VisiblesID;
import com.belafon.world.visibles.creatures.behaviour.behaviours.BehavioursPossibleIngredient;

public abstract class Visible implements BehavioursPossibleIngredient {
    public abstract UnboundedPlace getLocation();

    protected abstract void setLocation(UnboundedPlace place);

    private Set<Creature> watchers = new HashSet<Creature>();

    @FunctionalInterface
    public static interface ActionWatcher {
        public void doJob(Set<Creature> watchers);
    }

    /**
     * Unables refactoring and spectating all
     * watcher of the visible.
     * 
     * @param action
     */
    public void getWatchers(ActionWatcher action) {
        synchronized (watchers) {
            action.doJob(watchers);
        }
    }

    /**
     * Adds new watcher into set of all watchers of the visible.
     * 
     * @param creature
     */
    public void addWatcher(Creature creature) {
        synchronized (watchers) {
            watchers.add(creature);
        }
    }

    /**
     * Removes concrete watcher.
     * It means that, the creature cannot see the
     * visible no longer.
     * 
     * @return Returns true, if the watcher was found and removed.
     *         Returns false, if the watcher was not found.
     */
    public boolean removeWatcher(Creature creature) {
        synchronized (watchers) {
            return watchers.remove(creature);
        }
    }

    /**
     * @return Returns a rate of visibility.
     *         How much is it visible in compare to other visibles.
     */
    public abstract int getVisibility();

    public abstract Class<? extends Visible> getClassType();

    public VisiblesID getId() {
        return new VisiblesID(getClassType(), getIdNumber());
    }

    protected abstract int getIdNumber();

    public void setLocationAndUpdateWatchers(UnboundedPlace position) {

        // send info about the change to all watchers
        this.getWatchers((watchers) -> {
            for (Creature watcher : watchers) {
                watcher.removeVisibleObjectWithoutRemovingFromWatchers_BeforeLocationOfTheVisibleChanges(this);
            }
            watchers.clear();
        });

        setLocation(position);

        /*
         * this.getWatchers((watchers) -> {
         * for (Creature watcher : watchers) {
         * watcher.
         * removeVisibleObjectWithoutRemovingFromWatchers_BeforeLocationOfTheVisibleChanges
         * (this);
         * }
         * watchers.clear();
         * });
         */
    }

    @Override
    public BehavioursPossibleIngredientID getBehavioursPossibleIngredientID() {
        return new BehavioursPossibleIngredientID(getClassType(), getIdNumber() + "");
    }
}
