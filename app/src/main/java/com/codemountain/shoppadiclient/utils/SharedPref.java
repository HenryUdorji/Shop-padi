package com.codemountain.shoppadiclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String PACKAGE = "com.codemountain.shoppadiclient.utils";
    public static final String CATEGORY = "com.codemountain.shoppadiclient.utils.category";
    public static final String MODE = "com.codemountain.shoppadiclient.utils.mode";

    SharedPreferences sharedPreferences;
   public SharedPref(Context context){
       sharedPreferences = context.getSharedPreferences(PACKAGE, Context.MODE_PRIVATE);
   }
   //Save pref
    public void putStringInPref(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void putBooleanInPref(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    //load pref
    public String getCategoryListString(){
        return sharedPreferences.getString(CATEGORY, "Android phones");
    }

    public String getUIDString(){
        return sharedPreferences.getString("UID", "");
    }

    public boolean getBoolean(){
        return sharedPreferences.getBoolean(MODE, false);
    }

}
