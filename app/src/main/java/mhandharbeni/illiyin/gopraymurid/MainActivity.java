package mhandharbeni.illiyin.gopraymurid;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golovin.fluentstackbar.FluentSnackbar;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import mhandharbeni.illiyin.gopraymurid.Fragment.Family;
import mhandharbeni.illiyin.gopraymurid.Fragment.Meme;
import mhandharbeni.illiyin.gopraymurid.Fragment.Setting;
import mhandharbeni.illiyin.gopraymurid.Fragment.Timeline;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

public class MainActivity extends AppCompatActivity implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";



    TabLayout tabLayout;

    private FluentSnackbar mFluentSnackbar;
    private RelativeLayout mainLayout, signinLayout, signupLayout;
    private Button btnSignin, btnSignup;
    private TextView txtScreenSignup, txtScreenSignin;

    private String server;
    private String uriSignin;
    private String uriSignup;

    EncryptedPreferences encryptedPreferences;
    ConnectionBuddyConfiguration networkInspectorConfiguration;
    HttPizza client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // permission
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
        // permission
        // no limit status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        // no limit status bar

        //init encrypting
        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        //init encrypting

        // connection buddy
        networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(this).build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);
        // connection buddy

        //httppizza
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        //httppizza
        //url
        server = getString(R.string.server)+"/"+getString(R.string.vServer)+"/";
        uriSignin = "users/self/login";
        uriSignup = "users/self/daftar";
        //url
        setContentView(R.layout.activity_main);

        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        signinLayout = (RelativeLayout) findViewById(R.id.signinLayout);
        signupLayout = (RelativeLayout) findViewById(R.id.signupLayout);

        checkSession();
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
        //String decryptedApiKey = encryptedPreferences.getUtils().decryptStringValue(encryptedApiKey);
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
                signinInit();
                break;
            case 2 :
                signupLayout.setVisibility(View.VISIBLE);
                signupinit();
                break;
            case 3 :
                mainLayout.setVisibility(View.VISIBLE);
                mainInit();
                break;
        }
    }
    public void mainInit(){
        tabLayout = (TabLayout) findViewById(R.id.MaterialTab);

        tabLayout.removeAllTabs();

        final TabLayout.Tab timeline = tabLayout.newTab();
        final TabLayout.Tab family = tabLayout.newTab();
        final TabLayout.Tab meme = tabLayout.newTab();
        final TabLayout.Tab setting = tabLayout.newTab();

        timeline.setIcon(R.drawable.ic_tab_timeline);
        family.setIcon(R.drawable.ic_tab_pray_circle);
        meme.setIcon(R.drawable.ic_tab_ibadah_daily);
        setting.setIcon(R.drawable.ic_tab_setting);


        tabLayout.addTab(timeline, 0);
        tabLayout.addTab(family, 1);
        tabLayout.addTab(meme, 2);
        tabLayout.addTab(setting, 3);

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                chooseFragment(tab.getPosition());
                showSnackBar(String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
    public void signinInit(){
        btnSignin = (Button) findViewById(R.id.btnSignin);
        txtScreenSignup = (TextView) findViewById(R.id.txtScreenSignup);
        txtScreenSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(2);
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignin();
            }
        });
    }
    public void doSignin(){
        if(encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")){
            btnSignin.setText(getString(R.string.prosesLogin));
            btnSignin.setEnabled(false);
            TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
            TextView txtPassword = (TextView) findViewById(R.id.txtPassword);
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
                    } catch (IOException e) {
                        e.printStackTrace();
                        btnSignin.setText(getString(R.string.signin));
                        btnSignin.setEnabled(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                doSignup();
            }
        });
    }
    public void doSignup(){
        if(encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")){
            // koneksi tersedia
            btnSignup.setText(getString(R.string.prosesDaftar));
            btnSignup.setEnabled(false);
            TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
            TextView txtPassword = (TextView) findViewById(R.id.txtPassword);
            TextView txtNama= (TextView) findViewById(R.id.txtNama);
            // koneksi tersedia
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
                            showSnackBar(getString(R.string.loginFailed));
                            btnSignup.setText(getString(R.string.signin));
                            btnSignup.setEnabled(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showSnackBar(getString(R.string.loginFailed));
                        btnSignup.setText(getString(R.string.signin));
                        btnSignup.setEnabled(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showSnackBar(getString(R.string.loginFailed));
                        btnSignup.setText(getString(R.string.signin));
                        btnSignup.setEnabled(true);
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
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
    }

    @Override
    public void onStop() {
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
        super.onStop();
    }
}
