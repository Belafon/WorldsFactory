package com.belafon.world.mobileClient.game.maps;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlaceInfoFragment;
import com.belafon.world.mobileClient.game.maps.playersPlaceFragments.PlacePanel;


/**
 * A fragment, that shows a map of the surrounding places, that are visible
 * by the player.
 */
public class SurroundingPlacesFragment extends Fragment {
    private static final int SIZE_OF_GAP_IN_PIXELS = 15;
    private static final int SIZE_OF_BUTTON_IN_PIXELS = 100;

    private SurroundingMap map;
    private int fragmentContainerId;
    private Fragment previousFragment;
    public volatile boolean activeState = false;

    public SurroundingPlacesFragment(SurroundingMap map, int fragmentContainerId, Fragment previousFragment) {
        this.map = map;
        this.fragmentContainerId = fragmentContainerId;
        this.previousFragment = previousFragment;
    }

    private LinearLayout rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = (LinearLayout) inflater.inflate(R.layout.fragment_surrounding_places, container, false);
        drawMap(rootView);
        activeState = true;
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        activeState = false;
    }

    private void drawMap(LinearLayout rootView) {
        GridLayout gridLayout = rootView.findViewById(R.id.gridLayout);
        gridLayout.removeAllViews();

        for (int x = 0; x < SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS; x++) {
            for (int y = 0; y < SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS; y++) {
                Button placeButton = new Button(getContext());

                // set buttons size, color, margins...
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.height = SIZE_OF_BUTTON_IN_PIXELS;
                layoutParams.width = SIZE_OF_BUTTON_IN_PIXELS;
                layoutParams.setMargins(SIZE_OF_GAP_IN_PIXELS, SIZE_OF_GAP_IN_PIXELS, 0, 0); // Set margins for the gap
                placeButton.setLayoutParams(layoutParams);

                int color;
                if (map.getPlaceFragment(x, y) == null) {
                    color = Place.UNKNOWN.typePlace.backgroundColor;
                    placeButton.setBackgroundColor(color);
                    placeButton.setOnClickListener(new PlaceButtonClickListener(x, y, Place.UNKNOWN, this));
                } else {
                    color = this.map.getPlaceFragment(x, y).typePlace.backgroundColor;
                    placeButton.setBackgroundColor(color);
                    placeButton.setOnClickListener(new PlaceButtonClickListener(x, y, map.getPlaceFragment(x, y), this));
                }

                if(x == SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS / 2
                        && y == SurroundingMap.NUMBER_OF_PLACES_IN_SIGHT_IN_ONE_AXIS / 2)
                    setPlaceButtonAsPlayersPosition(placeButton, color);

                gridLayout.addView(placeButton);

            }
        }

    }

    private void setPlaceButtonAsPlayersPosition(Button placeButton, int color) {
        Drawable borderDrawable = AbstractActivity.getActualActivity().getDrawable(R.drawable.players_position_place_button);
        placeButton.setBackground(borderDrawable);
        placeButton.getBackground().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
    }

    /**
     * redraws the map
     */
    public void update() {
            if(rootView != null){

                if(!this.isAdded()){
                    if(AbstractActivity.getActualActivity().findViewById(fragmentContainerId) == null)
                        return;

                    AbstractActivity.getActualActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(fragmentContainerId, this)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                }

                drawMap(rootView);
            }
    }

    /**
     * sets concrete place in the map as a unknown
     */
    public void updateRemovePlace(Place place) {
        if(!activeState)
            return;

        AbstractActivity.getActualActivity().runOnUiThread(() -> {

            if(rootView != null && this.isAdded()
                    && place instanceof PlacePanel placePanel){

                GridLayout gridLayout = rootView.findViewById(R.id.gridLayout);

                int index = placePanel.positionY * gridLayout.getColumnCount() + placePanel.positionX;

                if (index >= 0 && index < gridLayout.getChildCount()) {
                    View placeButton = gridLayout.getChildAt(index);
                    placeButton.setBackgroundColor(Place.UNKNOWN.typePlace.backgroundColor);
                }
            }
        });

    }


    /**
     * When a place button is clicked, concrete PlaceInfoFragment is shown.
     */
    private class PlaceButtonClickListener implements View.OnClickListener {
        private int x;
        private int y;
        private Place place;
        private SurroundingPlacesFragment surroundingPlacesFragment;

        PlaceButtonClickListener(int x, int y, Place place, SurroundingPlacesFragment surroundingPlacesFragment) {
            this.x = x;
            this.y = y;
            this.place = place;
            this.surroundingPlacesFragment = surroundingPlacesFragment;
        }

        @Override
        public void onClick(View v) {
            PlaceInfoFragment placeInfoFragment = place.getInfoFragment(surroundingPlacesFragment, fragmentContainerId);
            AbstractActivity.getActualActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(fragmentContainerId, placeInfoFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
