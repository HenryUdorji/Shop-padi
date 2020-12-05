package com.codemountain.shoppadiclient;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.codemountain.shoppadiclient.utils.SharedPref;

public class ShopPadi extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPref sharedPref = new SharedPref(getApplicationContext());
        boolean mode = sharedPref.getBoolean();
        if (mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
