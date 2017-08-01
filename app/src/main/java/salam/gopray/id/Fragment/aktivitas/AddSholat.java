package salam.gopray.id.Fragment.aktivitas;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.azimolabs.keyboardwatcher.KeyboardWatcher;
import com.dunst.check.CheckableImageButton;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.realm.RealmResults;
import salam.gopray.id.MainActivity;
import salam.gopray.id.R;
import salam.gopray.id.database.JadwalSholat;
import salam.gopray.id.database.Timeline;
import salam.gopray.id.database.helper.JadwalSholatHelper;
import salam.gopray.id.database.helper.TimelineHelper;
import salam.gopray.id.service.intent.ServiceTimeline;
import salam.gopray.id.service.intent.UploadTimeline;
import sexy.code.HttPizza;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by root on 04/05/17.
 */

public class AddSholat extends Fragment implements View.OnClickListener,KeyboardWatcher.OnKeyboardToggleListener, ViewTreeObserver.OnGlobalLayoutListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
//    String endUri;
    View v;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;
    CheckableImageButton checkSubuh, checkDhuhur, checkAshar, checkMaghrib, checkIsya, checkSunnah;
    EditText txtBersama, txtTempat;
    Button btnSave;
    TimelineHelper th;
    JadwalSholatHelper jsHelper;
    String tSubuh, tDhuha, tDhuhur,tAshar,tMaghrib,tIsya;
    int width, height;
    RelativeLayout rlTambahSholat;
    ScrollView scTambahSholat;
    int pointWajib = 10, pointSunnah = 5;
    String dSubuh,dDhuha,dDhuhur,dAshar,dMaghrib,dIsya;
    int points;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        socialMediaShare = new ShareSocialMedia(getActivity(), getActivity().getApplicationContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //init encrypting
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        //init encrypting
        /*init network*/
        client = new HttPizza();
        /*init network*/
        /*Timeline Helper*/
        th = new TimelineHelper(getActivity().getApplicationContext());
        jsHelper = new JadwalSholatHelper(getActivity().getApplicationContext());
        /*Timeline Helper*/

//        endUri = getResources().getString(R.string.server)+"/"+getResources().getString(R.string.vServer)+"/"+"users/self/timeline";
        v = inflater.inflate(R.layout.tambah_sholat, container, false);
        rlTambahSholat = (RelativeLayout) v.findViewById(R.id.rlTambahSholat);
        scTambahSholat = (ScrollView) v.findViewById(R.id.scTambahSholat);
        checkSubuh = (CheckableImageButton) v.findViewById(R.id.btnSubuh);
        checkDhuhur = (CheckableImageButton) v.findViewById(R.id.btnDhuhur);
        checkAshar = (CheckableImageButton) v.findViewById(R.id.btnAshar);
        checkMaghrib = (CheckableImageButton) v.findViewById(R.id.btnMaghrib);
        checkIsya = (CheckableImageButton) v.findViewById(R.id.btnIsya);
        checkSunnah= (CheckableImageButton) v.findViewById(R.id.btnSunnah);
        txtBersama = (EditText) v.findViewById(R.id.txtBersama);
        txtTempat = (EditText) v.findViewById(R.id.txtTempat);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
//        focusOnViewTop(scTambahSholat, rlTambahSholat);
        txtBersama.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(scTambahSholat, rlTambahSholat);
                }
            }
        });
        txtTempat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(scTambahSholat, rlTambahSholat);
                }
            }
        });
        getJadwalSholat();
        uncheckButton();
        checkSunnah.setOnClickListener(this);
        checkSubuh.setOnClickListener(this);
        checkDhuhur.setOnClickListener(this);
        checkAshar.setOnClickListener(this);
        checkMaghrib.setOnClickListener(this);
        checkIsya.setOnClickListener(this);

        btnSave = (Button) v.findViewById(R.id.btnSimpan);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveServer();
            }
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        rlTambahSholat.setMinimumHeight(height+100);
        setStatButton();
        int totalHeight = height  / 2;
        int totalWidth = width / 2;
        totalHeight *= -1;
        totalWidth *= -1;
        focusOnViewTop(scTambahSholat, rlTambahSholat);
        scTambahSholat.scrollTo(totalWidth, totalHeight);
        txtBersama.clearFocus();
        txtTempat.clearFocus();
        return v;
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void uncheckButton(){
        checkSubuh.setChecked(FALSE);
        checkDhuhur.setChecked(FALSE);
        checkAshar.setChecked(FALSE);
        checkMaghrib.setChecked(FALSE);
        checkIsya.setChecked(FALSE);
        checkSunnah.setChecked(FALSE);
    }
    public void getJadwalSholat(){
//        String tanggal = (getDateTime());
        String tanggal = getDate();
        JadwalSholat js = new JadwalSholat();
        RealmResults<JadwalSholat> rjs = jsHelper.getJadwalSholat(tanggal);
        if (rjs.size() > 0){
            tSubuh     = rjs.get(0).getSubuh();
            tDhuha     = rjs.get(0).getDhuha();
            tDhuhur    = rjs.get(0).getDhuhur();
            tAshar     = rjs.get(0).getAshar();
            tMaghrib   = rjs.get(0).getMaghrib();
            tIsya      = rjs.get(0).getIsya();
        }
    }
    public void setStatButton(){
        String tanggal = getDate();
        String jam = getTime();
        RealmResults<JadwalSholat> rjs = jsHelper.getJadwalSholat(tanggal);
        if (rjs.size() > 0){

            dSubuh     = rjs.get(0).getSubuh();
            dDhuha     = rjs.get(0).getDhuha();
            dDhuhur    = rjs.get(0).getDhuhur();
            dAshar     = rjs.get(0).getAshar();
            dMaghrib   = rjs.get(0).getMaghrib();
            dIsya      = rjs.get(0).getIsya();
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
        String startDay = "00:00:00";
        String endDay = "23:59:59";
        checkSubuh.setEnabled(false);
        checkDhuhur.setEnabled(false);
        checkAshar.setEnabled(false);
        checkMaghrib.setEnabled(false);
        checkIsya.setEnabled(false);
        long wSekarang = stringToTime(waktuSekarang);
        long wSubuh = stringToTime(waktuSubuh);
        long wDhuha = stringToTime(waktuDhuha);
        long wDhuhur = stringToTime(waktuDhuhur);
        long wAshar = stringToTime(waktuAshar);
        long wMaghrib = stringToTime(waktuMaghrib);
        long wIsya = stringToTime(waktuIsya);
        long wEndDay = stringToTime(endDay);
        String label = "Subuh";
        long remainTime = 0;
        if (wSekarang > wSubuh && wSekarang <= wDhuha){
            checkSubuh.setEnabled(true);
            checkDhuhur.setEnabled(false);
            checkAshar.setEnabled(false);
            checkMaghrib.setEnabled(false);
            checkIsya.setEnabled(false);
        }else if (wSekarang > wDhuhur && wSekarang <= wAshar){
            checkSubuh.setEnabled(false);
            checkDhuhur.setEnabled(true);
            checkAshar.setEnabled(false);
            checkMaghrib.setEnabled(false);
            checkIsya.setEnabled(false);
        }if (wSekarang > wAshar && wSekarang < wMaghrib){
            checkSubuh.setEnabled(false);
            checkDhuhur.setEnabled(false);
            checkAshar.setEnabled(true);
            checkMaghrib.setEnabled(false);
            checkIsya.setEnabled(false);
        }if (wSekarang > wMaghrib && wSekarang < wIsya){
            checkSubuh.setEnabled(false);
            checkDhuhur.setEnabled(false);
            checkAshar.setEnabled(false);
            checkMaghrib.setEnabled(true);
            checkIsya.setEnabled(false);
        }if (wSekarang > wIsya && wSekarang < wEndDay){
            checkSubuh.setEnabled(false);
            checkDhuhur.setEnabled(false);
            checkAshar.setEnabled(false);
            checkMaghrib.setEnabled(false);
            checkIsya.setEnabled(true);
        }
    }
    public void getJadwal(){

    }
    public double getPoint(int point, String start, String end, String current){

        long startTime = stringToTime(start);
        long endTime = stringToTime(end);
        long currentTime = stringToTime(current);
        long selisihTime;
        double minuteTotal, pointPerMenit, diffCurrentTimeinMinute, gotPoint;
        long diffCurrentTime;
        if (currentTime <= endTime){
            selisihTime = endTime - startTime;
            minuteTotal = convertMinute(selisihTime);
            pointPerMenit = (double)(int)point / (double)(int)minuteTotal;
            if (currentTime < endTime){
                diffCurrentTime = endTime - currentTime;
            }else{
                diffCurrentTime = currentTime - endTime;
            }
            diffCurrentTimeinMinute = convertMinute(diffCurrentTime);
            gotPoint = diffCurrentTimeinMinute * pointPerMenit;
        }else{
            gotPoint = 0;
        }
        return gotPoint;
    }
    public void saveServer(){
        String sBersama = txtBersama.getText().toString();
        String sTempat = txtTempat.getText().toString();
        if (sBersama.equalsIgnoreCase("") || sBersama.isEmpty()){
            sBersama = "nothing";
        }
        if (sTempat.equalsIgnoreCase("") || sTempat.isEmpty()){
            sTempat = "nothing";
        }
        if (sTempat.contains("masjid") || sTempat.contains("Masjid") || sTempat.contains("MASJID")){
            points = points * 2;
        }
        int rand = getRandId();
        Timeline tl = new Timeline();
        tl.setId(rand);
        tl.setId_aktivitas(3);
        tl.setId_ibadah(Integer.valueOf(encryptedPreferences.getString("SHOLAT","1")));
        tl.setNama_aktivitas("Sholat");
        tl.setNama_ibadah(encryptedPreferences.getString("NAMA_SHOLAT","Subuh"));
        tl.setImage("tanpa gambar");
        tl.setTempat(sTempat);
        tl.setBersama(sBersama);
        tl.setPoint(points);
        tl.setNominal(0);
        tl.setDate(getDate());
        tl.setJam(getTime());
        tl.setStatus(1);

        th.AddTimelineOffline(tl);
        if(!checkIsRunning(UploadTimeline.class)){
            Intent intenUpload = new Intent(getActivity().getApplicationContext(), UploadTimeline.class);
            getActivity().startService(intenUpload);
        }
        if(!checkIsRunning(ServiceTimeline.class)){
            Intent intentTimeline = new Intent(getActivity().getApplicationContext(), ServiceTimeline.class);
            getActivity().startService(intentTimeline);
        }
        Intent intentActivity = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intentActivity, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity().getApplicationContext())
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("GoPray Point")
                        .setContentText("Point Anda bertambah "+points);

        int mNotificationId = 042;
        NotificationManager mNotifyMgr =
                (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        new LovelyStandardDialog(getActivity())
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_logo)
                .setTitle(R.string.app_name)
                .setMessage("Point Anda Bertambah "+points)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainAktivitas)getActivity()).sendScreen(this.getClass().getName());
    }

    private boolean checkIsRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public int getRandId(){
        int max = 1000000, min = 50000;
        Random rand = new Random();
        int randomNum = rand.nextInt(max - min + 1) + min;
        if (th.checkDuplicate(randomNum)){
            randomNum = getRandId();
        }
        return randomNum;
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
    public String getDifferenceTime(String time1, String time2){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(time1);
            d2 = format.parse(time2);
        } catch (ParseException e) {
            ((MainAktivitas)getActivity()).sendException(e);
            e.printStackTrace();
        }
        long diff = (d2.getTime() - d1.getTime());
        long diffSeconds = (diff / 1000)%60;
        long diffMinutes = (diff / (60 * 1000))%60;
        long diffHours = (diff / (60 * 60 * 1000))%60;
        String result = diffHours +":"+ diffMinutes +":"+diffSeconds;
        return result;
    }
    public void checkDifference(){
        if (!encryptedPreferences.getString("JAM_TERAKHIR", "").equalsIgnoreCase("")){
            String time1 = getTime();
            String time2 = encryptedPreferences.getString("JAM_TERAKHIR", "");

            String difference = getDifferenceTime(time1, time2);
        }
    }
    @Override
    public void onClick(View v) {
        double pointx;
        Long L;
        uncheckButton();

        switch (v.getId()){
            case R.id.btnSubuh :
                pointx = getPoint(pointWajib, dSubuh, dDhuha, getTime());
                L = Math.round(pointx);
                points = Integer.valueOf(L.intValue());
                encryptedPreferences.edit().putString("SHOLAT", "1").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Subuh").commit();
                checkSubuh.setChecked(TRUE);
                break;
            case R.id.btnDhuhur :
                pointx = getPoint(pointWajib, dDhuhur, dAshar, getTime());
                L = Math.round(pointx);
                points = Integer.valueOf(L.intValue());
                encryptedPreferences.edit().putString("SHOLAT", "2").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Dhuhur").commit();
                checkDhuhur.setChecked(TRUE);
                break;
            case R.id.btnAshar :
                pointx = getPoint(pointWajib, dAshar, dMaghrib, getTime());
                L = Math.round(pointx);
                points = Integer.valueOf(L.intValue());
                encryptedPreferences.edit().putString("SHOLAT", "3").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Ashar").commit();
                checkAshar.setChecked(TRUE);
                break;
            case R.id.btnMaghrib :
                pointx = getPoint(pointWajib, dMaghrib, dIsya, getTime());
                L = Math.round(pointx);
                points = Integer.valueOf(L.intValue());

                encryptedPreferences.edit().putString("SHOLAT", "4").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Maghrib").commit();
                checkMaghrib.setChecked(TRUE);
                break;
            case R.id.btnIsya :
                pointx = getPoint(pointWajib, dIsya, "23:59:59", getTime());
                L = Math.round(pointx);
                points = Integer.valueOf(L.intValue());
                encryptedPreferences.edit().putString("SHOLAT", "5").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Isya").commit();
                checkIsya.setChecked(TRUE);
                break;
            case R.id.btnSunnah :
                points = pointSunnah;
                encryptedPreferences.edit().putString("SHOLAT", "6").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Sunnah").commit();
                checkSunnah.setChecked(TRUE);
                break;
        }
    }
    private final void focusOnViewTop(final View parent, final View v){
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.scrollTo(0, v.getTop());
            }
        });
    }

    private final void focusOnView(final View parent, final View v){
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.scrollTo(0, v.getBottom());
            }
        });
    }

    @Override
    public void onGlobalLayout() {

    }

    @Override
    public void onKeyboardShown(int keyboardSize) {

    }

    @Override
    public void onKeyboardClosed() {

    }
    public long stringToTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        Date d1 = null;
        try {
            d1 = format.parse(time);
        } catch (ParseException e) {
            ((MainAktivitas)getActivity()).sendException(e);
            e.printStackTrace();
        }
        return d1.getTime();
    }
    public double convertMinute(long time){
        long diffSeconds = ((time / 1000)%60) / 60;
        long diffMinutes = (time / (60 * 1000))%60;
        long diffHours = ((time / (60 * 60 * 1000))%60) * 60;
        long totalinminute = diffSeconds + diffMinutes + diffHours;
        double i = (double) (long) totalinminute;
        return i;
    }
    public String getRemainTime(long time){
        long diffSeconds = (time / 1000)%60;
        long diffMinutes = (time / (60 * 1000))%60;
        long diffHours = (time / (60 * 60 * 1000))%60;
        String remainTime = String.valueOf(Math.abs(diffHours))+" Jam "+String.valueOf(Math.abs(diffMinutes))+" Menit";
        return remainTime;
    }

}
