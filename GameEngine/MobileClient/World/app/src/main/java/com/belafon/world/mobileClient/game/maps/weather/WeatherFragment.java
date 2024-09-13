package com.belafon.world.mobileClient.game.maps.weather;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import com.belafon.world.mobileClient.logs.Logs;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Contains main loop for updating the background color filter.
 * that is updated each 50 milliseconds.
 * Each update is done with use of {@link colorViewTransitions} set.
 */
public class WeatherFragment implements Runnable {
    private static final String TAG = "WeatherFragment";
    private final View view;
    private int r = 0; // 0 ... 255
    private int g = 0;
    private int b = 0;
    private int a = 0;
    private int aTransition = 0;
    private int rTransition = 0;
    private int gTransition = 0;
    private int bTransition = 0;
    private final Set<ColorViewTransition> colorViewTransitions = new HashSet<>();
    private final PartOfDayColorViewTransition partOfDayColorViewTransition = new PartOfDayColorViewTransition(
            new ColorViewTransition.Color(0, 0, 0,0));

    public final CurrentFilterColor currentFilterColor = new CurrentFilterColor(this);

    private final CloudsColorViewTransitions weatherTransitions;
    public WeatherFragment(View view) {
         weatherTransitions = new CloudsColorViewTransitions(colorViewTransitions, this.currentFilterColor);
        this.view = view;
        this.colorViewTransitions.add(partOfDayColorViewTransition);
        new Thread(this).start();
    }

    /**
     * Updates the text describing clouds
     * 
     * @param cloud
     */
    public void setClouds(Cloud cloud) {
        weatherTransitions.setClouds(cloud);
    }

    /**
     * Updates the text describing weather
     * 
     * @param weather
     */
    public void setWeather(String weather, Weather.WeatherType type) {
        weatherTransitions.setWeather(type);
    }

    /**
     * Updates the text describing part of day
     * 
     * @param partOfDay
     */
    public void setPartOfDay(PartOfDay partOfDay) {
        synchronized (this){
            partOfDayColorViewTransition.addPartOfDay(partOfDay);
        }
    }

    public synchronized void addColorViewTransition(ColorViewTransition colorViewTransition) {
        colorViewTransitions.add(colorViewTransition);
    }

    public synchronized void removeColorViewTransition(ColorViewTransition colorViewTransition) {
        colorViewTransitions.remove(colorViewTransition);
    }

    private ColorViewTransition.Color lastColor;
    @Override
    public void run() {
        Thread.currentThread().setName("BackgroundColorFilter");
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (colorViewTransitions) {
                Iterator<ColorViewTransition> iterator = colorViewTransitions.iterator();
                while (iterator.hasNext()) {
                    makeTransition(iterator);
                }
            }

            r += rTransition;
            g += gTransition;
            b += bTransition;
            a += aTransition;

            rTransition = 0;
            gTransition = 0;
            bTransition = 0;
            aTransition = 0;

            if(Logs.WEATHER_FILTER && lastColor != null && (lastColor.r != r
                    || lastColor.g != g
                    || lastColor.b != b
                    || lastColor.a != a))
                Log.d(TAG, "run: r, g, b, a = " + r + " " + g + " " + b + " " + a);

            if(lastColor != null && (lastColor.r != r
                    || lastColor.g != g
                    || lastColor.b != b
                    || lastColor.a != a))
                executor.execute(() ->
                    handler.post(() ->
                        view.setBackgroundColor(getAndroidColor()))
                );

            lastColor = new ColorViewTransition.Color(r, g, b, a);
        }
    }

    // executor is used to avoid blocking the main thread
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    private void makeTransition(Iterator<ColorViewTransition> iterator) {
        ColorViewTransition colorViewTransition = iterator.next();
        ColorViewTransition.Color updatedColor = colorViewTransition.getColorUpdate();
        rTransition += updatedColor.r;
        gTransition += updatedColor.g;
        bTransition += updatedColor.b;
        aTransition += updatedColor.a;
        colorViewTransition.updateCurrentIdleCount();
        if (colorViewTransition.isTransitionDone()) {
            iterator.remove();
        }
    }

    private int getAndroidColor() {
        int a = checkColorBounderies(checkColor(this.a));
        int r = checkColorBounderies(checkColor(this.r));
        int g = checkColorBounderies(checkColor(this.g));
        int b = checkColorBounderies(checkColor(this.b));
        return Color.argb(a, r, g, b);
    }

    private int checkColorBounderies(int color) {
        if(color > 255)
            return 255;
        else if (color < 0)
            return 0;
        else return color;
    }

    private int checkColor(int color) {
        if (color < 0) {
            return 0;
        }
        if (color > 255) {
            return 255;
        }
        return color;
    }

    public ColorViewTransition.Color getColor(){
        return new ColorViewTransition.Color(r, g, b, a);
    }

    public static class CurrentFilterColor{
        private final WeatherFragment fragment;
        public CurrentFilterColor(WeatherFragment fragment){
            this.fragment = fragment;
        }
        public ColorViewTransition.Color getColor(){
            return fragment.getColor();
        }
    }
}
