package com.belafon.world.mobileClient.game.visibles.creatures;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.IPossibleBehavioursFragment;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.ListOfBehavioursForSetOfIngredients;
import com.belafon.world.mobileClient.game.visibles.VisiblesInfoFragment;


/**
 * A fragment, that shows information about a creature, that is visible by the creature, that is currently played by the player.
 */
public class CreaturesInfoFragment extends VisiblesInfoFragment<Creature> implements IPossibleBehavioursFragment {

    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView idTextView;


    public CreaturesInfoFragment(Fragment previousFragment, int fragmentContainerId, Creature creature) {
        super(previousFragment, fragmentContainerId, creature);
    }
    private ListOfBehavioursForSetOfIngredients behavioursFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creatures_info, container, false);

        nameTextView = rootView.findViewById(R.id.nameTextView);
        descriptionTextView = rootView.findViewById(R.id.descriptionTextView);
        idTextView = rootView.findViewById(R.id.idTextView);
        behavioursFragment = setPossibleBehavioursFragment(R.id.behavioursFragmentContainer, visible, this);

        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        updateViews();

        return rootView;
    }

    private void updateViews() {
        Creature creature = getVisible();
        if (creature != null) {
            nameTextView.setText("Name: " + creature.getName());
            descriptionTextView.setText("Description: " + creature.getLook());
            idTextView.setText("Id: " + creature.getId());
        }
    }

    @Override
    public String getTitleText() {
        return getVisible().name + " " + getVisible().getId();
    }

    public ListOfBehavioursForSetOfIngredients getBehavioursList() {
        return behavioursFragment;
    }
}
