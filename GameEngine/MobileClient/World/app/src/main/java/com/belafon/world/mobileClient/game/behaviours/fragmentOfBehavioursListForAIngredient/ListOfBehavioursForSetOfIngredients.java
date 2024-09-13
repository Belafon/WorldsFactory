package com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.Game;
import com.belafon.world.mobileClient.game.behaviours.Behaviour;
import com.belafon.world.mobileClient.game.behaviours.Behaviours;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fragment that holds a list of behaviours that are feasible 
 * and also that can be done in the support of concrete set of 
 * ingredients (all of the ingredients in the set has to be able to use
 * together).
 */
public class ListOfBehavioursForSetOfIngredients extends Fragment {
    private List<BehavioursPossibleIngredient> ingredients;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private final int fragmentContainer;
    private final Fragment containerFragment;
    private List<Behaviour> listedBehaviours;

    public ListOfBehavioursForSetOfIngredients(List<BehavioursPossibleIngredient> ingredients,
            int fragmentContainer, Fragment containerFragment) {
        this.ingredients = ingredients;
        this.fragmentContainer = fragmentContainer;
        this.containerFragment = containerFragment;
    }

    public ListOfBehavioursForSetOfIngredients(BehavioursPossibleIngredient ingredient,
            int fragmentContainer, Fragment containerFragment) {
        this.ingredients = new ArrayList<>();
        this.ingredients.add(ingredient);
        this.fragmentContainer = fragmentContainer;
        this.containerFragment = containerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_of_behaviours_for_set_of_ingredients, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view_behaviours);
        layoutManager = new LinearLayoutManager(getActivity());

        getFeasibleBehaviours();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void getFeasibleBehaviours() {
        Behaviours behaviours = Game.stats.behaviours;
        Set<Behaviour> feasibleBehaviours = new HashSet<>();

        feasibleBehaviours = ingredients.stream()
                .flatMap(ingredient -> ingredient.requirements.stream())
                .distinct()
                .flatMap(requirement -> behaviours.feasibles.stream()
                        .filter(behaviour -> behaviour.requirements.stream()
                                .anyMatch(other -> other.equals(requirement))))
                .collect(Collectors.toSet());

        if (listedBehaviours == null) {
            listedBehaviours = new ArrayList<>(feasibleBehaviours);
            mAdapter = new IngredientsAdapter.IngredientsAdapterBuilder()
                    .setPossibleBehavioursList(listedBehaviours)
                    .setFragmentContainer(fragmentContainer)
                    .setGoBackFragment(this)
                    .setIngredients(ingredients)
                    .setReplacingFragment(this)
                    .setContainerFragment(containerFragment)
                    .build();
        } else {
            listedBehaviours.clear();
            listedBehaviours.addAll(feasibleBehaviours);
        }
    }

    public synchronized void addIngredient(BehavioursPossibleIngredient ingredient) {
        ingredients.add(ingredient);
        getFeasibleBehaviours();
        mAdapter.notifyDataSetChanged();
    }

    public synchronized void removeIngredient(BehavioursPossibleIngredient ingredient) {
        ingredients.remove(ingredient);
        getFeasibleBehaviours();
        mAdapter.notifyDataSetChanged();
    }
}
