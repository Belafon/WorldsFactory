package com.belafon.world.mobileClient.game.visibles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.Stats;
import com.belafon.world.mobileClient.game.visibles.creatures.CreaturesFragment;
import com.belafon.world.mobileClient.game.visibles.items.ItemFragment;
import com.belafon.world.mobileClient.game.visibles.resources.ResourcesFragment;

/** 
 * Main fragment that holds all information about visibles.
 * It contains three lists of visibles: items, creatures and resources.
 * It is directly reachable from the main menu of the game.
 */
public class VisiblesFragment extends Fragment {
    public ItemFragment items;
    public CreaturesFragment creatures;
    public ResourcesFragment resources;

    public VisiblesFragment(Visibles visibles) {
        // Initialize child fragments with their corresponding fragment container IDs
        items = new ItemFragment(R.id.game_fragment, visibles, this);
        creatures = new CreaturesFragment(R.id.game_fragment, visibles, this);
        resources = new ResourcesFragment(R.id.game_fragment, visibles, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_visibles, container, false);

        FragmentManager childFragmentManager = getChildFragmentManager();

        FragmentTransaction itemsTransaction = childFragmentManager.beginTransaction();
        itemsTransaction.replace(R.id.items, items);
        itemsTransaction.commit();

        FragmentTransaction creaturesTransaction = childFragmentManager.beginTransaction();
        creaturesTransaction.replace(R.id.creatures, creatures);
        creaturesTransaction.commit();

        FragmentTransaction resourcesTransaction = childFragmentManager.beginTransaction();
        resourcesTransaction.replace(R.id.resources, resources);
        resourcesTransaction.commit();

        return rootView;
    }
}
