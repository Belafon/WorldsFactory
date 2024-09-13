package com.belafon.world.mobileClient.game.maps.weather;

import android.util.Log;

import com.belafon.world.mobileClient.logs.Logs;

/**
 * This class represents a transition between two colors.
 * It is used for coloring the background filter.
 */
public class DifferenceColorViewTransition extends ColorViewTransition {
    private static final String TAG = "DifferenceColorViewTran";
    // says how much times it should be updated before 
    // the color is changed to the full transition
    public int durationOfTransition;
    // says how many updates is a change
    private float updateFrekvency = 0;
    // indicates when will be next update where is a change,
    // or when will not be next update, it depends
    // if there are more updates with change or wthout (if updateFrekvenci is higher than 0)
    private int currentIndexCounter = 0;

    // when this drops to 0, the transition is done
    public int currentIdleCount = 0;

    // it says how much the color should change in total by this transition
    public final Color colorDifference;

    private Color currentlyUpdatedColor = new Color(0, 0, 0, 0);
    
    //public Color colorUnitDifference;
    private int numberOfUpdates = 0;

    public Color getWholeCurrentUpdate(){
        return currentlyUpdatedColor;
    }
    public DifferenceColorViewTransition(Color colorDifference, int durationOfTransition) {
        this.colorDifference = new Color(
                colorDifference.r,
                colorDifference.g,
                colorDifference.b,
                colorDifference.a);

        this.durationOfTransition = durationOfTransition;
        this.currentIdleCount = durationOfTransition + 1;

        this.updateFrekvency = 0;
        this.currentIndexCounter = 0;

        /*   this.colorUnitDifrence = new Color(
                changeUnitTransition(colorDifference.r),
                changeUnitTransition(colorDifference.g),
                changeUnitTransition(colorDifference.b),
                changeUnitTransition(colorDifference.a));*/
        sizeOfUnitR = changeUnitTransition(colorDifference.r);
        sizeOfUnitG = changeUnitTransition(colorDifference.g);
        sizeOfUnitB = changeUnitTransition(colorDifference.b);
        sizeOfUnitA = changeUnitTransition(colorDifference.a);
    }

    private float sizeOfUnitR = 0f;
    private float sizeOfUnitG = 0f;
    private float sizeOfUnitB = 0f;
    private float sizeOfUnitA = 0f;

    // counters are counting up to 1,
    // when some counter is larger, the concrete color chanel is changed
    private float counterOfUnitR = 0f;
    private float counterOfUnitG = 0f;
    private float counterOfUnitB = 0f;
    private float counterOfUnitA = 0f;

    private float numberOfUpdatesR = 0;
    private float numberOfUpdatesG = 0;
    private float numberOfUpdatesB = 0;
    private float numberOfUpdatesA = 0;

    private float changeUnitTransition(float unitOfTransition) {
        // size of unit
        return  unitOfTransition / durationOfTransition;
    }

    @Override
    public synchronized void updateCurrentIdleCount() {
        isUpdateUsed = false;
        // reset counters
        if (Math.abs(counterOfUnitR) >= 1) {
            counterOfUnitR = getRestSmallerThanOne(counterOfUnitR);
            numberOfUpdatesR++;
        }
        if (Math.abs(counterOfUnitG) >= 1){
            counterOfUnitG = getRestSmallerThanOne(counterOfUnitG);
            numberOfUpdatesG++;
        }

        if (Math.abs(counterOfUnitB) >= 1){
            counterOfUnitB = getRestSmallerThanOne(counterOfUnitB);
            numberOfUpdatesB++;
        }

        if (Math.abs(counterOfUnitA) >= 1){
            counterOfUnitA = getRestSmallerThanOne(counterOfUnitA);
            numberOfUpdatesA++;
        }

        counterOfUnitR += sizeOfUnitR;
        counterOfUnitG += sizeOfUnitG;
        counterOfUnitB += sizeOfUnitB;
        counterOfUnitA += sizeOfUnitA;

        this.currentlyUpdatedColor = new Color(
                currentlyUpdatedColor.r + (int)sizeOfUnitR,
                currentlyUpdatedColor.r + (int)sizeOfUnitR,
                currentlyUpdatedColor.r + (int)sizeOfUnitR,
                currentlyUpdatedColor.r + (int)sizeOfUnitR);

        currentIdleCount--;
        numberOfUpdates++;
    }

    private boolean isUpdateUsed = false;
    @Override
    public synchronized Color getColorUpdate() {
        isUpdateUsed = true;
        return new Color((int) counterOfUnitR, (int) counterOfUnitG, (int) counterOfUnitB, (int) counterOfUnitA);
    }


