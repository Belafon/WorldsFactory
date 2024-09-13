package com.belafon.world.mobileClient.game.behaviours;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.Screen;
import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;
import com.belafon.world.mobileClient.game.visibles.creatures.PlayableCreature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fragment that contains all things that are needed
 * to be able to execute concrete behaviour.
 * 
 * For each requirement there is unique RequirementChooser, 
 * that holds a list of Spinners for each needed ingredient
 * (according the count).
 * 
 * Also when an ingredient is selected, in other 
 * RequirementChoosers the ingredient is not able to be
 * selected anymore.
 */
public class BehavioursExecutorFragment extends Fragment {
    private static final int ITEM_BEHAVIOURS_PADDING_IN_PX = Screen.dpToPixels(8);

    private int fragmentContainerId;
    private Fragment previousFragment;
    private Behaviour behaviour;
    private Set<BehavioursPossibleIngredient> selectedIngredients = new HashSet<>();
    private List<BehavioursPossibleIngredient> preselectedIngredients = new ArrayList<>();
    public BehavioursExecutorFragment(int fragmentContainerId,
            Fragment lastFragment, Behaviour behaviour) {
        this.fragmentContainerId = fragmentContainerId;
        this.previousFragment = lastFragment;
        this.behaviour = behaviour;
    }

    public BehavioursExecutorFragment(int fragmentContainerId,
            Fragment lastFragment, Behaviour behaviour, List<BehavioursPossibleIngredient> selectedIngredients) {
        this.fragmentContainerId = fragmentContainerId;
        this.previousFragment = lastFragment;
        this.behaviour = behaviour;
        //this.selectedIngredients.addAll(selectedIngredients);
        this.preselectedIngredients = selectedIngredients;
    }

