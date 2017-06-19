package salam.gopray.id.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import salam.gopray.id.MainActivity;
import salam.gopray.id.service.intent.JadwalSholatService;
import salam.gopray.id.service.intent.MasterKerabatService;
import salam.gopray.id.service.intent.MessageParentService;
import salam.gopray.id.service.intent.ProfilePictureService;
import salam.gopray.id.service.intent.QuoteService;
import salam.gopray.id.service.intent.ServiceTimeline;
import salam.gopray.id.service.intent.UploadTimeline;

/**
 * Created by root on 02/05/17.
 */

public class MainServices extends Service {
    public static Boolean serviceRunning = false;
    public static final long NOTIFY_INTERVAL = 5 * 1000;
    public static final long NOTIFY_INTERVAL_JADWAL = 2592 * 1000; /*30 hari*/
    private Handler handler = new Handler();
    private Handler handlerJadwal = new Handler();
    private Timer timer = null;
    private Timer timerJadwal = null;
    public static final String
            ACTION_LOCATION_BROADCAST = MainActivity.class.getName();

    @Override
    public void onCreate() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

        if (timerJadwal != null) {
            timerJadwal.cancel();
        }
        timerJadwal = new Timer();
        timerJadwal.scheduleAtFixedRate(new TimeDisplayTimerTaskJadwal(), 0, NOTIFY_INTERVAL_JADWAL);
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
    private boolean checkIsRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    class TimeDisplayTimerTaskJadwal extends TimerTask{

        @Override
        public void run() {
            handlerJadwal.post(new Runnable() {
                @Override
                public void run() {
                    if(!checkIsRunning(JadwalSholatService.class)){
                        Intent jss = new Intent(getBaseContext(), JadwalSholatService.class);
                        startService(jss);
                    }
                }
            });
        }
    }
    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!checkIsRunning(ServiceTimeline.class)){
                        Intent is = new Intent(getBaseContext(), ServiceTimeline.class);
                        startService(is);
                    }
                    if(!checkIsRunning(UploadTimeline.class)){
                        Intent uis = new Intent(getApplicationContext(), UploadTimeline.class);
                        startService(uis);
                    }
                    if(!checkIsRunning(ProfilePictureService.class)){
                        Intent pp = new Intent(getApplicationContext(), ProfilePictureService.class);
                        startService(pp);
                    }
                    if(!checkIsRunning(QuoteService.class)){
                        Intent pp = new Intent(getApplicationContext(), QuoteService.class);
                        startService(pp);
                    }
                    if(!checkIsRunning(MessageParentService.class)){
                        Intent mpp = new Intent(getApplicationContext(), MessageParentService.class);
                        startService(mpp);
                    }
                    if(!checkIsRunning(MasterKerabatService.class)){
                        Intent mks = new Intent(getApplicationContext(), MasterKerabatService.class);
                        startService(mks);
                    }
                }
            });
        }
    }
}
