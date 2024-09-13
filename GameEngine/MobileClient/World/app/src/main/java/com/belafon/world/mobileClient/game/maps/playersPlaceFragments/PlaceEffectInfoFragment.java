package com.belafon.world.mobileClient.game.maps.playersPlaceFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.IPossibleBehavioursFragment;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.ListOfBehavioursForSetOfIngredients;

public class PlaceEffectInfoFragment extends Fragment implements IPossibleBehavioursFragment {

    private Fragment previousFragment;
    private int fragmentContainerId;
    private PlayersPlaceEffect.PlaceEffectName name;
    private String look;
    private PlayersPlaceEffect effect;

    public PlaceEffectInfoFragment(
            Fragment previousFragment,
            int fragmentContainerId,
            PlayersPlaceEffect.PlaceEffectName name,
            String look,
            PlayersPlaceEffect effect) {

        this.previousFragment = previousFragment;
        this.fragmentContainerId = fragmentContainerId;
        this.name = name;
        this.look = look;
        this.effect = effect;
    }

    private ListOfBehavioursForSetOfIngredients behavioursFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_place_effect_info, container, false);
        behavioursFragment = setPossibleBehavioursFragment(R.id.behavioursFragmentContainer, effect, this);

        TextView nameLabel = rootView.findViewById(R.id.nameLabel);
        nameLabel.setText(name.toString());
        nameLabel.setTypeface(nameLabel.getTypeface(), Typeface.BOLD);

        TextView lookLabel = rootView.findViewById(R.id.lookLabel);
        lookLabel.setText(look);

        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        return rootView;
    }

    private void goBack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerId, previousFragment);
        fragmentTransaction.commit();
    }

    public ListOfBehavioursForSetOfIngredients getBehavioursList() {
        return behavioursFragment;
    }
}
