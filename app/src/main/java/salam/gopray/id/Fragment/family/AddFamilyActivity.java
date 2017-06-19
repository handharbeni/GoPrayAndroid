package salam.gopray.id.Fragment.family;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.golovin.fluentstackbar.FluentSnackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import salam.gopray.id.Fragment.aktivitas.MainAktivitas;
import salam.gopray.id.R;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

/**
 * Created by root on 16/06/17.
 */

public class AddFamilyActivity  extends AppCompatActivity implements ConnectivityChangeListener {
    private static String TAG = MainAktivitas.class.getSimpleName();
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";

    private FirebaseAnalytics mFirebaseAnalytics;
    EncryptedPreferences encryptedPreferences;
    ImageView imageBack;

    MaterialEditText etEmail;
    Button btnAddKerabat;

    HttPizza client;
    String endUri;

    private FluentSnackbar mFluentSnackbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new HttPizza();
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/kerabat";
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.layout_add_kerabat);

        etEmail = (MaterialEditText) findViewById(R.id.etEmail);
        btnAddKerabat = (Button) findViewById(R.id.btnAddKerabat);
        btnAddKerabat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAdd();
            }
        });

        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void doAdd(){
        String token = encryptedPreferences.getString(KEY, "0");
        String decryptToken = encryptedPreferences.getUtils().decryptStringValue(token);

        /*add email to server*/
        if(encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")){
            RequestBody formBody = new FormBody.Builder()
                    .add("email", etEmail.getText().toString())
                    .add("access_token", decryptToken)
                    .build();
            Request request = client.newRequest()
                    .url(endUri)
                    .post(formBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean statusObject = jsonObject.getBoolean("return");
                        if (statusObject){
                            showSnackBar("Kerabat Berhasil ditambahkan");
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }else{
            /*show error*/
            showSnackBar("Koneksi tidak tersedia");
        }
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
}
