package com.belafon.world.mobileClient.gameActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.Game;
import com.belafon.world.mobileClient.menuScreen.EmptyFragment;



public class GameActivity extends AbstractActivity {
    public LinearLayout notifications;
    public ImageView backgroundImage;
    public View colorFilter;
    public Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display side bar navigation menu fragment
        openFragment(new MainMenuFragment(this), R.id.game_main_bar_navigation);

        // lets prepare stats and other fragments
        this.game = new Game(this);

        setContentView(R.layout.activity_game);

        notifications = (LinearLayout) findViewById(R.id.notifications);
        backgroundImage = (ImageView) findViewById(R.id.background_image);

        colorFilter = findViewById(R.id.color_filter);
        Game.stats.maps.weather.setWeatherView(colorFilter);

        var waitingScreenView = findViewById(R.id.waiting_screen);
        var waitingScreen = (WaitingScreenForStartingGame) getSupportFragmentManager().findFragmentById(R.id.waiting_screen);


        SetActivity.setGameActivity(this);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                waitingScreen.stopRotate = true;

                openFragment(game.fragments.bodyStatistics);
                waitingScreenView.setVisibility(View.GONE);
                openFragment(new EmptyFragment(), R.id.waiting_screen);
            });
        }).start();
    }

    public void openFragment(Fragment fragment) {
        openFragment(fragment, R.id.game_fragment);
    }

    public Fragment getGameFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.game_fragment);
    }

    public Fragment getSideMenuFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.game_main_bar_navigation);
    }

    /**
     * Sets the color filter of the game.
     * 
     * @param a 0-255
     * @param r 0-255
     * @param g
     * @param b
     */
    public void setFilterColor(int a, int r, int g, int b) {
        colorFilter.setBackgroundColor(android.graphics.Color.argb(a, r, g, b));
    }

    public int getGameFragmentContainerID() {
        return R.id.game_fragment;
    }

    public void setPlaceBackground(String picture) {
        int pictureId = getResources().getIdentifier(picture, "drawable", getPackageName());
        backgroundImage.setImageResource(pictureId);
    }
}