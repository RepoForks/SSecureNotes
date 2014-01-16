package com.hooloovoo.securenotes.object;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by angelo on 16/01/14.
 * This class need to remember temporally password in Shared Preference
 */
public class PasswordPreference {
    public static final String KEY_PREFS_PASSWORD = "password_key";
    private static final String APP_SHARED_PREFS = "passwordpreference"; //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public PasswordPreference(Context context){
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getPassword(){
        return _sharedPrefs.getString(KEY_PREFS_PASSWORD,"");
    }

    public void savePassword(String password){
        _prefsEditor.putString(KEY_PREFS_PASSWORD,password);
        _prefsEditor.commit();
    }
}
