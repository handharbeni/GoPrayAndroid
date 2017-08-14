package salam.gopray.id.Fragment.aktivitas.sub;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.realm.RealmResults;
import okhttp3.RequestBody;
import salam.gopray.id.Fragment.aktivitas.sub.stiker.StikerFragment;
import salam.gopray.id.MainActivity;
import salam.gopray.id.R;
import salam.gopray.id.database.MasterStiker;
import salam.gopray.id.database.Timeline;
import salam.gopray.id.database.TransaksiStiker;
import salam.gopray.id.database.helper.MasterStikerHelper;
import salam.gopray.id.database.helper.TimelineHelper;
import salam.gopray.id.database.helper.TransaksiStikerHelper;
import salam.gopray.id.service.intent.ServiceTimeline;
import salam.gopray.id.service.intent.UploadTimeline;
import salam.gopray.id.util.FreeTextInterface;
import salam.gopray.id.util.KeyboardHeightObserver;
import salam.gopray.id.util.KeyboardHeightProvider;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by root on 19/06/17.
 */

public class ImageFragment extends Fragment implements KeyboardHeightObserver, EncryptedPreferences.OnSharedPreferenceChangeListener, FreeTextInterface {
    View v;

    private final static String TAG = "ImageFreeFragment";
    private KeyboardHeightProvider keyboardHeightProvider;
    EncryptedPreferences encryptedPreferences;

    MasterStikerHelper masterStikerHelper;
    TransaksiStikerHelper transaksiStikerHelper;
    TimelineHelper th;

    ScrollView svStiker;
    RelativeLayout imageFreeTextLayout;

    int widthx, heightx;
    TabLayout tabLayout;
    ImageView txtImage;

    Button btnPost;
    EditText txtStatus, txtBersama, txtTempat;

    TabHost tabHost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterStikerHelper = new MasterStikerHelper(getActivity().getApplicationContext());
        transaksiStikerHelper = new TransaksiStikerHelper(getActivity().getApplicationContext());
        th = new TimelineHelper(getActivity().getApplicationContext());

