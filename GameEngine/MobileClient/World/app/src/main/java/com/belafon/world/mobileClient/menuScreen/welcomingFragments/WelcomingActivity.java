package com.belafon.world.mobileClient.menuScreen.welcomingFragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.client.Client;
import com.belafon.world.mobileClient.MainActivity;
import com.belafon.world.mobileClient.menuScreen.MenuActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.Screen;

public class WelcomingActivity extends AbstractActivity {
    private static final String TAG = "WelcomingActivity";
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private final Fragment[] fragments;

    public WelcomingActivity() {
        this.fragments = new Fragment[]{
            new FirstPage(),new SecondPage(), new CreateNamePage()
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcoming);
        viewPager = findViewById(R.id.welcomingViewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
    
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }

    public void onNewClientNameClick(View view){
        String name = ((EditText)findViewById(R.id.newClientNameEditText)).getText().toString();
        if(name.length() < 3 || name.length() > 18) {
            Screen.info("The name is too short or too long", 0);
            return;
        }
        if(name.contains("\\") || name.contains("*")){
            Screen.info("You can not use * and \\", 0);
            return;
        }
        Client.setName(name);
        MainActivity.startData.saveDataBoolean(AbstractActivity.getActualActivity(), false, "isFirstStart");
        Intent menuIntent = new Intent(WelcomingActivity.this , MenuActivity.class);
        startActivity(menuIntent);
        finish();
    }
}