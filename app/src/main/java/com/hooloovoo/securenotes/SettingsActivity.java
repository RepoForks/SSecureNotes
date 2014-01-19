package com.hooloovoo.securenotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hooloovoo.securenotes.object.PasswordPreference;

import java.util.Timer;
import java.util.TimerTask;


public class SettingsActivity extends Activity {

    SharedPreferences mSharedPreferences;
    boolean browserActivity = false;
    boolean sameApp = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setNavigationBar();
		// Display the fragment as the main content.

	}



	private void setNavigationBar(){
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		//getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case android.R.id.home:
            sameApp = true;
            NavUtils.navigateUpFromSameTask(this);
            return true;
		default:
            return super.onOptionsItemSelected(item);
		}
		//return super.onOptionsItemSelected(item);
	}

    @Override
    protected void onPause() {
        super.onPause();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean toLock = mSharedPreferences.getBoolean("lockapponpause",false);
        if(!sameApp && toLock){
            //lockAPP
            Log.d("NOTEACTIVITY", "lockApp");
            PasswordPreference preference = new PasswordPreference(getApplicationContext());
            preference.setLockedPassword(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        PasswordPreference preference = new PasswordPreference(getApplicationContext());
        if(preference.isAppLocked()){
            setContentView(R.layout.locked_app_layout);
            setLockedLayout();
        }else{
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
        sameApp = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("foo", true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null){
            PasswordPreference preference = new PasswordPreference(getApplicationContext());
            preference.setLockedPassword(false);
        }

    }


	
	private void startPasswordDialog(){
		PasswordDialogFragment newf = new PasswordDialogFragment();
		newf.mContenxt = getApplicationContext();
		newf.show(getFragmentManager(), "password");
	}





    private void setLockedLayout(){
        Button button = (Button) findViewById(R.id.button_unlock_app);
        final EditText editText = (EditText) findViewById(R.id.editText_password_locked_app);
        final PasswordPreference preference = new PasswordPreference(getApplicationContext());
        final String pass = preference.getPassword();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.equals(editText.getText().toString().trim())){
                    preference.setLockedPassword(false);
                    setContentView(R.layout.activity_settings);
                    getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new SettingsFragment())
                            .commit();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.esito_no, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
