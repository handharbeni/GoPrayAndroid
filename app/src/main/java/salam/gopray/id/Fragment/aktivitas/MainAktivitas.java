package salam.gopray.id.Fragment.aktivitas;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import salam.gopray.id.R;

/**
 * Created by root on 12/05/17.
 */

public class MainAktivitas extends AppCompatActivity {
    private static String TAG = MainAktivitas.class.getSimpleName();
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";

    private FirebaseAnalytics mFirebaseAnalytics;
    EncryptedPreferences encryptedPreferences;
    ImageView imageBack;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.layout_main_aktivitas);

        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Fragment fragment = null;
        Bundle bundle = getIntent().getExtras();
        int mode = bundle.getInt("MODE");
        switch (mode){
            case 0:
                /*sholat*/
                fragment = new AddSholat();
                break;
            case 1:
                /*mengaji*/
                fragment = new AddMengaji();
                break;
            case 2:
                fragment = new AddSedekah();
                break;
            case 3:
                fragment = new AddPuasa();
                break;
            case 4:
                fragment = new AddFreeText();
                break;
            default:
                fragment = new AddSholat();
                break;
        }
        changeFragment(fragment);
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FrameContainer, fragment);
        ft.commit();
    }
    public void sendException(Exception e){
        FirebaseCrash.report(new Exception(e));
        FirebaseCrash.logcat(Log.ERROR, TAG, "Crash caught");
        FirebaseCrash.report(e);
        Bundle bundle = new Bundle();
        bundle.putString("Exception", e.getMessage());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        mFirebaseAnalytics.setMinimumSessionDuration(20000);
        mFirebaseAnalytics.setSessionTimeoutDuration(500);
        mFirebaseAnalytics.setUserId("2");
        mFirebaseAnalytics.setUserProperty("User", encryptedPreferences.getUtils().decryptStringValue(
                encryptedPreferences.getString(KEY, getString(R.string.dummyToken))));
    }
    public void sendScreen(String name){
        Bundle bundle = new Bundle();
        bundle.putString("Screen", name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        mFirebaseAnalytics.setMinimumSessionDuration(20000);
        mFirebaseAnalytics.setSessionTimeoutDuration(500);
        mFirebaseAnalytics.setUserId("2");
        mFirebaseAnalytics.setUserProperty("User", encryptedPreferences.getUtils().decryptStringValue(
                encryptedPreferences.getString(KEY, getString(R.string.dummyToken))));
    }
    public void sendEvent(String category, String action, String label){
        Bundle bundle = new Bundle();
        bundle.putString("EventCategory", category);
        bundle.putString("EventAction", action);
        bundle.putString("EventLabel", label);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        mFirebaseAnalytics.setMinimumSessionDuration(20000);
        mFirebaseAnalytics.setSessionTimeoutDuration(500);
        mFirebaseAnalytics.setUserId("2");
        mFirebaseAnalytics.setUserProperty("User",
                encryptedPreferences.getUtils().decryptStringValue(
                        encryptedPreferences.getString(KEY,
                                getString(R.string.dummyToken))
                )
        );
    }
}
