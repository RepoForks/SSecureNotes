package com.hooloovoo.securenotes;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import com.hooloovoo.securenotes.object.TimerUnlock;

public class InfoActivity extends Activity {

    SharedPreferences mSharedPreferences;
    Typeface font;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setNavigationBar();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

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
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("INFOACTIVITY", "close Timer");
        TimerUnlock timerUnlock = TimerUnlock.getInstance();
        timerUnlock.resetTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setTimeFinish();
    }

    private void setNavigationBar(){
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
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

    private void setTimeFinish(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int endSeconds = Integer.parseInt(mSharedPreferences.getString("secondWaitToFinish", "10"));

        Log.d("NOTESACTIVITY", "Second to wait: " + endSeconds);
        TimerUnlock timerUnlock = TimerUnlock.getInstance();
        timerUnlock.startTime(this,endSeconds);
    }

    private void startBroserActivity(String url){
        String mUrl = "https://"+url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
        startActivity(browserIntent);
    }

}
