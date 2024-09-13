package com.belafon.world.mobileClient.game.visibles.creatures;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.Stats;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import com.belafon.world.mobileClient.game.visibles.Visibles;
import com.belafon.world.mobileClient.game.visibles.VisiblesListFragment;
import com.belafon.world.mobileClient.game.visibles.items.Item;
import com.belafon.world.mobileClient.game.visibles.items.ItemInfoFragment;
import com.belafon.world.mobileClient.game.visibles.resources.Resource;
import com.belafon.world.mobileClient.game.visibles.resources.ResourceInfoFragment;


/**
 * list of visible creatures
 */
 public class CreaturesFragment extends VisiblesListFragment<CreaturesInfoFragment> {
    public CreaturesFragment(int fragmentContainerId, Visibles visibles, Fragment returnFragment) {
        super(fragmentContainerId, visibles, returnFragment);
        for(Creature creature : visibles.creatures.values())
            addVisiblesTitle(creature);
    }

    @Override
    protected void initialize(Visibles visibles) {
        synchronized (visibles.creatures){
            for (Creature creature : visibles.creatures.values()) {
                addVisiblesTitle(creature);
            }
        }
    }

    public void addVisiblesTitle(Creature creature) {
        AbstractActivity.getActualActivity().runOnUiThread(()-> {
            CreaturesInfoFragment visiblesFragment = new CreaturesInfoFragment(returnFragment, fragmentContainerId, creature);
            addVisiblesTitle(visiblesFragment, () -> this.selectVisible(visiblesFragment));
        });
    }

    /**
     * Changes the concrete fragment with detailed info about
     * one creature,
     * 
     * @param visiblesFragment
     */
    public void selectVisible(CreaturesInfoFragment visiblesFragment) {
        showVisiblesInfoFragment(visiblesFragment);
    }

    @Override
    protected void updateRemovedVisible(CreaturesInfoFragment infoFragment, BehavioursPossibleIngredient ingredient) {
        if(infoFragment.getVisible().equals(ingredient))
            infoFragment.goBack();

        infoFragment.updateIngredientRemoved(ingredient, infoFragment.getBehavioursList());
    }
}
