package salam.gopray.id.Fragment.aktivitas.sub;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import salam.gopray.id.MainActivity;
import salam.gopray.id.R;
import salam.gopray.id.database.Timeline;
import salam.gopray.id.database.helper.TimelineHelper;
import salam.gopray.id.service.intent.ServiceTimeline;
import salam.gopray.id.service.intent.UploadTimeline;
import salam.gopray.id.util.KeyboardHeightObserver;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by root on 19/06/17.
 */

public class TextFragment extends Fragment implements KeyboardHeightObserver {
    View v;
    EncryptedPreferences encryptedPreferences;


    TimelineHelper th;


    Button btnPost;
    EditText txtStatus, txtBersama, txtTempat;

    ScrollView svTextFreeText;
    RelativeLayout rlMain;

    int widthx, heightx;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.text_fragment_freetext, container, false);

        svTextFreeText = (ScrollView) v.findViewById(R.id.svTextFreeText);
        rlMain = (RelativeLayout) v.findViewById(R.id.rlMain);

        encryptedPreferences = new EncryptedPreferences.Builder(getActivity().getApplicationContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        th = new TimelineHelper(getActivity().getApplicationContext());

        btnPost = (Button) v.findViewById(R.id.btnPost);
        txtStatus = (EditText) v.findViewById(R.id.txtStatus);
        txtBersama = (EditText) v.findViewById(R.id.txtBersama);
        txtTempat = (EditText) v.findViewById(R.id.txtTempat);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFreeText();
            }
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        widthx = displayMetrics.widthPixels;
        heightx = displayMetrics.heightPixels;

        return v;
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
    public void postFreeText(){
        int points = 10;
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
        tl.setId_aktivitas(5);
        tl.setId_ibadah(1);
        tl.setNama_aktivitas("");
        tl.setNama_ibadah("");
        tl.setImage(txtStatus.getText().toString());
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
    private final void focusOnView(final View parent, final View v){
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.scrollTo(0, v.getBottom());
            }
        });
    }
    private void resizeView(View view, int newWidth, int newHeight) {
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        view.setLayoutParams(params);
        view.requestLayout();
        view.invalidate();
    }
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";

        View view = v.findViewById(R.id.keyboards);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view .getLayoutParams();
        params.height = height;

        Double iHeight = height==0? 0 : (height*0.3)+300;
        svTextFreeText.setMinimumHeight(heightx+iHeight.intValue());
        rlMain.setMinimumHeight(heightx+iHeight.intValue());

        resizeView(svTextFreeText, widthx, heightx+iHeight.intValue());
        resizeView(rlMain, widthx, heightx+iHeight.intValue());
        focusOnView(svTextFreeText, rlMain);
    }
}
