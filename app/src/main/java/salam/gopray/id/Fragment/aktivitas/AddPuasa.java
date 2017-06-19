package salam.gopray.id.Fragment.aktivitas;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.golovin.fluentstackbar.FluentSnackbar;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.realm.RealmResults;
import salam.gopray.id.Fragment.share.ShareSocialMedia;
import salam.gopray.id.R;
import salam.gopray.id.database.JadwalSholat;
import salam.gopray.id.database.Timeline;
import salam.gopray.id.database.helper.JadwalSholatHelper;
import salam.gopray.id.database.helper.TimelineHelper;
import salam.gopray.id.service.intent.ServiceTimeline;
import salam.gopray.id.service.intent.UploadTimeline;
import sexy.code.HttPizza;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by root on 12/05/17.
 */

public class AddPuasa extends Fragment {
    private FluentSnackbar mFluentSnackbar;
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    View v;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;
    SearchableSpinner txtPuasa;
    Button btnSave;
    TimelineHelper th;
    JadwalSholatHelper jsHelper;

    String dSubuh,dDhuha,dDhuhur,dAshar,dMaghrib,dIsya;
    ShareSocialMedia shareSocialMedia;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        client = new HttPizza();
        th = new TimelineHelper(getActivity().getApplicationContext());
        jsHelper = new JadwalSholatHelper(getActivity().getApplicationContext());
        v = inflater.inflate(R.layout.tambah_puasa, container, false);
        txtPuasa = (SearchableSpinner) v.findViewById(R.id.txtPuasa);
        btnSave = (Button) v.findViewById(R.id.btnSimpan);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveServer();
            }
        });
//        shareSocialMedia = new ShareSocialMedia(getActivity(), getActivity().getApplicationContext());
/*        ArrayAdapter<String> spinnerSurat = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.surat));
        txtSurat.setAdapter(spinnerSurat);*/
        getJadwal();
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainAktivitas)getActivity()).sendScreen(this.getClass().getName());
    }
    public void getJadwal(){
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
        }
    }
    public long stringToTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        try {
            d1 = format.parse(time);
        } catch (ParseException e) {
            ((MainAktivitas)getActivity()).sendException(e);
            e.printStackTrace();
        }
        return d1.getTime();
    }
    public void saveServer(){
        long currentTime = stringToTime(getTime());
        long maghribTime = stringToTime(dMaghrib);
        Log.d("PUASA", "saveServer: "+currentTime);
        Log.d("PUASA", "saveServer: "+maghribTime);
        if (currentTime > maghribTime){
            String sSurat = "Puasa "+txtPuasa.getSelectedItem().toString();
            int iSurat = txtPuasa.getSelectedItemPosition()+1;
            if (sSurat.equalsIgnoreCase("") || sSurat.isEmpty()){
                sSurat = "";
            }

            int rand = getRandId();
            Timeline tl = new Timeline();
            tl.setId(rand);
            tl.setId_aktivitas(2);
            tl.setId_ibadah(iSurat);
            tl.setNama_aktivitas("Mengaji");
            tl.setNama_ibadah(sSurat);
            tl.setImage("nothing");
            tl.setTempat("nothing");
            tl.setBersama("nothing");
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

        }else{
            showSnackBar("Maaf, Belum waktunya berbuka");
        }
    }
    public void showSnackBar(String message){
        mFluentSnackbar = FluentSnackbar.create(getActivity());
        mFluentSnackbar.create(message)
                .maxLines(2)
                .backgroundColorRes(R.color.colorPrimary)
                .textColorRes(R.color.indicator)
                .duration(Snackbar.LENGTH_SHORT)
                .actionText(message)
                .actionTextColorRes(R.color.colorAccent)
                .important()
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}
