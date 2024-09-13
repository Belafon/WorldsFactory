package com.belafon.world.mobileClient;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Screen {
    private static final String TAG = "Screen";
    public volatile static int screenWidth;
    public volatile static int screenHeight;
    public static void setScreen(MainActivity mainActivity){
        // get size of screen, we needs to sum also the size of navigation bar, which is hidden
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels + getNavigationBarHeight(mainActivity);
        screenHeight = displaymetrics.heightPixels;
    }

    // GET SIZE OF NAVIGATIONBAR
    private static int getNavigationBarHeight(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.widthPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.widthPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    // HIDE BARS
    public static int currentApiVersion;
    public static void hideBars(AppCompatActivity activity){
        activity.getSupportActionBar().hide(); // hide the title bar
        // HIDE NAVIGATION BAR
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = activity.getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
            {
                @Override
                public void onSystemUiVisibilityChange(int visibility) // TODO
                {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) decorView.setSystemUiVisibility(flags);
                }
            });
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // Write info message Toast
    public static synchronized void info(String message, int duration){
        Toast.makeText(AbstractActivity.getActualActivity(), message, duration).show();
    }

    public static int dpToPixels(float dpValue) {
        float scale = AbstractActivity.getActualActivity().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
