package com.belafon.world.mobileClient.gameActivity;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.belafon.world.mobileClient.Screen;

public class SetActivity {
    public static void setGameActivity(GameActivity gameActivity) {
        int navWidth = Screen.screenWidth * 7 / 100;
        int fragWidth = Screen.screenWidth * 93 / 100;
        int notificWidth = Screen.screenWidth - navWidth - fragWidth;
        gameActivity.getSideMenuFragment().getView()
                .setLayoutParams(new LinearLayout.LayoutParams(navWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        gameActivity.notifications
                .setLayoutParams(new LinearLayout.LayoutParams(notificWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        gameActivity.getGameFragment().getView()
                .setLayoutParams(new LinearLayout.LayoutParams(fragWidth, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
