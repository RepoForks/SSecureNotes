package com.hooloovoo.securenotes.object;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by angelo on 15/01/14.
 */
public class TimerUnlock {
    public static int RESULT_TIME_UNLOCK = 1000;
    private static TimerUnlock timer;
    int seconds = 0;
    Timer t;

    private TimerUnlock(){
        t = new Timer();
    }

    static public TimerUnlock getInstance(){
        if(timer==null){timer = new TimerUnlock();}
        return timer;
    }

    public void startTime(Activity activity, int toSeconds){
        Log.d("TIMERUNLOCK", "start timer");
        t = new Timer();
        final Activity mActivity = activity;
        final int endSeconds = toSeconds;

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if( seconds == endSeconds ){

                            resetTimer();
                            seconds = 0;
                            //mActivity.setResult(RESULT_TIME_UNLOCK);
                            //tolgo la password
                            Log.d("TIMERUNLOCK", "sto per chiudere");
                            ActivityCompat compat = new ActivityCompat();
                            compat.finishAffinity(mActivity);
                            mActivity.finish();
                        }
                        seconds += 1;
                    }
                });

            }
        }, 0, 1000);
    }

    public void resetTimer(){
        if(t!=null){
            t.cancel();
            t.purge();
        }
    }
}
