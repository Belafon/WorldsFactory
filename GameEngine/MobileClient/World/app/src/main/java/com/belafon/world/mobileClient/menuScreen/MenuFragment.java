package com.belafon.world.mobileClient.menuScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.belafon.world.mobileClient.R;

public class MenuFragment extends Fragment {
    private final MenuActivity menuActivity;
    public MenuFragment(MenuActivity menuActivity){
        this.menuActivity = menuActivity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        menuActivity.newGame();
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }
}
