package com.belafon.world.mobileClient.game.visibles.items;

import android.os.Bundle;

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
import com.belafon.world.mobileClient.game.visibles.resources.Resource;

/**
 * list of visible items
 */
public class ItemFragment extends VisiblesListFragment<ItemInfoFragment> {
    public ItemFragment(int fragmentContainerId, Visibles visibles, Fragment returnFragment) {
        super(fragmentContainerId, visibles, returnFragment);
        for(Item item : visibles.items.values())
            addVisiblesTitle(item);
    }

    @Override
    protected void initialize(Visibles visibles) {
        synchronized (visibles.items){
            for (Item item : visibles.items.values()) {
                addVisiblesTitle(item);
            }
        }
    }

    public void addVisiblesTitle(Item item) {
        AbstractActivity.getActualActivity().runOnUiThread(() -> {
            ItemInfoFragment itemsInfoFragment = new ItemInfoFragment(returnFragment, fragmentContainerId, item);
            addVisiblesTitle(itemsInfoFragment, () -> this.selectVisible(itemsInfoFragment));
        });
    }

    /**
     * Changes the concrete fragment with detailed info about one item.
     *
     * @param itemsInfoFragment The ItemsInfoFragment to be selected.
     */
    public void selectVisible(ItemInfoFragment itemsInfoFragment) {
        showVisiblesInfoFragment(itemsInfoFragment);
    }

    @Override
    protected void updateRemovedVisible(ItemInfoFragment infoFragment, BehavioursPossibleIngredient ingredient) {
        if(infoFragment.getVisible().equals(ingredient))
            infoFragment.goBack();

        infoFragment.updateIngredientRemoved(ingredient, infoFragment.getBehavioursList());
    }

}