    private LinearLayout requirementsList;
    private TextView name;
    private TextView description;
    private Button execute;
    private LinearLayout preSelectedIngredientsList;
    private Button backButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_behaviours_executor, container, false);

        name = rootView.findViewById(R.id.behaviour_name);
        description = rootView.findViewById(R.id.behaviour_description);
        execute = rootView.findViewById(R.id.execute_button);
        requirementsList = rootView.findViewById(R.id.requirements_list);
        preSelectedIngredientsList = rootView.findViewById(R.id.preselected_ingredients_list);
        setPreselectedIngredientsList();

        backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener((View v) -> goBack());

        name.setText(behaviour.name);
        description.setText(behaviour.description);

        execute.setVisibility(View.VISIBLE);
        execute.setText("Execute");
        execute.setEnabled(true);
        execute.setOnClickListener((View v) -> executeBehaviour(behaviour));

        setRequirementsFragment(behaviour);

        return rootView;
    }

    private void setPreselectedIngredientsList() {
        if(selectedIngredients.isEmpty()){
            preSelectedIngredientsList.setVisibility(View.GONE);
            return;
        }

        for (BehavioursPossibleIngredient ingredient : preselectedIngredients) {
            TextView ingredientView = new TextView(getContext());
            ingredientView.setText(ingredient.toString());
            ingredientView.setTextColor(Color.WHITE);
            ingredientView.setGravity(Gravity.CENTER);
            int px = ITEM_BEHAVIOURS_PADDING_IN_PX;
            ingredientView.setPadding(px, px, px, px);
            preSelectedIngredientsList.addView(ingredientView);
        }
    }

    public void setBehaviour(Behaviour behaviour, Behaviours behaviours) {
        if(!behaviours.feasibles.contains(behaviour)){
            goBack();
            return;
        }

        this.behaviour = behaviour;

        name.setText(behaviour.name);
        description.setText(behaviour.description);

        execute.setVisibility(View.VISIBLE);
        execute.setEnabled(true);
        execute.setOnClickListener((View view) -> executeBehaviour(behaviour));

        setRequirementsFragment(behaviour);
    }

    private void executeBehaviour(Behaviour behaviour) {
        execute.setEnabled(false);

        // Get all selected ingredients
        List<BehavioursPossibleIngredient> selectedIngredients = new ArrayList<>();
        for (RequirementChooser chooser : requiremntsChoosers.values()) {
            for (Spinner spinner : chooser.spinners) {
                selectedIngredients.add((BehavioursPossibleIngredient) spinner.getSelectedItem());
            }
        }

        // Execute the behaviour with the selected ingredients
        behaviour.execute(selectedIngredients);
    }


    // set of all requirements of concrete behaviour, that is currently being
    // displayed
    public Map<Behaviour.BehavioursRequirementDetail, RequirementChooser> requiremntsChoosers = new HashMap<>();
    private Set<BehavioursPossibleIngredient> availableIngredients = new HashSet<>();

    private void setRequirementsFragment(Behaviour behaviour) {
        Set<BehavioursPossibleIngredient> availableIngredients;
        synchronized (PlayableCreature.allIngredients){
            availableIngredients = new HashSet<>(PlayableCreature.allIngredients);
        }

        // we have to remove all ingredients, that are already selected from different behaviour
        for (RequirementChooser chooser : requiremntsChoosers.values()) {
            for (Spinner spinner : chooser.spinners) {
                String selectedIngredientText = spinner.getSelectedItem().toString();

                // Find the corresponding BehavioursPossibleIngredient object
                BehavioursPossibleIngredient selectedIngredient = null;
                for (BehavioursPossibleIngredient ingredient : availableIngredients) {
                    if (ingredient.toString().equals(selectedIngredientText)) {
                        selectedIngredient = ingredient;
                        break;
                    }
                }

                if (selectedIngredient != null) {
                    availableIngredients.remove(selectedIngredient);
                }
            }
        }


        requiremntsChoosers = new HashMap<>();

        for (Behaviour.BehavioursRequirementDetail requirement : behaviour.requirements) {
            if (requirement.numOfConcreteIngredient != 0) {
                // Get the set of satisfiable ingredients for the requirement
                Set<BehavioursPossibleIngredient> satisfiableIngredients = new HashSet<>();
                getSatisfiableIngredients(requirement, satisfiableIngredients, availableIngredients);

                RequirementChooser requirementChooser = new RequirementChooser(requirement);
                requiremntsChoosers.put(requirement, requirementChooser);

                for (int i = 0; i < requirement.numOfConcreteIngredient; i++) {
                    // The behaviour is not feasible according clients data
                    if (satisfiableIngredients.isEmpty()) {
                        if(this.isDetached())
                            goBack();
                        return;
                    }

                    BehavioursPossibleIngredient item = satisfiableIngredients.iterator().next();
                    satisfiableIngredients.remove(item);
                    availableIngredients.remove(item);
                    requirementChooser.addNewIngredient(item, getContext());
                }
            }
        }

        for (RequirementChooser chooser : requiremntsChoosers.values()) {
            for (Spinner spinner : chooser.spinners) {
                ArrayAdapter<BehavioursPossibleIngredient> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, new ArrayList<>(availableIngredients));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        }

        setAvailableIngredients(behaviour, availableIngredients);

        drawList();
    }

    private void setAvailableIngredients(Behaviour behaviour, Set<BehavioursPossibleIngredient> availableIngredients) {
        this.availableIngredients = availableIngredients;
        for (Behaviour.BehavioursRequirementDetail requirement : behaviour.requirements) {
            if (requirement.numOfConcreteIngredient != 0) {
                Set<BehavioursPossibleIngredient> satisfableIngredients = new HashSet<>();
                getSatisfiableIngredients(requirement, satisfableIngredients, this.availableIngredients);
                requiremntsChoosers.get(requirement).setAvailableIngredients(satisfableIngredients, this.getContext());
            }
        }
    }

    private void getSatisfiableIngredients(Behaviour.BehavioursRequirementDetail detailedRequirement,
                                           Set<BehavioursPossibleIngredient> satisfableIngredients, Set<BehavioursPossibleIngredient> availableIngredients) {
        synchronized (PlayableCreature.allIngredients){
            for (BehavioursPossibleIngredient ingredient : PlayableCreature.allIngredients) {
                if (ingredient.requirements.contains(detailedRequirement.requirement)
                        && availableIngredients.contains(ingredient))
                    // the ingredient is satifable
                    satisfableIngredients.add(ingredient);
            }
        }
    }

    private LinearLayout drawList() {
        LinearLayout panel = requirementsList;
        panel.removeAllViews();

        for (RequirementChooser chooser : requiremntsChoosers.values()) {
            for (Spinner spinner : chooser.spinners) {
                spinner.setBackground(getResources().getDrawable(R.color.black, null));
                spinner.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        100));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    private BehavioursPossibleIngredient lastSelected = (BehavioursPossibleIngredient) spinner.getSelectedItem();
                    private boolean isUserCall = true;
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView)view).setTextColor(Color.WHITE);

                        BehavioursPossibleIngredient selectedIngredient = (BehavioursPossibleIngredient) parent.getItemAtPosition(position);
                        if(selectedIngredient == lastSelected
                                || !isUserCall){
                            isUserCall = true;
                            return;
                        }

                        isUserCall = false;

                        if (selectedIngredient != null) {
                            availableIngredients.remove(selectedIngredient);
                            availableIngredients.add(lastSelected);
                            lastSelected = selectedIngredient;

                            chooser.selectIngredient(selectedIngredient, spinner);

                            setAvailableIngredients(behaviour, availableIngredients);

                            panel.requestLayout();
                            panel.invalidate();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


                panel.addView(spinner);

                Iterator<BehavioursPossibleIngredient> preselectedIter = this.preselectedIngredients.iterator();
                while(preselectedIter.hasNext()){
                    BehavioursPossibleIngredient next = preselectedIter.next();
                    if(chooser.getAvailableIngredients().contains(next)){
                        chooser.selectIngredient(next, spinner);
                        preselectedIter.remove();
                        break;
                    }
                }
            }
        }

        panel.setMinimumHeight(90);

        return panel;
    }

    private void goBack() {
        synchronized (BehavioursFragment.EXECUTORS){
            BehavioursFragment.EXECUTORS.remove(this);
        }

        FragmentManager fragmentManager = getParentFragmentManager();

        fragmentManager.beginTransaction()
                .replace(fragmentContainerId, previousFragment)
                .addToBackStack(null)
                .commit();
    }

    /*public void update(Behaviours behaviours) {
        setBehaviour(behaviour, behaviours);
        execute.setEnabled(true);
    }*/

    public Behaviour getCurrentlySelectedBehaviour() {
        return behaviour;
    }
}
