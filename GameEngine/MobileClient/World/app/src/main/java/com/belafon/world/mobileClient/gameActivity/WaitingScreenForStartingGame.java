package com.belafon.world.mobileClient.gameActivity;

import android.graphics.Matrix;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.menuScreen.MenuActivity;

/**
 * Waiting screen.
 */
public class WaitingScreenForStartingGame extends Fragment {

    private static final String TAG = "WaitingScreenForStartin";

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

    public volatile boolean stopRotate = false;

    private void rotateImage(ImageView imageView) {
        new Thread(() -> {
            Thread.currentThread().setName("RotateImageAndWaitToStartTheGame");

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

}