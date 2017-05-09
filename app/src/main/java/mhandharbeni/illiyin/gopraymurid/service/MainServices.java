package mhandharbeni.illiyin.gopraymurid.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

import mhandharbeni.illiyin.gopraymurid.MainActivity;
import mhandharbeni.illiyin.gopraymurid.service.intent.JadwalSholatService;
import mhandharbeni.illiyin.gopraymurid.service.intent.ServiceTimeline;
import mhandharbeni.illiyin.gopraymurid.service.intent.UploadTimeline;

/**
 * Created by root on 02/05/17.
 */

public class MainServices extends Service {
    public static Boolean serviceRunning = false;
    public static final long NOTIFY_INTERVAL = 15 * 1000;
    private Handler handler = new Handler();
    private Timer timer = null;
    public static final String
            ACTION_LOCATION_BROADCAST = MainActivity.class.getName();

    @Override
    public void onCreate() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        serviceRunning = false;
        super.onDestroy();
    }
    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    /*TIMELINE SERVICE*/
                    Intent is = new Intent(getBaseContext(), ServiceTimeline.class);
                    startService(is);
                    /*TIMELINE SERVICE*/
                    /*JADWAL SHOLAT SERVICE*/
                    Intent jss = new Intent(getBaseContext(), JadwalSholatService.class);
                    startService(jss);
                    /*TIMELINE SERVICE*/
                    /*UPLOAD TIMELINE SERVICE*/
                    Intent uis = new Intent(getApplicationContext(), UploadTimeline.class);
                    startService(uis);
                    /*UPLOAD TIMELINE SERVICE*/
                }
            });
        }
    }
}
