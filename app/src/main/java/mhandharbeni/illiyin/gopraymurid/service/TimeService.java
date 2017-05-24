package mhandharbeni.illiyin.gopraymurid.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import mhandharbeni.illiyin.gopraymurid.MainActivity;
import mhandharbeni.illiyin.gopraymurid.service.intent.JadwalSholatService;
import mhandharbeni.illiyin.gopraymurid.service.intent.ServiceTimeline;

public class TimeService extends Service {
    public static Boolean serviceRunning = false;
    public static final long NOTIFY_INTERVAL = 1 * 1000;
    private Handler handler = new Handler();
    private Timer timer = null;

    @Override
    public void onCreate() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimeService.TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }
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
    private void sendBroadcastMessage() {
        this.sendBroadcast(new Intent().setAction("UPDATE TIMELINE").putExtra("MODE", "UPDATE TIME"));
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    sendBroadcastMessage();
                }
            });
        }
    }
}
