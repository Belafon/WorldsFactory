package com.belafon.world.mobileClient.game.visibles;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.Screen;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

import java.util.Hashtable;

/**
 * List of some type of visible.
 */
public abstract class VisiblesListFragment<T extends VisiblesInfoFragment> extends Fragment {
    private LinearLayout visiblesList;
    private Hashtable<Visible, T> visibleFragments = new Hashtable<>();
    private T selectedVisibleFragment;
    protected Fragment returnFragment;

    protected int fragmentContainerId;
    private final Visibles visibles;
    public VisiblesListFragment(int fragmentContainerId, Visibles visibles, Fragment returnFragment) {
        this.fragmentContainerId = fragmentContainerId;
        this.visibles = visibles;
        this.returnFragment = returnFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_visibles_list, container, false);

        visiblesList = rootView.findViewById(R.id.visibles_list);

        initialize(visibles);
        rootView.findViewById(R.id.scroll_view);

        return rootView;
    }
    protected abstract void initialize(Visibles visibles);

    protected void addVisiblesTitle(T visibleFragment, Runnable selectVisibleFragment) {
        if (!this.isAdded())
            return;
        
        TextView title = createTitleView(visibleFragment.getTitleText());
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dpToPixels(35));
        layoutParams.setMargins(0,5 , 0, 0);
        title.setBackgroundResource(R.color.themeTransparent);
        title.setLayoutParams(layoutParams);

        visibleFragment.setTitleView(title);
        title.setOnClickListener((View v) -> selectVisibleFragment.run());

        visiblesList.addView(title);
        visibleFragments.put(visibleFragment.getVisible(), visibleFragment);
    }

    protected void removeVisible(Visible visible) {
        if (!this.isAdded())
            return;

        getActivity().runOnUiThread(() -> {
            updateRemovedVisible(visibleFragments.get(visible), visible);

            visiblesList.removeView(visibleFragments.get(visible).getTitleView());
            visibleFragments.remove(visible);
        });
    }

    protected abstract void updateRemovedVisible(T infoFragment, BehavioursPossibleIngredient ingredient);
    protected void showVisiblesInfoFragment(T visibleInfoFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerId, visibleInfoFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private TextView createTitleView(String title) {
        TextView titleView = new TextView(getContext());
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setPadding(10, 10, 10, 10);

        return titleView;
    }
}
