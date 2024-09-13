package com.belafon.world.mobileClient.game.visibles.resources;

import android.content.res.Resources;

import androidx.fragment.app.Fragment;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.game.Stats;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import com.belafon.world.mobileClient.game.visibles.Visibles;
import com.belafon.world.mobileClient.game.visibles.VisiblesListFragment;
import com.belafon.world.mobileClient.game.visibles.items.Item;

public class ResourcesFragment extends VisiblesListFragment<ResourceInfoFragment> {
    public ResourcesFragment(int fragmentContainerId, Visibles visibles, Fragment returnFragment) {
        super(fragmentContainerId, visibles, returnFragment);
        for(Resource resource : visibles.resources.values())
            addVisiblesTitle(resource);
    }

    @Override
    protected void initialize(Visibles visibles) {
        synchronized (visibles.resources){
            for (Resource resource : visibles.resources.values()) {
                addVisiblesTitle(resource);
            }
        }
    }

    public void addVisiblesTitle(Resource resource) {
        AbstractActivity.getActualActivity().runOnUiThread(() -> {
            ResourceInfoFragment resourcesInfoFragment = new ResourceInfoFragment(returnFragment, fragmentContainerId, resource);
            addVisiblesTitle(resourcesInfoFragment, () -> this.selectVisible(resourcesInfoFragment));
        });
    }

    /**
     * Changes the concrete fragment with detailed info about one resource.
     *
     * @param resourcesInfoFragment The ResourcesInfoFragment to be selected.
     */
    public void selectVisible(ResourceInfoFragment resourcesInfoFragment) {
        showVisiblesInfoFragment(resourcesInfoFragment);
    }

    @Override
    protected void updateRemovedVisible(ResourceInfoFragment infoFragment, BehavioursPossibleIngredient ingredient) {
        if(infoFragment.getVisible().equals(ingredient))
            infoFragment.goBack();

        infoFragment.updateIngredientRemoved(ingredient, infoFragment.behavioursFragment);
    }

}
