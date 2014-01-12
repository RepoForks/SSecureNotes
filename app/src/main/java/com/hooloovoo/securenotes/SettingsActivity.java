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

import java.util.Timer;
import java.util.TimerTask;


public class SettingsActivity extends Activity {

    SharedPreferences mSharedPreferences;
    int seconds;

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


    public Timer setTimeFinish(){
        final Timer t;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int endSeconds = Integer.parseInt(mSharedPreferences.getString("secondWaitToFinish", "10"));
        Log.d("NOTESACTIVITY", "Second to wait: " + endSeconds);
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if( seconds == endSeconds ){

                            t.cancel();
                            t.purge();
                            seconds = 0;
                            finishAffinity();
                        }
                        seconds += 1;
                    }
                });

            }
        }, 0, 1000);

        return t;



    }
}
