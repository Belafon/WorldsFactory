package com.belafon.world.mobileClient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.belafon.world.mobileClient.dataSafe.DataLibrary;
import com.belafon.world.mobileClient.menuScreen.MenuActivity;
import com.belafon.world.mobileClient.menuScreen.welcomingFragments.WelcomingActivity;

public class MainActivity extends AbstractActivity {
    private static final String TAG = "MainActivity";
    private static final int sizeOfTimeInFirstScreen = 500;
    public static final DataLibrary startData = new DataLibrary("startData");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Screen.setScreen(this);

        // boolean isFirstAppStart = startData.LoadDataBool(this, "isFirstStart");
        
        final boolean isFirstAppStart = false; // TODO: update welcoming activity and remove this line
        
        new Handler().postDelayed(() -> {
                Intent menuIntent = null;
                if(isFirstAppStart) menuIntent = new Intent(MainActivity.this , WelcomingActivity.class);
                else menuIntent = new Intent(MainActivity.this , MenuActivity.class);
                startActivity(menuIntent);
                finish();
        }, sizeOfTimeInFirstScreen);
    }
}