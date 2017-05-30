package mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dunst.check.CheckableImageButton;
//import com.facebook.CallbackManager;
//import com.facebook.share.widget.ShareDialog;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.realm.RealmResults;
import mhandharbeni.illiyin.gopraymurid.Fragment.share.ShareSocialMedia;
import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.database.JadwalSholat;
import mhandharbeni.illiyin.gopraymurid.database.Timeline;
import mhandharbeni.illiyin.gopraymurid.database.helper.JadwalSholatHelper;
import mhandharbeni.illiyin.gopraymurid.database.helper.TimelineHelper;
import mhandharbeni.illiyin.gopraymurid.service.intent.ServiceTimeline;
import mhandharbeni.illiyin.gopraymurid.service.intent.UploadTimeline;
import sexy.code.HttPizza;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by root on 04/05/17.
 */

public class AddSholat extends Fragment implements View.OnClickListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    String endUri;
    View v;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;
    CheckableImageButton checkSubuh, checkDhuhur, checkAshar, checkMaghrib, checkIsya, checkSunnah;
    EditText txtBersama, txtTempat;
    Button btnSave;
    TimelineHelper th;
    JadwalSholatHelper jsHelper;
    String tSubuh, tDhuha, tDhuhur,tAshar,tMaghrib,tIsya;

    //ShareDialog shareDialog;
    //CallbackManager callbackManager;

//    ShareSocialMedia socialMediaShare;

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

        endUri = getResources().getString(R.string.server)+"/"+getResources().getString(R.string.vServer)+"/"+"users/self/timeline";
        v = inflater.inflate(R.layout.tambah_sholat, container, false);

        checkSubuh = (CheckableImageButton) v.findViewById(R.id.btnSubuh);
        checkDhuhur = (CheckableImageButton) v.findViewById(R.id.btnDhuhur);
        checkAshar = (CheckableImageButton) v.findViewById(R.id.btnAshar);
        checkMaghrib = (CheckableImageButton) v.findViewById(R.id.btnMaghrib);
        checkIsya = (CheckableImageButton) v.findViewById(R.id.btnIsya);
        checkSunnah= (CheckableImageButton) v.findViewById(R.id.btnSunnah);
        txtBersama = (EditText) v.findViewById(R.id.txtBersama);
        txtTempat = (EditText) v.findViewById(R.id.txtTempat);
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

        return v;
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
        String tanggal = "2017-04-11";
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
    public void saveServer(){
        String sBersama = txtBersama.getText().toString();
        String sTempat = txtTempat.getText().toString();

        if (sBersama.equalsIgnoreCase("") || sBersama.isEmpty()){
            sBersama = "nothing";
        }
        if (sTempat.equalsIgnoreCase("") || sTempat.isEmpty()){
            sTempat = "nothing";
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
        tl.setPoint(1);
        tl.setNominal(0);
        tl.setDate(getDate());
        tl.setJam(getTime());
        tl.setStatus(1);

        th.AddTimelineOffline(tl);
        /*run service*/
        if(!checkIsRunning(UploadTimeline.class)){
            Intent intenUpload = new Intent(getActivity().getApplicationContext(), UploadTimeline.class);
            getActivity().startService(intenUpload);
        }
        if(!checkIsRunning(ServiceTimeline.class)){
            Intent intentTimeline = new Intent(getActivity().getApplicationContext(), ServiceTimeline.class);
            getActivity().startService(intentTimeline);
        }
//        socialMediaShare.shareTwitter("MESSAGE TWITTER", "http://developers.facebook.com/android");
//        socialMediaShare.shareFacebook("TITLE", "DESCRIPTION", "http://developers.facebook.com/android");
//        shareInstagram("image/*", "");
//        shareFacebook();
        /*run service*/
        getActivity().finish();
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
            Log.d("ADD SHOLAT", "checkDifference: "+time1);
            Log.d("ADD SHOLAT", "checkDifference: "+time2);
            Log.d("ADD SHOLAT", "checkDifference: "+String.valueOf(difference));
        }
    }
    @Override
    public void onClick(View v) {
        uncheckButton();

        switch (v.getId()){
            case R.id.btnSubuh :
                encryptedPreferences.edit().putString("SHOLAT", "1").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Subuh").commit();
//                encryptedPreferences.edit().putString("JAM_TERAKHIR", tDhuha).commit();
                checkSubuh.setChecked(TRUE);
                break;
            case R.id.btnDhuhur :
                encryptedPreferences.edit().putString("SHOLAT", "2").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Dhuhur").commit();
//                encryptedPreferences.edit().putString("JAM_TERAKHIR", tAshar).commit();
                checkDhuhur.setChecked(TRUE);
                break;
            case R.id.btnAshar :
                encryptedPreferences.edit().putString("SHOLAT", "3").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Ashar").commit();
//                encryptedPreferences.edit().putString("JAM_TERAKHIR", tMaghrib).commit();
                checkAshar.setChecked(TRUE);
                break;
            case R.id.btnMaghrib :
                encryptedPreferences.edit().putString("SHOLAT", "4").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Maghrib").commit();
//                encryptedPreferences.edit().putString("JAM_TERAKHIR", tIsya).commit();
                checkMaghrib.setChecked(TRUE);
                break;
            case R.id.btnIsya :
                encryptedPreferences.edit().putString("SHOLAT", "5").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Isya").commit();
//                encryptedPreferences.edit().putString("JAM_TERAKHIR", tSubuh).commit();
                checkIsya.setChecked(TRUE);
                break;
            case R.id.btnSunnah :
                encryptedPreferences.edit().putString("SHOLAT", "6").commit();
                encryptedPreferences.edit().putString("NAMA_SHOLAT", "Sunnah").commit();
//                encryptedPreferences.edit().putString("JAM_TERAKHIR", "").commit();
                checkSunnah.setChecked(TRUE);
                break;
        }
//        checkDifference();
    }
}
