package salam.gopray.id.Fragment.aktivitas;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import salam.gopray.id.Fragment.share.ShareSocialMedia;
import salam.gopray.id.R;
import salam.gopray.id.database.Timeline;
import salam.gopray.id.database.helper.TimelineHelper;
import salam.gopray.id.service.intent.ServiceTimeline;
import salam.gopray.id.service.intent.UploadTimeline;
import sexy.code.HttPizza;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by root on 12/05/17.
 */

public class AddMengaji extends Fragment {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    View v;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;
    EditText txtAyat, txtTempat;
    SearchableSpinner txtSurat;
    Button btnSave;
    TimelineHelper th;
    ShareSocialMedia shareSocialMedia;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        client = new HttPizza();
        th = new TimelineHelper(getActivity().getApplicationContext());
        v = inflater.inflate(R.layout.tambah_mengaji, container, false);
        txtSurat = (SearchableSpinner) v.findViewById(R.id.txtMengaji);
        txtAyat = (EditText) v.findViewById(R.id.txtAyat);
        txtTempat = (EditText) v.findViewById(R.id.txtTempat);
        btnSave = (Button) v.findViewById(R.id.btnSimpan);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveServer();
            }
        });
//        shareSocialMedia = new ShareSocialMedia(getActivity(), getActivity().getApplication());
/*        ArrayAdapter<String> spinnerSurat = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.surat));
        txtSurat.setAdapter(spinnerSurat);*/
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainAktivitas)getActivity()).sendScreen(this.getClass().getName());
    }
    public void saveServer(){
        String sSurat = "Surat "+txtSurat.getSelectedItem().toString();
        String sAyat = txtAyat.getText().toString();
        String sTempat = txtTempat.getText().toString();

        if (sSurat.equalsIgnoreCase("") || sSurat.isEmpty()){
            sSurat = "";
        }
        if (sAyat.equalsIgnoreCase("") || sAyat.isEmpty()){
            sAyat = "";
        }else{
            sAyat = "Ayat "+txtAyat.getText().toString();
        }
        if (sTempat.equalsIgnoreCase("") || sTempat.isEmpty()){
            sTempat = "nothing";
        }
        int rand = getRandId();
        Timeline tl = new Timeline();
        tl.setId(rand);
        tl.setId_aktivitas(7);
        tl.setId_ibadah(1);
        tl.setNama_aktivitas("Mengaji");
        tl.setNama_ibadah("Mengaji");
        tl.setImage("tanpa gambar");
        tl.setTempat(sTempat);
        tl.setBersama(sSurat+" "+sAyat);
        tl.setPoint(5);
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
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity().getApplicationContext())
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle("GoPray Point")
                        .setContentText("Point Anda bertambah 5");

        int mNotificationId = 042;
        NotificationManager mNotifyMgr =
                (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        new LovelyStandardDialog(getActivity())
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_logo)
                .setTitle(R.string.app_name)
                .setMessage("Point Anda Bertambah 5")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                })
                .show();
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}
