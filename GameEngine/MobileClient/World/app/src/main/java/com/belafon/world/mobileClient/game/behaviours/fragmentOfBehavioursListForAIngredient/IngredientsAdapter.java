package com.belafon.world.mobileClient.game.behaviours.fragmentOfBehavioursListForAIngredient;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.Screen;
import com.belafon.world.mobileClient.game.behaviours.Behaviour;
import com.belafon.world.mobileClient.game.behaviours.BehavioursExecutorFragment;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    private List<Behaviour> possibleBehavioursList;
    private static final int ITEM_PADDING_SIZE_IN_PX = Screen.dpToPixels(8);
    private final int fragmentContainer;
    private final Fragment goBackFragment;
    private final List<BehavioursPossibleIngredient> ingredients;
    private final Fragment replacingFragment;
    private final Fragment containerFragment;

    private IngredientsAdapter(List<Behaviour> possibleBehavioursList, int fragmentContainer,
                              Fragment goBackFragment, List<BehavioursPossibleIngredient> ingredients, Fragment replacingFragment, Fragment containerFragment) {
        this.ingredients = ingredients;
        this.possibleBehavioursList = possibleBehavioursList;
        this.fragmentContainer = fragmentContainer;
        this.goBackFragment = goBackFragment;
        this.replacingFragment = replacingFragment;
        this.containerFragment = containerFragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_behaviours_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Behaviour behaviour = possibleBehavioursList.get(position);
        holder.textView.setText(behaviour.toString());
        holder.textView.setTextColor(Color.WHITE);
        holder.textView.setBackgroundResource(R.color.themeTransparent);
        int padding = Screen.dpToPixels(ITEM_PADDING_SIZE_IN_PX);
        holder.textView.setPadding(padding, padding, padding, padding);
        holder.itemView.setOnClickListener(view -> {
            // we need to display the fragment in the container
            BehavioursExecutorFragment fragment = new BehavioursExecutorFragment(fragmentContainer,
                    goBackFragment, behaviour, ingredients);

            containerFragment.getChildFragmentManager()
                    .beginTransaction()
                    .replace(fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return possibleBehavioursList.size();
    }

    public static class IngredientsAdapterBuilder {
        private List<Behaviour> possibleBehavioursList;
        private int fragmentContainer;
        private Fragment goBackFragment;
        private List<BehavioursPossibleIngredient> ingredients;
        private Fragment replacingFragment;
        private Fragment containerFragment;

        public IngredientsAdapterBuilder setPossibleBehavioursList(List<Behaviour> possibleBehavioursList) {
            this.possibleBehavioursList = possibleBehavioursList;
            return this;
        }

        public IngredientsAdapterBuilder setFragmentContainer(int fragmentContainer) {
            this.fragmentContainer = fragmentContainer;
            return this;
        }

        public IngredientsAdapterBuilder setGoBackFragment(Fragment goBackFragment) {
            this.goBackFragment = goBackFragment;
            return this;
        }

        public IngredientsAdapterBuilder setIngredients(List<BehavioursPossibleIngredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public IngredientsAdapterBuilder setReplacingFragment(Fragment replacingFragment) {
            this.replacingFragment = replacingFragment;
            return this;
        }

        public IngredientsAdapterBuilder setContainerFragment(Fragment containerFragment) {
            this.containerFragment = containerFragment;
            return this;
        }
        public IngredientsAdapter build() {
            // Validate that all required values are provided
            if (possibleBehavioursList == null || ingredients == null
                    || goBackFragment == null || replacingFragment == null) {
                throw new IllegalStateException("Required values not provided");
            }

            return new IngredientsAdapter(possibleBehavioursList, fragmentContainer, goBackFragment, ingredients, replacingFragment, containerFragment);
        }


    }

}
