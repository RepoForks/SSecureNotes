package com.hooloovoo.securenotes;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    private static final String KEY_HINT_PREF = "hints_value" ;
    SettingsActivity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setPreferences();

	}

	@Override
	public void onResume(){
		super.onResume();

	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (SettingsActivity) activity;
    }

    private void setPreferences(){
		addPreferencesFromResource(R.xml.settingsfile);
        Preference openInfoB = (Preference) getPreferenceScreen().findPreference("infobaracca");
        openInfoB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baraccasoftware.com"));
                startActivity(browserIntent);

                mActivity.browserActivity=true;
                return true;
            }
        });

        Preference infoActivity = (Preference) getPreferenceScreen().findPreference("infoactivityrun");
        infoActivity.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent infoIntent = new Intent(mActivity,InfoActivity.class);
                startActivity(infoIntent);
                mActivity.sameApp = true;
                return true;
            }
        });
        Preference setPassword = (Preference) getPreferenceScreen().findPreference("setPasswordKey");
        setPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startPasswordDialog();
                return true;
            }
        });
	}

    private void startPasswordDialog(){
        PasswordDialogFragment newf = new PasswordDialogFragment();
        newf.mContenxt = mActivity.getApplicationContext();
        newf.show(getFragmentManager(), "password");
    }

//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(KEY_HINT_PREF)) {
//            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putBoolean("silentMode", mSilentMode);
//
//            // Commit the edits!
//            editor.commit();
//        }
//    }

}
