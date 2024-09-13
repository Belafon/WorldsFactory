package com.belafon.world.mobileClient.game.maps.weather;

/**
 * Handles a transition for a cloud. 
 * The sky is covered and then uncovered.
 */
public class CloudsColorViewTransition extends ColorViewTransition {
    public enum Phase {
        // the transition is approaching the finalColor
        coverSun, 
        // the filter is colored with finalColor
        wholeCloud, 
        // the transition is approaching the initialColor
        unconverSun, 
        done 
    };

    public CloudsColorViewTransition(Color finalColor, int speedOfTransition, int durationOfCloud){
        this.finalColor = finalColor;
        this.durationOfTransition = speedOfTransition;
        this.durationOfCloud = durationOfCloud;
        phase = Phase.coverSun;
        currentTransition = new DifferenceColorViewTransition(finalColor, durationOfTransition);
    }
    private DifferenceColorViewTransition currentTransition;
    private Phase phase = Phase.coverSun;
    private Color currentColor;
    private Color finalColor;
    private int durationOfTransition;
    private int durationOfCloud;

    @Override
    public synchronized Color getColorUpdate() {
        return this.currentTransition.getColorUpdate();
    }

    @Override
    public synchronized void updateCurrentIdleCount() {
        if(phase == Phase.done)
            return;

        this.currentTransition.updateCurrentIdleCount();
        if(currentTransition.isTransitionDone()){
            if(phase == Phase.coverSun){
                phase = Phase.wholeCloud;
                Color color = new Color(0, 0, 0, 0);
                currentTransition = new DifferenceColorViewTransition(color, durationOfCloud);
            } else if(phase == Phase.wholeCloud){
                phase = Phase.unconverSun;
                Color returnColor = new Color(-finalColor.r, -finalColor.g, -finalColor.b, -finalColor.a);
                currentTransition = new DifferenceColorViewTransition(returnColor, durationOfTransition);
            } else if(phase == Phase.unconverSun){
                phase = Phase.done;
            }
        }
    }

    @Override
    public boolean isTransitionDone() {
        if(phase == Phase.done)
            return true;
        return false;
    }
}
