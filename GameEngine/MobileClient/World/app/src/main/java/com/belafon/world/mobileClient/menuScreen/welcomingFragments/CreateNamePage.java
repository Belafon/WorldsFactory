package com.belafon.world.mobileClient.menuScreen.welcomingFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.belafon.world.mobileClient.R;

public class CreateNamePage extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_welcome_new_name_page, null);
        Button button = (Button)root.findViewById(R.id.setClientsName);
        button.setBackgroundResource(R.color.transparent);
        return root;
    }
}
