package com.hooloovoo.securenotes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;


public class SettingsActivity extends Activity {

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
}
