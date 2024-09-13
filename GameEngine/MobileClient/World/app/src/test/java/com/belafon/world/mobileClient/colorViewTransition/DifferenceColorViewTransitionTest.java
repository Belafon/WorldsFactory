package com.belafon.world.mobileClient.colorViewTransition;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.belafon.world.mobileClient.game.maps.weather.ColorViewTransition.Color;
import com.belafon.world.mobileClient.game.maps.weather.DifferenceColorViewTransition;
import com.belafon.world.mobileClient.logs.Logs;

public class DifferenceColorViewTransitionTest {

    private DifferenceColorViewTransition transition;

    @Before
    public void setup() {
        Logs.WEATHER_FILTER = false;
        Color colorDifference = new Color(50, 100, 150, 200);
        int speedOfTransition = 5;
        transition = new DifferenceColorViewTransition(colorDifference, speedOfTransition);
    }

    @Test
    public void testColorUpdate() {
        Color expectedColorUpdate = new Color(10, 20, 30, 40);
        transition.updateCurrentIdleCount();
        Color actualColorUpdate = transition.getColorUpdate();
        assertEquals(expectedColorUpdate.r, actualColorUpdate.r);
        assertEquals(expectedColorUpdate.g, actualColorUpdate.g);
        assertEquals(expectedColorUpdate.b, actualColorUpdate.b);
        assertEquals(expectedColorUpdate.a, actualColorUpdate.a);
    }

    @Test
    public void testIsTransitionDone() {
        assertFalse(transition.isTransitionDone());

        // Perform updates equal to the speedOfTransition
        for (int i = 0; i <= transition.durationOfTransition; i++) {
            transition.updateCurrentIdleCount();
        }

        assertTrue(transition.isTransitionDone());
    }

    @Test
    public void testSetSpeedOfTransition() {
        int newSpeed = 10;
        transition.setDurationOfTransition(newSpeed);
        assertEquals(newSpeed, transition.durationOfTransition);

        Color expectedColorUpdate = new Color(5, 10, 15, 20);
        transition.updateCurrentIdleCount();
        Color actualColorUpdate = transition.getColorUpdate();

        assertEquals(expectedColorUpdate.r, actualColorUpdate.r);
        assertEquals(expectedColorUpdate.g, actualColorUpdate.g);
        assertEquals(expectedColorUpdate.b, actualColorUpdate.b);
        assertEquals(expectedColorUpdate.a, actualColorUpdate.a);
    }

    @Test
    public void testUpdateCurrentIdleCount() {
        int initialIdleCount = transition.currentIdleCount;

        transition.updateCurrentIdleCount();

        assertEquals(initialIdleCount - 1, transition.currentIdleCount);

        // Set a new speed of transition
        int newSpeed = 2;
        transition.setDurationOfTransition(newSpeed);
        assertEquals(newSpeed, transition.durationOfTransition);

        // Update the idle count again and check if it is reduced by 1
        transition.updateCurrentIdleCount();
        assertEquals(newSpeed - 1, transition.currentIdleCount);

        // Update the idle count multiple times equal to the new speed of transition
        for (int i = 0; i < newSpeed; i++) {
            transition.updateCurrentIdleCount();
        }

        // Check if the transition is done
        assertTrue(transition.isTransitionDone());
    }

    @Test
    public void testUpdateSpeed() {
        int initialIdleCount = transition.currentIdleCount;

        transition.updateCurrentIdleCount();
        Color actualColorUpdate = transition.getColorUpdate();

        transition.setDurationOfTransition(10);
        transition.setDurationOfTransition(5);
        transition.setDurationOfTransition(2);

        transition.updateCurrentIdleCount();

        actualColorUpdate = transition.getColorUpdate();
        assertEquals(20, actualColorUpdate.r);
        assertEquals(40, actualColorUpdate.g);
        assertEquals(60, actualColorUpdate.b);
        assertEquals(80, actualColorUpdate.a);
        assertEquals(1, transition.currentIdleCount);
    }

    @Test
    public void testUpdateSpeedWithRepeatedSpeedUpdates() {
        int initialIdleCount = transition.currentIdleCount;

        transition.updateCurrentIdleCount();
        Color actualColorUpdate = transition.getColorUpdate();
        assertEquals(10, actualColorUpdate.r);
        assertEquals(20, actualColorUpdate.g);
        assertEquals(30, actualColorUpdate.b);
        assertEquals(40, actualColorUpdate.a);
        assertEquals(5, transition.currentIdleCount);

        transition.setDurationOfTransition(10);
        transition.updateCurrentIdleCount();
        actualColorUpdate = transition.getColorUpdate();
        assertEquals(4, actualColorUpdate.r); // 36
        assertEquals(8, actualColorUpdate.g); // 72
        assertEquals(12, actualColorUpdate.b); // 108
        assertEquals(16, actualColorUpdate.a); // 144
        assertEquals(9, transition.currentIdleCount);


        transition.setDurationOfTransition(5);
        transition.updateCurrentIdleCount();
        actualColorUpdate = transition.getColorUpdate();
        assertEquals(7, actualColorUpdate.r); // 28
        assertEquals(14, actualColorUpdate.g);
        assertEquals(21, actualColorUpdate.b);
        assertEquals(28, actualColorUpdate.a);
        assertEquals(4, transition.currentIdleCount);

        transition.setDurationOfTransition(2);
        transition.updateCurrentIdleCount();

        actualColorUpdate = transition.getColorUpdate();
        assertEquals(14, actualColorUpdate.r);
        assertEquals(28, actualColorUpdate.g);
        assertEquals(43, actualColorUpdate.b);
        assertEquals(57, actualColorUpdate.a);
        assertEquals(1, transition.currentIdleCount);
    }

}
