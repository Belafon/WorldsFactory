package com.belafon.world.mobileClient.dataSafe;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class for saving data in shared preferences,
 * that means saving configuration data.
 */
public class DataLibrary {
    private volatile SharedPreferences sharedPreferences;
    private String wardrobeName = "";

    public DataLibrary(String name){
        this.wardrobeName = name;
    }
    public void saveDataInteager(Context context, int value, String name){
        sharedPreferences = context.getSharedPreferences(wardrobeName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(name, value);
        editor.apply();
    }
    public void saveDataString(Context context, String value, String name){
        sharedPreferences = context.getSharedPreferences(wardrobeName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }
    public void saveDataBoolean(Context context, boolean value, String name){
        sharedPreferences = context.getSharedPreferences(wardrobeName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }
    public int LoadDataInteager(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(wardrobeName, Context.MODE_PRIVATE);
        int value = 0;
        value = sharedPreferences.getInt(name, value);
        return value;
    }
    public String LoadDataString(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(wardrobeName, Context.MODE_PRIVATE);
        String value  = "true";
        value = sharedPreferences.getString(name, value);
        return value;
    }
    public boolean LoadDataBool(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(wardrobeName, Context.MODE_PRIVATE);
        boolean bool = true;
        bool = sharedPreferences.getBoolean(name, bool);
        return bool;
    }
}
