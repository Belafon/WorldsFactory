package com.belafon.world.mobileClient.game.visibles.resources;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.IPossibleBehavioursFragment;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.ListOfBehavioursForSetOfIngredients;
import com.belafon.world.mobileClient.game.visibles.VisiblesInfoFragment;
import com.belafon.world.mobileClient.game.Game;


public class ResourceInfoFragment extends VisiblesInfoFragment<Resource> implements IPossibleBehavioursFragment {

    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView idTextView;
    private TextView massTextView;


    public ResourceInfoFragment(Fragment previousFragment, int fragmentContainerId, Resource resource) {
        super(previousFragment, fragmentContainerId, resource);
    }

    ListOfBehavioursForSetOfIngredients behavioursFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_resource_info, container, false);

        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        nameTextView = rootView.findViewById(R.id.nameTextView);
        descriptionTextView = rootView.findViewById(R.id.descriptionTextView);
        idTextView = rootView.findViewById(R.id.idTextView);
        massTextView = rootView.findViewById(R.id.massTextView);
        behavioursFragment = setPossibleBehavioursFragment(R.id.behavioursFragmentContainer, visible, this);

        updateViews();

        return rootView;
    }

    private void updateViews() {
        Resource resource = getVisible();
        if (resource != null) {
            nameTextView.setText("Name: " + resource.getName());
            descriptionTextView.setText("Description: " + resource.getDescription());
            idTextView.setText("ID: " + resource.getId());
            massTextView.setText("Mass: " + resource.getMass());
        }
    }


    @Override
    public String getTitleText() {
        return getVisible().getName() + " " + getVisible().getId();
    }

    public ListOfBehavioursForSetOfIngredients getBehavioursList() {
        return behavioursFragment;
    }

}
