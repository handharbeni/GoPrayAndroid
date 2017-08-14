package salam.gopray.id.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.RealmResults;
import salam.gopray.id.MainActivity;
import salam.gopray.id.R;
import salam.gopray.id.database.JadwalSholat;
import salam.gopray.id.database.helper.JadwalSholatHelper;

public class TimeService extends Service {
    public static Boolean serviceRunning = false;
    public static final long NOTIFY_INTERVAL = 1 * 1000;
    public static final long NOTIFY_POPUP = 10 * 1000;
    private Handler handler = new Handler();
    private Handler handlerPopup = new Handler();
    private Timer timer = null;
    private Timer timerPopup = null;
    JadwalSholatHelper jsHelper;
    @Override
    public void onCreate() {

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimeService.TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

        if (timerPopup != null){
            timerPopup.cancel();
        }
        timerPopup = new Timer();
        timerPopup.scheduleAtFixedRate(new TimeService.TimeDisplayTimerTaskPopup(), 0, NOTIFY_POPUP);
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
    private void innerFunction(){
        jsHelper = new JadwalSholatHelper(getApplicationContext());
        getCurrentSholat();
        jsHelper.closeRealm();
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
    class TimeDisplayTimerTaskPopup extends TimerTask{
        @Override
        public void run() {
            handlerPopup.post(new Runnable() {
                @Override
                public void run() {
                    innerFunction();
                }
            });
        }

    }
    public void getCurrentSholat(){
        String tanggal = getDate();
        String jam = getTime();
        RealmResults<JadwalSholat> rjs = jsHelper.getJadwalSholat(tanggal);
        if (rjs.size() > 0){

            String dSubuh     = rjs.get(0).getSubuh();
            String dDhuha     = rjs.get(0).getDhuha();
            String dDhuhur    = rjs.get(0).getDhuhur();
            String dAshar     = rjs.get(0).getAshar();
            String dMaghrib   = rjs.get(0).getMaghrib();
            String dIsya      = rjs.get(0).getIsya();
            checkLewatWaktu(jam,dSubuh,dDhuha,dDhuhur,dAshar,dMaghrib,dIsya);

        }
    }
    public void checkLewatWaktu(String waktuSekarang,
                                String waktuSubuh,
                                String waktuDhuha,
                                String waktuDhuhur,
                                String waktuAshar,
                                String waktuMaghrib,
                                String waktuIsya){

        String remain = "";

        long wSekarang = stringToTime(waktuSekarang);
        long wSubuh = stringToTime(waktuSubuh);
        long wDhuha = stringToTime(waktuDhuha);
        long wDhuhur = stringToTime(waktuDhuhur);
        long wAshar = stringToTime(waktuAshar);
        long wMaghrib = stringToTime(waktuMaghrib);
        long wIsya = stringToTime(waktuIsya);
        String label = "Subuh";
        long remainTime = 0;
        if (wSekarang == wSubuh){
            showNotifTemporary("SAATNYA SHOLAT SUBUH", "Klik Disini Untuk Menambah Aktivitas");
        }else if(wSekarang == wDhuhur){
            showNotifTemporary("SAATNYA SHOLAT DHUHUR", "Klik Disini Untuk Menambah Aktivitas");
        }else if(wSekarang == wAshar){
            showNotifTemporary("SAATNYA SHOLAT ASHAR", "Klik Disini Untuk Menambah Aktivitas");
        }else if(wSekarang == wMaghrib){
            showNotifTemporary("SAATNYA SHOLAT MAGHRIB", "Klik Disini Untuk Menambah Aktivitas");
        }else if(wSekarang == wIsya){
            showNotifTemporary("SAATNYA SHOLAT ISYA", "Klik Disini Untuk Menambah Aktivitas");
        }


        if (wSekarang < wSubuh){
            label = "Subuh";
            remainTime = wSekarang - wSubuh;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wDhuha){
            label = "Dhuha";
            remainTime = wSekarang - wDhuha;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wDhuhur){
            label = "Dhuhur";
            remainTime = wSekarang - wDhuhur;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wAshar){
            label = "Ashar";
            remainTime = wSekarang - wAshar;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wMaghrib){
            label = "Maghrib";
            remainTime = wSekarang - wMaghrib;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wIsya){
            label = "Isya";
            remainTime = wSekarang - wIsya;
            remain = getRemainTime(remainTime);
        }else if (wSekarang > wIsya){
            label = "Subuh";
            remain = "Besok Jam "+waktuSubuh;
        }
        showNotif(label, remain);
    }
    public long stringToTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        try {
            d1 = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1.getTime();
    }
    public String getRemainTime(long time){
        long diffSeconds = (time / 1000)%60;
        long diffMinutes = (time / (60 * 1000))%60;
        long diffHours = (time / (60 * 60 * 1000))%60;
        String remainTime = String.valueOf(Math.abs(diffHours))+" Jam "+String.valueOf(Math.abs(diffMinutes))+" Menit";
        return remainTime;
    }
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    public void showNotif(String title, String text){
        Intent intentActivity = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentActivity, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setOngoing(true);

        int mNotificationId = 042;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
    public void showNotifTemporary(String title, String text){
        Intent intentActivity = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentActivity, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(title)
                        .setContentText(text);

        int mNotificationId = 045;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(mNotificationId);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
