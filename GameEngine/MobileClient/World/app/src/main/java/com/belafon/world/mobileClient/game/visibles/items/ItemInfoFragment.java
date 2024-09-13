package com.belafon.world.mobileClient.game.visibles.items;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.IPossibleBehavioursFragment;
import com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient.ListOfBehavioursForSetOfIngredients;
import com.belafon.world.mobileClient.game.visibles.VisiblesInfoFragment;

public class ItemInfoFragment extends VisiblesInfoFragment<Item> implements IPossibleBehavioursFragment {

    private TextView titleTextView;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView idTextView;
    private TextView weightTextView;
    private TextView visibilityTextView;
    private TextView tossTextView;

    public ItemInfoFragment(Fragment previousFragment, int fragmentContainerId, Item item) {
        super(previousFragment, fragmentContainerId, item);
    }
    private ListOfBehavioursForSetOfIngredients behavioursFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_info, container, false);

        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener((View v) -> goBack());

        titleTextView = rootView.findViewById(R.id.titleTextView);
        nameTextView = rootView.findViewById(R.id.nameTextView);
        descriptionTextView = rootView.findViewById(R.id.descriptionTextView);
        idTextView = rootView.findViewById(R.id.idTextView);
        weightTextView = rootView.findViewById(R.id.weightTextView);
        visibilityTextView = rootView.findViewById(R.id.visibilityTextView);
        tossTextView = rootView.findViewById(R.id.tossTextView);
        behavioursFragment = setPossibleBehavioursFragment(R.id.behavioursFragmentContainer, visible, this);

        updateViews();

        return rootView;
    }

    private void updateViews() {
        Item item = getVisible();
        if (item != null) {
            titleTextView.setText("Info about item");
            nameTextView.setText("Name: " + item.getName());
            descriptionTextView.setText("Description: " + item.getDescription());
            idTextView.setText("ID: " + item.getId());
            weightTextView.setText("Weight: " + item.getWeight());
            visibilityTextView.setText("Visibility: " + item.getVisibility());
            tossTextView.setText("Toss: " + item.getToss());
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