        encryptedPreferences = new EncryptedPreferences.Builder(getActivity().getApplicationContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        encryptedPreferences.registerOnSharedPreferenceChangeListener(this);

        v = inflater.inflate(R.layout.image_fragment_freetext, container, false);

        svStiker = (ScrollView) v.findViewById(R.id.svStiker);
        imageFreeTextLayout = (RelativeLayout) v.findViewById(R.id.imageFreeTextLayout);

        initDataDummy();
        initTab();

        txtImage = (ImageView) v.findViewById(R.id.txtImage);
        txtImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStiker();
            }
        });

        btnPost = (Button) v.findViewById(R.id.btnPost);
        txtBersama = (EditText) v.findViewById(R.id.txtBersama);
        txtTempat = (EditText) v.findViewById(R.id.txtTempat);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFreeText();
            }
        });
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());

        return v;
    }
    public void toggleStiker(){
        RelativeLayout layoutStiker = (RelativeLayout) v.findViewById(R.id.layoutStiker);
        if (layoutStiker.getVisibility() == View.GONE){
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            widthx = displayMetrics.widthPixels;
            heightx = displayMetrics.heightPixels;

            heightx += 250;
            resizeView(imageFreeTextLayout, widthx, heightx);
            layoutStiker.setVisibility(View.VISIBLE);
            String id = String.valueOf("1");
            changeFragment(Integer.valueOf(id));
            tabLayout.setScrollPosition(0, 0f, true);
        }else{
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            widthx = displayMetrics.widthPixels;
            heightx = displayMetrics.heightPixels;

            heightx -= 250;
            resizeView(imageFreeTextLayout, widthx, heightx);
            layoutStiker.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        encryptedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        encryptedPreferences.registerOnSharedPreferenceChangeListener(this);
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }
    @Override
    public void onDestroy() {
        encryptedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        keyboardHeightProvider.close();
        super.onDestroy();
    }
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {

        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";

        View view = v.findViewById(R.id.keyboards);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view .getLayoutParams();
        params.height = height;

        Double iHeight = height==0? 0 : (height*0.3)+300;
        svStiker.setMinimumHeight(heightx+iHeight.intValue());
        imageFreeTextLayout.setMinimumHeight(heightx+iHeight.intValue());

        resizeView(svStiker, widthx, heightx+iHeight.intValue());
        resizeView(imageFreeTextLayout, widthx, heightx+iHeight.intValue());
        focusOnView(svStiker, imageFreeTextLayout);

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
    public void initDataDummy(){
        masterStikerHelper.deleteAll();
        for (int i=1; i<=10;i++){
            if (!masterStikerHelper.checkDuplicate(i)){
                MasterStiker masterStiker = new MasterStiker();
                masterStiker.setId(i);
                masterStiker.setNama("MasterStiker"+i);
                masterStiker.setCover("http://via.placeholder.com/16x16");
                masterStikerHelper.addStiker(masterStiker);
                for (int j=i;j<=i*10;j++){
                    if (!transaksiStikerHelper.checkDuplicate(j)){
                        TransaksiStiker transaksiStiker = new TransaksiStiker();
                        transaksiStiker.setId(j);
                        transaksiStiker.setId_stiker(i);
                        transaksiStiker.setStiker("http://via.placeholder.com/16x16");
                        transaksiStikerHelper.addStiker(transaksiStiker);
                    }
                }
            }
        }
    }
    public void initTab(){
        tabLayout = (TabLayout) v.findViewById(R.id.StikerTab);
        tabLayout.removeAllTabs();

        RealmResults<MasterStiker> results = masterStikerHelper.getStiker();
        if (results.size() > 0){
            for (int i = 0; i<results.size();i++){
                final TabLayout.Tab tab = tabLayout.newTab();
                ImageView imgStikerCover = new ImageView(getActivity().getApplicationContext());
                Glide.with(getActivity().getApplicationContext()).load(results.get(i).getCover()).into(imgStikerCover);
                imgStikerCover.setPadding(0, 0, 0, 0);
                imgStikerCover.setTag(String.valueOf(results.get(i).getId()));
                imgStikerCover.setId(R.id.StikerTab);
                tab.setCustomView(imgStikerCover);
                tabLayout.addTab(tab, i);

            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                ImageView imgStikerCover = (ImageView) v.findViewById(R.id.StikerTab);
                String id = String.valueOf(imgStikerCover.getTag());
                changeFragment(Integer.valueOf(id));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                ImageView imgStikerCover = (ImageView) v.findViewById(R.id.StikerTab);
                String id = String.valueOf(imgStikerCover.getTag());
                changeFragment(Integer.valueOf(id));
            }
        });
    }
    public void changeFragment(int id){
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        Fragment fragment = new StikerFragment(this);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentStiker, fragment);
        ft.commit();
    }

    @Override
    public void onSharedPreferenceChanged(EncryptedPreferences encryptedPreferences, String key) {
        Toast.makeText(getActivity().getApplicationContext(), "PREFERENCES CHANGED", Toast.LENGTH_SHORT).show();
        if (!encryptedPreferences.getString("STIKERSELECTED", "NOTHING").equalsIgnoreCase("NOTHING")){
            Glide.with(this).load(encryptedPreferences.getString("STIKERSELECTED", "NOTHING")).into(txtImage);
        }
    }

    @Override
    public void changeStiker() {
        Toast.makeText(getActivity().getApplicationContext(), "Change Stiker", Toast.LENGTH_SHORT).show();
        if (!encryptedPreferences.getString("STIKERSELECTED", "NOTHING").equalsIgnoreCase("NOTHING")){
            Glide.with(this).load(encryptedPreferences.getString("STIKERSELECTED", "NOTHING")).into(txtImage);
        }
    }
    public void toogleSoftboard(){
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.RESULT_HIDDEN);
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
        tl.setId_aktivitas(6);
        tl.setId_ibadah(2);
        tl.setNama_aktivitas("");
        tl.setNama_ibadah("");
        tl.setImage(encryptedPreferences.getString("STIKERSELECTED", "NOTHING"));
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
}
