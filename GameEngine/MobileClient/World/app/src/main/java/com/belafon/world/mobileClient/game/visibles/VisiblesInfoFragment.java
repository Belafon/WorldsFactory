package com.belafon.world.mobileClient.game.visibles;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

/**
 * Holds information about concrete visible.
 * It contains list of information with list of 
 * feasible behaviours, that can be executed with 
 * this visible as an ingredient.
 */
public abstract class VisiblesInfoFragment<T extends Visible> extends Fragment {
    private Fragment previousFragment;
    private int fragmentContainerId;
    protected T visible;

    public VisiblesInfoFragment(
            Fragment previousFragment,
            int fragmentContainerId,
            T visible) {
        this.visible = visible;
        this.previousFragment = previousFragment;
        this.fragmentContainerId = fragmentContainerId;
    }

    /**
     * Go back to the previous fragment.
     */
    public void goBack() {
        if(isAdded()){
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragmentContainerId, previousFragment);
            fragmentTransaction.commit();
        }
    }

    public abstract String getTitleText();

    public T getVisible() {
        return visible;
    }

    /**
     * Title view in the context of the VIsibleListFragment,
     * this fragmnet is the only one.
     * 
     * This information is held just for removeing the title view
     * from the list.
     */
    private TextView titleView;

    public View getTitleView() {
        return titleView;
    }

    public void setTitleView(TextView titleView) {
        this.titleView = titleView;
    }

}