    private float getRestSmallerThanOne(float num){
        int inum = (int) num;
        return num - (float)inum;
    }
    
    @Override
    public boolean isTransitionDone() {
        if (currentIdleCount <= 0) {
            return true;
        }
        return false;
    }

    /**
     * This method speeds up the transition,
     * or leave the speed same.
     */
    public void setHigherSpeedOfTransition(int speedOfTransition){
        if(speedOfTransition >= currentIdleCount)
            return;
        setDurationOfTransition(speedOfTransition);
    }
    public synchronized void setDurationOfTransition(int durationOfTransition) {
    //    if(isUpdateUsed){
            setSpeedOfTransitionWhenUpdateUsed(durationOfTransition);
    //    } else {
   //        setSpeedOfTransitionWhenUpdateNotUsed(speedOfTransition);
     //   }
    }

    private void setSpeedOfTransitionWhenUpdateNotUsed(int speedOfTransition) {
        if(speedOfTransition <= 0)
            speedOfTransition = 1;

        if(Logs.WEATHER_FILTER)
            Log.d(TAG, "setSpeedOfTransition: set speed ------------ " + speedOfTransition);

        this.durationOfTransition = speedOfTransition;

        float sizeOfUnitRNew = changeUnitTransition((float) colorDifference.r - (sizeOfUnitR * numberOfUpdatesR));
        float sizeOfUnitGNew = changeUnitTransition((float) colorDifference.g - (sizeOfUnitG * numberOfUpdatesG));
        float sizeOfUnitBNew = changeUnitTransition((float) colorDifference.b - (sizeOfUnitB * numberOfUpdatesB));
        float sizeOfUnitANew = changeUnitTransition((float) colorDifference.a - (sizeOfUnitA * numberOfUpdatesA));

        numberOfUpdatesR = (sizeOfUnitR * numberOfUpdatesR) / sizeOfUnitRNew;
        numberOfUpdatesG = (sizeOfUnitG * numberOfUpdatesG) / sizeOfUnitGNew;
        numberOfUpdatesB = (sizeOfUnitB * numberOfUpdatesB) / sizeOfUnitBNew;
        numberOfUpdatesA = (sizeOfUnitA * numberOfUpdatesA) / sizeOfUnitANew;

        this.sizeOfUnitR = sizeOfUnitRNew;
        this.sizeOfUnitG = sizeOfUnitGNew;
        this.sizeOfUnitB = sizeOfUnitBNew;
        this.sizeOfUnitA = sizeOfUnitANew;

        this.numberOfUpdates = 0;
        this.currentIdleCount = speedOfTransition;
    }

    private void setSpeedOfTransitionWhenUpdateUsed(int speedOfTransition) {
        if(speedOfTransition <= 0)
            speedOfTransition = 1;

        if(Logs.WEATHER_FILTER)
            Log.d(TAG, "setSpeedOfTransition: set speed ------------ " + speedOfTransition);

        this.durationOfTransition = speedOfTransition;

        float sizeOfUnitRNew = changeUnitTransition((float) colorDifference.r - (sizeOfUnitR * numberOfUpdatesR + counterOfUnitR));
        float sizeOfUnitGNew = changeUnitTransition((float) colorDifference.g - (sizeOfUnitG * numberOfUpdatesG + counterOfUnitG));
        float sizeOfUnitBNew = changeUnitTransition((float) colorDifference.b - (sizeOfUnitB * numberOfUpdatesB + counterOfUnitB));
        float sizeOfUnitANew = changeUnitTransition((float) colorDifference.a - (sizeOfUnitA * numberOfUpdatesA + counterOfUnitA));

        numberOfUpdatesR = (sizeOfUnitR * numberOfUpdatesR + counterOfUnitR) / sizeOfUnitRNew;
        numberOfUpdatesG = (sizeOfUnitG * numberOfUpdatesG + counterOfUnitG) / sizeOfUnitGNew;
        numberOfUpdatesB = (sizeOfUnitB * numberOfUpdatesB + counterOfUnitB) / sizeOfUnitBNew;
        numberOfUpdatesA = (sizeOfUnitA * numberOfUpdatesA + counterOfUnitA) / sizeOfUnitANew;

        counterOfUnitR = 0;
        counterOfUnitG = 0;
        counterOfUnitB = 0;
        counterOfUnitA = 0;

        this.sizeOfUnitR = sizeOfUnitRNew;
        this.sizeOfUnitG = sizeOfUnitGNew;
        this.sizeOfUnitB = sizeOfUnitBNew;
        this.sizeOfUnitA = sizeOfUnitANew;

        this.numberOfUpdates = 0;
        this.currentIdleCount = speedOfTransition;
    }
}
