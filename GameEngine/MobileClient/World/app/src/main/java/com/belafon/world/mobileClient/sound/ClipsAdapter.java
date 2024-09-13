package com.belafon.world.mobileClient.sound;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * This class is used to play music in the game.
 */
public class ClipsAdapter {

    private static final String TAG = "ClipsAdapter";

    public MediaPlayer mMediaPlayer;
    public float size;
    public float volumeSize;
    public static boolean stopPlay;
    public static boolean backOrExit;
    public static boolean goBack;
    private int currentSong;

    public void play(Context context, int type, boolean isLoop, float volumeSize) {
        mMediaPlayer = MediaPlayer.create(context, type);
        setCurrentSong(type);
        mMediaPlayer.setLooping(isLoop);
        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
        setVolume(volumeSize);
        setVolumeSize(volumeSize);
    }

    // 1 = default speed
   /* public void setSpeed(float speed){
        mMediaPlayer.getPlaybackParams().setSpeed(speed);
    }*/
    public void setVolume(float size){
        mMediaPlayer.setVolume(size, size);
        this.size = size;
        //Log.d(TAG, String.valueOf(size));
    }
    public void start(){
        mMediaPlayer.start();
    }
    public void pause(){
        mMediaPlayer.pause();
    }
    public void stop(){
        mMediaPlayer.stop();
    }
    public void seekTo(int i){
        mMediaPlayer.seekTo(i);
    }
    public void looping(boolean b){
        mMediaPlayer.setLooping(b);
    }
    public void setStopPlay(boolean b){
        stopPlay = b;
    }
    public  boolean getStopPlay(){
        return stopPlay;
    }
    public void setVolumeSize(float size){
        volumeSize = size;
    }
    public float getVolumeSize(){
        return volumeSize;
    }

    public void setSize(float size){
        this.size = size;
    }
    public float getSize(){
        return this.size;
    }
    public int getCurrentSong() {
        return currentSong;
    }
    public void setCurrentSong(int currentSong) {
        this.currentSong = currentSong;
    }
}