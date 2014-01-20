package com.hooloovoo.securenotes;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hooloovoo.securenotes.object.PasswordPreference;

public class InfoActivity extends Activity {

    SharedPreferences mSharedPreferences;
    Typeface font;

    boolean sameApp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //setNavigationBar();


      font = Typeface.createFromAsset(getAssets(), "fonts/EarlyGameBoy.ttf");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            sameApp = true;
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        sameApp = true;
    }

    private void setNavigationBar(){
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setLockedLayout(){
        Button button = (Button) findViewById(R.id.button_unlock_app);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/EarlyGameBoy.ttf");
        ((TextView)findViewById(R.id.app_locked_title_dialog)).setTypeface(font);
        ((Button)findViewById(R.id.button_unlock_app)).setTypeface(font);
        ((TextView)findViewById(R.id.editText_password_locked_app)).setTypeface(font);
        final EditText editText = (EditText) findViewById(R.id.editText_password_locked_app);
        final PasswordPreference preference = new PasswordPreference(getApplicationContext());
        final String pass = preference.getPassword();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.equals(editText.getText().toString().trim())){
                    preference.setLockedPassword(false);
                    setContentView(R.layout.activity_info);
                    getFragmentManager().beginTransaction()
                            .add(R.id.container, new PlaceholderFragment())
                            .commit();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.esito_no, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        TextView code;
        InfoActivity mActivity;
        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = (InfoActivity) activity;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);
            Typeface font = Typeface.createFromAsset(rootView.getContext().getAssets(), "fonts/EarlyGameBoy.ttf");
            ((TextView)rootView.findViewById(R.id.textView_appname2)).setTypeface(font);
            ((TextView)rootView.findViewById(R.id.textView_versione)).setTypeface(font);
            ((TextView)rootView.findViewById(R.id.license)).setTypeface(font);
            code = (TextView) rootView.findViewById(R.id.textView_code);
            code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startBroserActivity(code.getText().toString());
                }
            });
            return rootView;
        }
    }



    private void startBroserActivity(String url){
        String mUrl = "https://"+url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
        startActivity(browserIntent);
    }

}
