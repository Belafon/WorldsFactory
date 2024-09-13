package com.belafon.world.mobileClient.game.maps.playersPlaceFragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.IPossibleBehavioursFragment;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.ListOfBehavioursForSetOfIngredients;
import com.belafon.world.mobileClient.game.maps.Place;
import com.belafon.world.mobileClient.game.maps.SurroundingPlacesFragment;

import java.util.List;

public class PlaceInfoFragment extends Fragment implements IPossibleBehavioursFragment {
    private Fragment previousFragment;
    private int fragmentContainerId;
    private String name;
    private String look;
    private List<PlayersPlaceEffect> placeEffects;
    private Place place;
    public PlaceInfoFragment(
            Fragment previousFragment,
            int fragmentContainerId,
            String name,
            String look,
            List<PlayersPlaceEffect> placeEffects,
            Place place) {

        this.previousFragment = previousFragment;
        this.fragmentContainerId = fragmentContainerId;
        this.name = name;
        this.look = look;
        this.placeEffects = placeEffects;
        this.place = place;
    }

    private ListOfBehavioursForSetOfIngredients behavioursFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ConstraintLayout rootView = (ConstraintLayout) inflater.inflate(R.layout.fragment_place_info, container, false);

        behavioursFragment = setPossibleBehavioursFragment(R.id.behavioursFragmentContainer, place, this);

        TextView nameLabel = rootView.findViewById(R.id.nameLabel);
        nameLabel.setText(name.toString());
        nameLabel.setTypeface(nameLabel.getTypeface(), Typeface.BOLD);

        TextView lookLabel = rootView.findViewById(R.id.descriptionLabel);
        lookLabel.setText(look);

        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener((View v) -> goBack());

        // get list of buttons of each place effects
        // for each place effect add button to the list
        LinearLayout placeEffectsList = rootView.findViewById(R.id.placeEffectsList);
        for (PlayersPlaceEffect effect : placeEffects) {
            Button placeEffectButton = new Button(this.getContext());
            placeEffectButton.setText(effect.name.name());
            placeEffectButton.setOnClickListener((View v) -> displayPlaceEffectInfo(effect));
            placeEffectsList.addView(placeEffectButton);
        }

        return rootView;
    }

    private void goBack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerId, previousFragment);
        fragmentTransaction.commit();
    }

    private void displayPlaceEffectInfo(PlayersPlaceEffect effect) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerId, effect.getInfoFragment(this, fragmentContainerId));
        fragmentTransaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(previousFragment instanceof SurroundingPlacesFragment surroundingPlacesFragment){
            surroundingPlacesFragment.activeState = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(previousFragment instanceof SurroundingPlacesFragment surroundingPlacesFragment){
            surroundingPlacesFragment.activeState = true;
        }
    }

    public ListOfBehavioursForSetOfIngredients getBehavioursList() {
        return behavioursFragment;
    }

}