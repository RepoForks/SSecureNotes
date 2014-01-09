package com.hooloovoo.securenotes;



import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    Activity mActivity;
	
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
        mActivity = activity;
    }

    private void setPreferences(){
		addPreferencesFromResource(R.xml.settingsfile);
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

}
