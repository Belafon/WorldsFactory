package com.belafon.world.mobileClient.gameActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.belafon.world.mobileClient.R;

/**
 * Main menu navigation bar in the game.
 */
public class MainMenuFragment extends Fragment {
    private GameActivity gameActivity;
    

    public MainMenuFragment(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        ImageButton characterButton = view.findViewById(R.id.character);
        characterButton.setOnClickListener(v -> onCharacter());

        ImageButton visiblesButton = view.findViewById(R.id.visibles);
        visiblesButton.setOnClickListener(v -> onVisibles());


        ImageButton inventoryButton = view.findViewById(R.id.inventory);
        inventoryButton.setOnClickListener(v -> onInventory());

        ImageButton behaviourButton = view.findViewById(R.id.behaviour);
        behaviourButton.setOnClickListener(v -> onBehaviour());
          
        ImageButton mapButton = view.findViewById(R.id.map);
        mapButton.setOnClickListener(v -> onMap());
         
        ImageButton viewButton = view.findViewById(R.id.view);
        viewButton.setOnClickListener(v -> onView());

        ImageButton storyButton = view.findViewById(R.id.story);
        storyButton.setOnClickListener(v -> onStory());

        return view;
    }

    private void onVisibles() {
        gameActivity.openFragment(gameActivity.game.fragments.visibles);
    }

    public void onCharacter() {
        gameActivity.openFragment(gameActivity.game.fragments.bodyStatistics);
    }

    public void onInventory() {
        gameActivity.openFragment(gameActivity.game.fragments.inventory);
    }

    public void onBehaviour() {
        gameActivity.openFragment(gameActivity.game.fragments.behaviours);
    }

    public void onMap() {
        gameActivity.openFragment(gameActivity.game.fragments.surroundingPlaces);
    }

    public void onView() {
        gameActivity.openFragment(gameActivity.game.fragments.view);
    }

    public void onStory() {
        gameActivity.openFragment(gameActivity.game.fragments.story);
    }
}
