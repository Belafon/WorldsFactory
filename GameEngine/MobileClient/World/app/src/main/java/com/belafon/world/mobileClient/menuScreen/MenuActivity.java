package com.belafon.world.mobileClient.menuScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.client.Client;
import com.belafon.world.mobileClient.gameActivity.GameActivity;
import com.belafon.world.mobileClient.gameActivity.WaitingScreenFragment;
import com.belafon.world.mobileClient.menuScreen.welcomingFragments.WelcomingActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.Screen;
import com.belafon.world.mobileClient.sound.Pool;

public class MenuActivity extends AbstractActivity {
    private static final String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Client client = new Client();
        Client.connectToLastIp();
        showConnectToServerFragment();
    }

    public void startTutorial(View view) {
        startTutorial();
    }

    private void startTutorial() {
        Intent menuIntent = new Intent(MenuActivity.this, WelcomingActivity.class);
        startActivity(menuIntent);
        finish();
    }

    // CONNECT TO SERVER FRAGMENT ---------------------------------------------
    public void showConnectToServerFragment() {
        openFragment(new ConnectToServerFragment(), R.id.menu_fragment);
    }

    public void showMenuFragment() {
        openFragment(new MenuFragment(this), R.id.menu_fragment);
    }

    // called, when the connect button is clicked, this will try to connect to
    // server
    // with ip set in EditText with id edit_ip
    public void connect(View view) {
        if (!getIpAddress())
            return;

        if (!getPortNumber())
            return;

        new Thread(() -> Client.connect())
                .start();
    }

    private boolean getIpAddress() {
        String ip = ((EditText) findViewById(R.id.edit_ip)).getText().toString();
        /*if (Patterns.IP_ADDRESS.matcher(ip).matches()) {
            Screen.info("Invalid ip address format.", 0);
            return false;
        }*/
        Client.ip = ip;
        return true;
    }

    private boolean getPortNumber() {
        int port = 0;
        try {
            String portString = ((EditText) findViewById(R.id.port)).getText().toString();
            port = Integer.parseInt(portString);
            if (port <= 1024) {
                Screen.info("Port must be bigger than 1024.", 0);
                return false;
            }
        } catch (Exception e) {
            Screen.info("Wrong port number format.", 0);
            return false;
        }
        Client.port = port;
        return true;
    }
    
    // FRAGMENT MENU ----------------------------------------------------------

    public void NewGame(View view) {
        newGame();
    }

    public void newGame() {
        Client.sender.serverMessages.findMatch();
        openFragment(new WaitingScreenFragment(), R.id.menu_fragment);
    }

    public void Character(View view) {
        float volume = 1;
        //Pool.playMenuSound(1, volume);
    }

    public void Stats(View view) {
        float volume = 1;
        //Pool.playMenuSound(2, volume);
    }

    public void StartSettings(View view) {
        float volume = 1;
        //Pool.playMenuSound(3, volume);
    }

    public void About(View view) {
        float volume = 1;
        //Pool.playMenuSound(4, volume);
    }

    public void startGame() {
        final Intent menuIntent = new Intent(MenuActivity.this, GameActivity.class);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                startActivity(menuIntent);
                finish();
            }
        });
    }


    public Fragment getMenuFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.menu_fragment);
    }
}