package com.belafon.world.mobileClient.gameActivity;

import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.menuScreen.MenuActivity;

/**
 * Waiting screen for MenuActivity only.
 */
public class WaitingScreenFragment extends Fragment {
    private static final String TAG = "WaitingScreenFragment";

    public WaitingScreenFragment(){
        stopRotate = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_waiting_screen, container, false);
        ImageView imageView = (ImageView) root.findViewById(R.id.loading);
        final int height = AbstractActivity.getActualActivity().getDrawable(R.drawable.loading2).getIntrinsicHeight();
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(height, height));
        rotateImage(imageView);
        return root;
    }

    public static volatile boolean stopRotate = false;

    private void rotateImage(ImageView imageView) {
        new Thread(() -> {
            Thread.currentThread().setName("RotateImage");
            float angle = 0;
            while (!stopRotate) {
                angle += 0.1f;
                final Matrix matrix = new Matrix();
                final float angle2 = angle;

                new Thread(() -> {
                    AbstractActivity.getActualActivity().runOnUiThread(() -> {
                        imageView.setScaleType(ImageView.ScaleType.MATRIX); // required
                        matrix.postRotate((float) angle2, imageView.getWidth() / 2,
                                imageView.getHeight() / 2);
                        imageView.setImageMatrix(matrix);
                    });
                }).start();

                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

    }

    public static void setNumberOfPlayersToWait(int numberOfPlayersToWait) {
        WaitingScreenFragment waitingScreenFragment = getWaitingScreenFragment();
        if (waitingScreenFragment != null)
            waitingScreenFragment.setNumberOfPlayersToWaitTextView(numberOfPlayersToWait);
    }

    private static WaitingScreenFragment getWaitingScreenFragment() {
        // check if current activity is menu activity
        // and if current fragment is waiting screen fragment
        if (AbstractActivity.getActualActivity() instanceof MenuActivity menuActivity
                && menuActivity.getMenuFragment() instanceof WaitingScreenFragment waitingScreenFragment) {
            return waitingScreenFragment;
        } else
            Log.d(TAG,
                    "setNumberOfPlayersToWait: current activity is not menu activity or current fragment is not waiting screen fragment");
        return null;
    }

    public static void startGame() {
        stopRotate = true;
        MenuActivity menuActivity = getMenuActivity();

        if (menuActivity == null)
            return;

        menuActivity.startGame();
    }

    private static MenuActivity getMenuActivity() {
        // check if current activity is menu activity
        // and if current fragment is waiting screen fragment
        if (AbstractActivity.getActualActivity() instanceof MenuActivity menuActivity) {
            return menuActivity;
        } else
            Log.d(TAG,"setNumberOfPlayersToWait: current activity is not menu activity or current fragment is not waiting screen fragment");
        return null;
    }

    private void setNumberOfPlayersToWaitTextView(int numberOfPlayersToWait) {
        AbstractActivity.getActualActivity().runOnUiThread(() -> {
            getNumberOfPlayersToWaitTextView().setText(numberOfPlayersToWait + "");
        });
    }

    private TextView getNumberOfPlayersToWaitTextView() {
        return (TextView) AbstractActivity.getActualActivity().findViewById(R.id.numberOfPlayersInQueue);
    }
}
