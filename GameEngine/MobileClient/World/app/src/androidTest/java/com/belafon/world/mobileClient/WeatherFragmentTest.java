package com.belafon.world.mobileClient;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.belafon.world.mobileClient.game.maps.weather.ColorViewTransition;
import com.belafon.world.mobileClient.game.maps.weather.DifferenceColorViewTransition;
import com.belafon.world.mobileClient.game.maps.weather.WeatherFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class WeatherFragmentTest {

    private WeatherFragment weatherFragment;
    private View mockedView;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        mockedView = new View(appContext);
        weatherFragment = new WeatherFragment(mockedView);
    }

    @Test
    public void testAddAndRemoveColorViewTransition() throws Exception {
        ColorViewTransition mockTransition1 = new DifferenceColorViewTransition(new ColorViewTransition.Color(10, 20, 30, 40), 5);
        ColorViewTransition mockTransition2 = new DifferenceColorViewTransition(new ColorViewTransition.Color(5, 15, 25, 35), 5);

        // Add two transitions
        weatherFragment.addColorViewTransition(mockTransition1);
        weatherFragment.addColorViewTransition(mockTransition2);

        // Verify that both transitions are added
        Set<ColorViewTransition> colorViewTransitions = getColorViewTransitions(weatherFragment);
        assertEquals(3, colorViewTransitions.size()); // the third one is part of day

        // Remove one transition
        weatherFragment.removeColorViewTransition(mockTransition1);

        // Verify that only one transition is left
        colorViewTransitions = getColorViewTransitions(weatherFragment);
        assertEquals(2, colorViewTransitions.size());
    }

    @Test
    public void testMakeTransition() throws Exception {
        ColorViewTransition mockTransition1 = new DifferenceColorViewTransition(new ColorViewTransition.Color(10, 20, 30, 40), 5);
        ColorViewTransition mockTransition2 = new DifferenceColorViewTransition(new ColorViewTransition.Color(5, 15, 25, 35), 5);

       /* // Add two transitions
        weatherFragment.addColorViewTransition(mockTransition1);
        weatherFragment.addColorViewTransition(mockTransition2);

        // Call makeTransition method to update color transitions
        Set<ColorViewTransition> colorViewTransitions = getColorViewTransitions(weatherFragment);
        Iterator<ColorViewTransition> iterator = colorViewTransitions.iterator();
        invokeMakeTransition(weatherFragment, iterator);

        // Verify that the color transitions are updated accordingly
        assertEquals(7, getRTransition(weatherFragment));
        assertEquals(17, getGTransition(weatherFragment));
        assertEquals(27, getBTransition(weatherFragment));
        assertEquals(37, getATransition(weatherFragment));*/
    }

    // Helper methods to access private members using reflection

    private Set<ColorViewTransition> getColorViewTransitions(WeatherFragment weatherFragment) throws Exception {
        Field field = WeatherFragment.class.getDeclaredField("colorViewTransitions");
        field.setAccessible(true);
        return (Set<ColorViewTransition>) field.get(weatherFragment);
    }

    private void invokeMakeTransition(WeatherFragment weatherFragment, Iterator<ColorViewTransition> iterator) throws Exception {
        Field field = WeatherFragment.class.getDeclaredField("rTransition");
        field.setAccessible(true);
        field.setInt(weatherFragment, 0);

        field = WeatherFragment.class.getDeclaredField("gTransition");
        field.setAccessible(true);
        field.setInt(weatherFragment, 0);

        field = WeatherFragment.class.getDeclaredField("bTransition");
        field.setAccessible(true);
        field.setInt(weatherFragment, 0);

        field = WeatherFragment.class.getDeclaredField("aTransition");
        field.setAccessible(true);
        field.setInt(weatherFragment, 0);

        weatherFragment.run();
    }

    private int getRTransition(WeatherFragment weatherFragment) throws Exception {
        Field field = WeatherFragment.class.getDeclaredField("rTransition");
        field.setAccessible(true);
        return field.getInt(weatherFragment);
    }

    private int getGTransition(WeatherFragment weatherFragment) throws Exception {
        Field field = WeatherFragment.class.getDeclaredField("gTransition");
        field.setAccessible(true);
        return field.getInt(weatherFragment);
    }

    private int getBTransition(WeatherFragment weatherFragment) throws Exception {
        Field field = WeatherFragment.class.getDeclaredField("bTransition");
        field.setAccessible(true);
        return field.getInt(weatherFragment);
    }

    private int getATransition(WeatherFragment weatherFragment) throws Exception {
        Field field = WeatherFragment.class.getDeclaredField("aTransition");
        field.setAccessible(true);
        return field.getInt(weatherFragment);
    }
}