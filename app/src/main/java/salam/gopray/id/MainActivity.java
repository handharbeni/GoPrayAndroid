package salam.gopray.id;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.azimolabs.keyboardwatcher.KeyboardWatcher;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.crash.FirebaseCrash;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import me.zhanghai.android.materialprogressbar.HorizontalProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import salam.gopray.id.Fragment.Family;
import salam.gopray.id.Fragment.Meme;
import salam.gopray.id.Fragment.Setting;
import salam.gopray.id.Fragment.Timeline;
import salam.gopray.id.database.helper.MessageParentHelper;
import salam.gopray.id.service.MainServices;
import salam.gopray.id.service.TimeService;
import salam.gopray.id.util.FontChangeCrawler;
import salam.gopray.id.util.KeyboardHeightObserver;
import salam.gopray.id.util.KeyboardHeightProvider;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity implements ConnectivityChangeListener, KeyboardWatcher.OnKeyboardToggleListener, ViewTreeObserver.OnGlobalLayoutListener,KeyboardHeightObserver, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static String TAG = MainActivity.class.getSimpleName();
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";

    private static final String TITLE = TAG;
    TabLayout tabLayout;

    private FluentSnackbar mFluentSnackbar;
    private RelativeLayout activityLayout, mainLayout, signinLayout, signupLayout;
    private Button btnSignin, btnSignup;
    private TextView txtScreenSignup, txtScreenSignin;

    private String server;
    private String uriSignin;
    private String uriSignup;

    EncryptedPreferences encryptedPreferences;
    ConnectionBuddyConfiguration networkInspectorConfiguration;
    HttPizza client;
    FontChangeCrawler fontReplacer;
    EditText txtEmail;
    EditText txtPassword;
    EditText txtNama;

    ScrollView svLogin;
    ScrollView svSignup;
    private KeyboardWatcher keyboardWatcher;
    int widthx, heightx;

    MaterialProgressBar mProgressBar;

    MessageParentHelper mpHelper;
    String endUriDelete;

    private KeyboardHeightProvider keyboardHeightProvider;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        endUriDelete = getResources().getString(R.string.server) + "/" + getResources().getString(R.string.vServer) + "/" + "users/self/deletememe";
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        initDeepLink();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (encryptedPreferences.getString("WELCOME", "0").equalsIgnoreCase("0")) {
            Intent i = new Intent(this, MyWelcomeActivity.class);
            startActivity(i);
        }

        mpHelper = new MessageParentHelper(getApplicationContext());
        registerReceiver(this.receiver, new IntentFilter("UPDATE MESSAGE"));

        keyboardWatcher = new KeyboardWatcher(this);
        keyboardWatcher.setListener(this);
        Typeface defaultFont = Typeface.createFromAsset(getAssets(), "fonts/helveticanormal.ttf");
        String[] permissions = new String[11];
        permissions[0] = Manifest.permission.CAMERA;
        permissions[1] = Manifest.permission.INTERNET;
        permissions[2] = Manifest.permission.WAKE_LOCK;
        permissions[3] = Manifest.permission.LOCATION_HARDWARE;
        permissions[4] = Manifest.permission.ACCESS_COARSE_LOCATION;
        permissions[5] = Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[6] = Manifest.permission.READ_PHONE_STATE;
        permissions[7] = Manifest.permission.ACCESS_NETWORK_STATE;
        permissions[8] = Manifest.permission.ACCESS_WIFI_STATE;
        permissions[9] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[10] = Manifest.permission.READ_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(
                this,
                permissions,
                5
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(this).build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        server = getString(R.string.server) + "/" + getString(R.string.vServer) + "/";
        uriSignin = "users/self/login";
        uriSignup = "users/self/daftar";
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        widthx = displayMetrics.widthPixels;
        heightx = displayMetrics.heightPixels;
        setContentView(R.layout.activity_main);
        mProgressBar = (MaterialProgressBar) findViewById(R.id.more_progress);
        mProgressBar.setProgressDrawable(new HorizontalProgressDrawable(this));
        mProgressBar.setVisibility(View.GONE);

        fontReplacer = new FontChangeCrawler(defaultFont);
        fontReplacer.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        signinLayout = (RelativeLayout) findViewById(R.id.signinLayout);
        signupLayout = (RelativeLayout) findViewById(R.id.signupLayout);
        startServices();
        ConnectionBuddy.getInstance().clearNetworkCache(this);
        checkSession();
        onNewIntent(getIntent());
        checkDifferentTime();
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }
    private final void focusOnView(final View parent, final View v){
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.scrollTo(0, v.getBottom());
            }
        });
    }
    public void showProgress(){
        mProgressBar.setVisibility(View.VISIBLE);
    }
    public void stopProgress(){
        mProgressBar.setVisibility(View.GONE);
    }

    public void startServices(){
        if (!MainServices.serviceRunning){
            startService(new Intent(getApplicationContext(), MainServices.class));
        }
        if(!TimeService.serviceRunning){
            startService(new Intent(getApplicationContext(), TimeService.class));
        }
    }
    public void stopServices(){
        stopService(new Intent(this, MainServices.class));
        stopService(new Intent(this, TimeService.class));

        ConnectionBuddy.getInstance().clearNetworkCache(this);
        ConnectionBuddy.getInstance().notifyConnectionChange(true, this);

        networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(this).build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);

    }
    public void checkSession(){
        // 0 : logout (default)
        // 1 : register
        // 2 : main layout
        String loggedin = encryptedPreferences.getString(STAT, "1");
        setView(Integer.valueOf(loggedin));

    }
    public void setSession(String key, String nama, String email, String picture, String pos){
        String encryptedApiKey = encryptedPreferences.getUtils().encryptStringValue(key);
        encryptedPreferences.edit()
                .putString(KEY, encryptedApiKey)
                .putString(NAMA, nama)
                .putString(EMAIL, email)
                .putString(PICTURE, picture)
                .putString(STAT, pos)
                .apply();
        checkSession();
    }
    public void setView(Integer stat){
        mainLayout.setVisibility(View.GONE);
        signinLayout.setVisibility(View.GONE);
        signupLayout.setVisibility(View.GONE);
        switch (stat){
            case 1 :
                signinLayout.setVisibility(View.VISIBLE);
                /**/
                sendScreen("SIGNIN");
                signinInit();
                break;
            case 2 :
                sendScreen("SIGNUP");
                signupLayout.setVisibility(View.VISIBLE);
                signupinit();
                break;
            case 3 :
                sendScreen("MAIN SCREEN");
                mainLayout.setVisibility(View.VISIBLE);
                mainInit();
                break;
        }
    }
    public void setBadgeOrtu(int count, boolean show){
        if (tabLayout != null){
            View v = tabLayout.getTabAt(1).getCustomView();
            ImageView badge = (ImageView) v.findViewById(R.id.tab_badge);
            TextView txtBadge = (TextView) v.findViewById(R.id.tab_badge_text);
            badge.setVisibility(View.GONE);
            txtBadge.setVisibility(View.GONE);
            if (count > 0){
                if (show){
                    badge.setVisibility(View.VISIBLE);
                    txtBadge.setVisibility(View.VISIBLE);
                    txtBadge.setText(String.valueOf(count));
                }else{
                    badge.setVisibility(View.GONE);
                    txtBadge.setVisibility(View.GONE);
                }
            }
        }
    }
    public void mainInit(){
        tabLayout = (TabLayout) findViewById(R.id.MaterialTab);
        tabLayout.removeAllTabs();
        final TabLayout.Tab timeline = tabLayout.newTab();
        final TabLayout.Tab family = tabLayout.newTab();
        final TabLayout.Tab meme = tabLayout.newTab();
        final TabLayout.Tab setting = tabLayout.newTab();
        timeline.setIcon(R.drawable.tab_timeline);
//        family.setCustomView(R.layout.badge_tab_family);
        meme.setIcon(R.drawable.tab_meme);
        setting.setIcon(R.drawable.tab_setting);


        tabLayout.addTab(timeline, 0);
        tabLayout.addTab(family, 1);
        tabLayout.addTab(meme, 2);
        tabLayout.addTab(setting, 3);

        tabLayout.getTabAt(1).setCustomView(R.layout.badge_tab_family);


        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Drawable icon = null;
                switch (tab.getPosition()){
                    case 0:
                        icon = getResources().getDrawable(R.drawable.tab_timeline_actives);
                        break;
                    case 1:
                        icon = getResources().getDrawable(R.drawable.tab_ortu_actives);
//                        setBadgeOrtu(10, true);
                        break;
                    case 2:
                        icon = getResources().getDrawable(R.drawable.tab_meme_actives);
                        break;
                    case 3:
                        icon = getResources().getDrawable(R.drawable.tab_setting_actives);
                        break;
                }
                tab.setIcon(icon);
                chooseFragment(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Drawable icon = null;
                switch (tab.getPosition()){
                    case 0:
                        icon = getResources().getDrawable(R.drawable.tab_timeline);
                        break;
                    case 1:
                        icon = getResources().getDrawable(R.drawable.tab_ortu);
//                        setBadgeOrtu(1, true);
                        break;
                    case 2:
                        icon = getResources().getDrawable(R.drawable.tab_meme);
                        break;
                    case 3:
                        icon = getResources().getDrawable(R.drawable.tab_setting);
                        break;
                }
                tab.setIcon(icon);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        View view = findViewById(R.id.mainLayout);
        view.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });
        tabLayout.getTabAt(0).isSelected();
        Fragment fragment = new Timeline();
        changeFragment(fragment);
    }
    public void chooseFragment(int position){
        Fragment fragment = new Timeline();
        switch (position){
            case 0:
                fragment = new Timeline();
                break;
            case 1:
                fragment = new Family();
                break;
            case 2:
                fragment = new Meme();
                break;
            case 3:
                fragment = new Setting();
                break;
        }
        changeFragment(fragment);
    }
    public void checkDifferentTime(){
        Calendar localTime = Calendar.getInstance();

        int hourx = localTime.get(Calendar.HOUR);
        int minutex = localTime.get(Calendar.MINUTE);
        int secondx = localTime.get(Calendar.SECOND);
        Log.d(TAG, "checkDifferentTime: LOCAL "+String.valueOf(hourx+":"+minutex+":"+secondx));

        Calendar germanyTime = new GregorianCalendar(TimeZone.getTimeZone("GMT+07:00"));
        int hour = germanyTime.getTime().getHours();
        int minute = germanyTime.getTime().getMinutes();
        int second = germanyTime.getTime().getSeconds();
        Log.d(TAG, "checkDifferentTime: GMT7 "+String.valueOf(hour+":"+minute+":"+second));

    }
    public void signinInit(){
        svLogin = (ScrollView) findViewById(R.id.svLogin);
        btnSignin = (Button) findViewById(R.id.btnSignin);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtScreenSignup = (TextView) findViewById(R.id.txtScreenSignup);
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(svLogin, signinLayout);
                }
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(svLogin, signinLayout);
                }
            }
        });
        txtScreenSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(2);
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                doSignin();
            }
        });
        View view = findViewById(R.id.signinLayout);
        view.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });
    }
    public void doSignin(){
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        if(encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")){
            btnSignin.setText(getString(R.string.prosesLogin));
            btnSignin.setEnabled(false);
            // koneksi tersedia
            RequestBody formBody = new FormBody.Builder()
                    .add("email", txtEmail.getText().toString())
                    .add("password", txtPassword.getText().toString())
                    .build();
            Request request = client.newRequest()
                    .url(server+uriSignin)
                    .post(formBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Response response) {
                    try {
                        String responses = response.body().string();
                        Log.d("MAINACTIVITY GOPRAY", "onResponse: "+responses);
                        JSONObject jsonObj = new JSONObject(responses);
                        Boolean returns = jsonObj.getBoolean("return");
                        if(returns){
                            JSONArray jsonData = jsonObj.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jsonObjData = jsonData.getJSONObject(i);
                                String sNama    = jsonObjData.getString("nama");
                                String sEmail   = jsonObjData.getString("email");
                                String sPict    = jsonObjData.getString("profile_picture");
                                String sKey     = jsonObjData.getString("key");
                                setSession(sKey, sNama, sEmail, sPict, "3");
                            }
                            btnSignin.setText(getString(R.string.signin));
                            btnSignin.setEnabled(true);
                            checkSession();
                        }else{
                            showSnackBar(getString(R.string.loginFailed));
                            btnSignin.setText(getString(R.string.signin));
                            btnSignin.setEnabled(true);
                        }
                    } catch (JSONException|IOException e) {
                        e.printStackTrace();
                        sendException(e);
                        btnSignin.setText(getString(R.string.signin));
                        btnSignin.setEnabled(true);
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    showSnackBar(getString(R.string.loginFailed));
                    btnSignin.setText(getString(R.string.signin));
                    btnSignin.setEnabled(true);
                }
            });
        }else{
            showSnackBar(getString(R.string.noConnection));
        }
    }

    public void signupinit(){
        svSignup = (ScrollView) findViewById(R.id.svSignup);
        btnSignup = (Button) findViewById(R.id.btnSignup);
//        btnSignup.setText(getString(R.string.prosesDaftar));
        btnSignup.setEnabled(true);
        txtEmail = (EditText) findViewById(R.id.txtDEmail);
        txtPassword = (EditText) findViewById(R.id.txtDPassword);
        txtNama= (EditText) findViewById(R.id.txtDNama);
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(svSignup, signupLayout);
                }
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(svSignup, signupLayout);
                }
            }
        });
        txtNama.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(svSignup, signupLayout);
                }
            }
        });
        txtScreenSignin = (TextView) findViewById(R.id.txtScreenSignin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        txtScreenSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(1);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                doSignup();
            }
        });
        View view = findViewById(R.id.signupLayout);
        view.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });
    }
    public void doSignup(){
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        if(encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")){
            btnSignup.setText(getString(R.string.prosesDaftar));
            btnSignup.setEnabled(false);
            RequestBody formBody = new FormBody.Builder()
                    .add("nama", txtNama.getText().toString())
                    .add("email", txtEmail.getText().toString())
                    .add("password", txtPassword.getText().toString())
                    .build();
            Request request = client.newRequest()
                    .url(server+uriSignup)
                    .post(formBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Response response) {
                    try {
                        String responses = response.body().string();
                        JSONObject jsonObj = new JSONObject(responses);
                        Boolean returns = jsonObj.getBoolean("return");
                        if(returns){
                            showSnackBar(getString(R.string.signupSuccess));
                            btnSignup.setText(getString(R.string.signin));
                            btnSignup.setEnabled(true);
                            setView(1);
                        }else{
                            showSnackBar(getString(R.string.signupFailed));
                            btnSignup.setText(getString(R.string.signup));
                            btnSignup.setEnabled(true);
                        }
                    } catch (JSONException|IOException e) {
                        e.printStackTrace();
                        sendException(e);
                        showSnackBar(getString(R.string.signupFailed));
                        btnSignup.setText(getString(R.string.signup));
                        btnSignup.setEnabled(true);
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    showSnackBar(getString(R.string.signupFailed));
                    btnSignin.setText(getString(R.string.signup));
                    btnSignin.setEnabled(true);
                }
            });
        }else{
            showSnackBar(getString(R.string.noConnection));
        }
    }
    public void showSnackBar(String message){
        mFluentSnackbar = FluentSnackbar.create(this);
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

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        if(event.getState().getValue() == ConnectivityState.CONNECTED){
            // device has active internet connection
            encryptedPreferences.edit()
                    .putString("NETWORK", "1")
                    .apply();
        }
        else{
            // there is no active internet connection on this device
            encryptedPreferences.edit()
                    .putString("NETWORK", "0")
                    .apply();
        }
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        fm.replace(R.id.FrameContainer, fragment);
        fm.commit();
    }
    @Override
    public void onStart() {
        super.onStart();
        sendScreen(TAG);
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
        final Uri BASE_URL = Uri.parse("https://gopray.id");
        final String APP_URI = BASE_URL.buildUpon().build().toString();

        Indexable articleToIndex = new Indexable.Builder()
                .setName(TITLE)
                .setUrl(APP_URI)
                .build();

        Task<Void> task = FirebaseAppIndex.getInstance().update(articleToIndex);
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "App Indexing API: Successfully added " + TITLE + " to index");
            }
        });

        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "App Indexing API: Failed to add " + TITLE + " to index. " + exception.getMessage());
            }
        });
        Task<Void> actionTask = FirebaseUserActions.getInstance().start(Actions.newView(TITLE,
                APP_URI));

        actionTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "App Indexing API: Successfully started view action on ");
            }
        });
        actionTask.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "App Indexing API: Failed to start view action on "+TITLE
                        + exception.getMessage());
            }
        });
    }

    @Override
    public void onStop() {
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
        final Uri BASE_URL = Uri.parse("https://gopray.id");
        final String APP_URI = BASE_URL.buildUpon().build().toString();

        Task<Void> actionTask = FirebaseUserActions.getInstance().end(Actions.newView(TITLE,
                APP_URI));

        actionTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "App Indexing API: Successfully ended view action on " + TITLE);
            }
        });

        actionTask.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "App Indexing API: Failed to end view action on " + TITLE + ". "
                        + exception.getMessage());
            }
        });
        super.onStop();
    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onKeyboardShown(int keyboardSize) {
//        svLogin.scrollTo(0, keyboardSize);
//        for (int i = 0; i< keyboardSize;i++){
//            Space space = new Space(this);
//        }
//        svLogin.notify();
    }

    @Override
    public void onKeyboardClosed() {

    }
    public void getHeight(String mode){
        switch (mode){
            case "signin":
                initHeightSignin();
                break;
            case "signup":
                initHeightSignup();
                break;
            case "main":
                initHeightMain();
                break;
        }
    }
    public void initHeightSignin(){
        Rect r = new Rect();
        signinLayout.getWindowVisibleDisplayFrame(r);
        int screenHeight = signinLayout.getRootView().getHeight();
        int heightDifference = screenHeight - (r.bottom - r.top);

    }
    public void initHeightSignup(){
        Rect r = new Rect();
        signupLayout.getWindowVisibleDisplayFrame(r);
        int screenHeight = signinLayout.getRootView().getHeight();
        int heightDifference = screenHeight - (r.bottom - r.top);
    }
    public void initHeightMain(){
        Rect r = new Rect();
        mainLayout.getWindowVisibleDisplayFrame(r);
        int screenHeight = signinLayout.getRootView().getHeight();
        int heightDifference = screenHeight - (r.bottom - r.top);
    }
    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        signinLayout.getWindowVisibleDisplayFrame(r);
        int screenHeight = signinLayout.getRootView().getHeight();
        int heightDifference = screenHeight - (r.bottom - r.top);
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
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        keyboardHeightProvider.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(this.receiver, new IntentFilter("UPDATE MESSAGE"));
        keyboardHeightProvider.setKeyboardHeightObserver(this);
        super.onResume();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String mode = bundle.getString("MODE");
            switch (mode){
                case "UPDATE LIST":
                    setBadgeOrtu(mpHelper.countNewMessage(), true);
                    break;
                default:
                    break;
            }


        }
    };
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
        }
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
        mFirebaseAnalytics.setUserId("1");
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
        mFirebaseAnalytics.setUserId("1");
        mFirebaseAnalytics.setUserProperty("User", encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, getString(R.string.dummyToken))));
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
        mFirebaseAnalytics.setUserId("1");
        mFirebaseAnalytics.setUserProperty("User", encryptedPreferences.getUtils().decryptStringValue(
                encryptedPreferences.getString(KEY, getString(R.string.dummyToken))));
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {

        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";

        View view = findViewById(R.id.keyboards);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view .getLayoutParams();
        params.height = height;
        svLogin = (ScrollView) findViewById(R.id.svLogin);
        svSignup = (ScrollView) findViewById(R.id.svSignup);
        LinearLayout lrLogin = (LinearLayout) findViewById(R.id.lrLogin);
        LinearLayout lrSignup = (LinearLayout) findViewById(R.id.lrSignup);
        Double iHeight = height==0? 0 : (height*0.3)+150;
        lrLogin.setMinimumHeight(heightx+iHeight.intValue());
        lrSignup.setMinimumHeight(heightx+iHeight.intValue());

        resizeView(lrLogin, widthx, heightx+iHeight.intValue());
        resizeView(lrSignup, widthx, heightx+iHeight.intValue());
        resizeView(mainLayout, widthx, heightx+iHeight.intValue());
        focusOnView(svLogin, signinLayout);
        focusOnView(svSignup, signupLayout);
    }
    public void dialogDelete(final int id){
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setTitle("HAPUS")
                .setMessage("Hapus Post?"+String.valueOf(id))
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                /*HAPUS AKTIVITAS*/
                        deleteTimeline(id);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    public void deleteTimeline(final int id){
        final int idTemp;
        idTemp = id;
        String token = encryptedPreferences.getString(KEY, getString(R.string.dummyToken));
        String decryptToken = encryptedPreferences.getUtils().decryptStringValue(token);
        /*delete server*/
        RequestBody formBody = new FormBody.Builder()
                .add("access_token", decryptToken)
                .add("id_meme", String.valueOf(id))
                .build();
        Request request = client.newRequest()
                .url(endUriDelete)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        /*delete server*/
    }
    public void initDeepLink(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                } else {
                                    Log.d(TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
