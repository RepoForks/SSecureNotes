package com.hooloovoo.securenotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import com.hooloovoo.securenotes.object.TimerUnlock;

import java.util.Timer;
import java.util.TimerTask;


public class SettingsActivity extends Activity {

    SharedPreferences mSharedPreferences;
    boolean browserActivity = false;
    //boolean sameApp = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNavigationBar();
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
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
		/*switch (item.getItemId()) {
		case R.id.imposta_password:
			startPasswordDialog();
			break;
		default:
            return super.onOptionsItemSelected(item);
		}*/
		return super.onOptionsItemSelected(item);
	}

    @Override
    protected void onPause() {
        super.onPause();
        int moreSeconds = 0;
        if(browserActivity) moreSeconds = 40;
        setTimeFinish(moreSeconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimerUnlock timerUnlock = TimerUnlock.getInstance();
        timerUnlock.resetTimer();
    }

    //	private void startPasswordActivity(){
//		Intent intent = new Intent(this,PasswordActivity.class);
//		intent.putExtra("situation", false);
//		startActivity(intent);
//	}
	
	private void startPasswordDialog(){
		PasswordDialogFragment newf = new PasswordDialogFragment();
		newf.mContenxt = getApplicationContext();
		newf.show(getFragmentManager(), "password");
	}


    public void setTimeFinish(int plusSeconds){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int endSeconds = Integer.parseInt(mSharedPreferences.getString("secondWaitToFinish", "10"));
        endSeconds += plusSeconds;
        Log.d("SETTINGSACTIVITY", "Second to wait: " + endSeconds);
        TimerUnlock timerUnlock = TimerUnlock.getInstance();
        timerUnlock.startTime(this,endSeconds);


    }
}
