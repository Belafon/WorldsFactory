package com.belafon.world.mobileClient.game.behaviours;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.belafon.world.mobileClient.game.behaviours.behavioursPossibleIngredients.BehavioursPossibleIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RequirementChooser {
    public Behaviour.BehavioursRequirementDetail requirement;
    private List<BehavioursPossibleIngredient> selectedIngredients = new ArrayList<>();
    private Set<BehavioursPossibleIngredient> availableIngredients;
    List<Spinner> spinners = new ArrayList<>();

    public Set<BehavioursPossibleIngredient> getAvailableIngredients(){
            return Collections.unmodifiableSet(this.availableIngredients);
    }

    public RequirementChooser(Behaviour.BehavioursRequirementDetail requirement) {
        this.requirement = requirement;
    }

    public void setAvailableIngredients(Set<BehavioursPossibleIngredient> availableIngredients, Context context) {
        this.availableIngredients = availableIngredients;
        fillSpinners(context);
    }

    /**
     * Creates a new Spinner and saves the default selected item.
     *
     * @param ingredient The selected ingredient.
     * @param context
     */
    public void addNewIngredient(BehavioursPossibleIngredient ingredient, Context context) {
        selectedIngredients.add(ingredient);
        Spinner spinner = new Spinner(context);
        spinners.add(spinner);
    }

    private void fillSpinners(Context context) {
        if (availableIngredients == null)
            throw new Error("setAvailableIngredients is null!");

        int spinnerIndex = 0;
        for (Spinner spinner : spinners) {
            List<BehavioursPossibleIngredient> ingredientsList = new ArrayList<>();

            BehavioursPossibleIngredient selectedIngredient = selectedIngredients.get(spinnerIndex);
            ingredientsList.add(selectedIngredient);

            List<BehavioursPossibleIngredient> availableIngredientsList = new ArrayList<>(availableIngredients);
            Collections.sort(availableIngredientsList);
            ingredientsList.addAll(availableIngredientsList);

            ArrayAdapter<BehavioursPossibleIngredient> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, ingredientsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            spinner.setSelection(0);


            //((TextView)spinner.getSelectedView()).setTextColor(Color.WHITE);


            spinnerIndex++;
        }
    }

    /**
     * Finds the position of a target item in a Spinner's adapter.
     *
     * @param spinner The Spinner in which to search for the item.
     * @param targetItem The object representing the target item.
     * @return The position of the target item in the Spinner's adapter, or -1 if not found.
     */
    private static int findPositionInSpinner(Spinner spinner, Object targetItem) {
        ArrayAdapter<Object> adapter = (ArrayAdapter<Object>) spinner.getAdapter();
        int itemCount = adapter.getCount();

        for (int i = 0; i < itemCount; i++) {
            if (adapter.getItem(i).equals(targetItem)) {
                return i;
            }
        }

        return -1; // Item not found
    }

    /**
     * Selects the ingredient without updating the spinner.
     * */
    public void selectIngredient(BehavioursPossibleIngredient ingredient, Spinner spinner) {
        int index = spinners.indexOf(spinner);
        selectedIngredients.set(index, ingredient);
        int position = findPositionInSpinner(spinner, ingredient);
        spinner.setSelection(position, true);
    }
}
