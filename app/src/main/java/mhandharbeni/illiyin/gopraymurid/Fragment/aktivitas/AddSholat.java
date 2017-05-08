package mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.dunst.check.CheckableImageButton;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mhandharbeni.illiyin.gopraymurid.R;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

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
    TextView txtBersama, txtTempat;
    Button btnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //init encrypting
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        //init encrypting
        /*init network*/
        client = new HttPizza();
        /*init network*/

        endUri = getResources().getString(R.string.server)+"/"+getResources().getString(R.string.vServer)+"/"+"users/self/timeline";
        v = inflater.inflate(R.layout.tambah_sholat, container, false);
        checkSubuh = (CheckableImageButton) v.findViewById(R.id.btnSubuh);
        checkDhuhur = (CheckableImageButton) v.findViewById(R.id.btnDhuhur);
        checkAshar = (CheckableImageButton) v.findViewById(R.id.btnAshar);
        checkMaghrib = (CheckableImageButton) v.findViewById(R.id.btnMaghrib);
        checkIsya = (CheckableImageButton) v.findViewById(R.id.btnIsya);
        checkSunnah= (CheckableImageButton) v.findViewById(R.id.btnSunnah);
        txtBersama = (TextView) v.findViewById(R.id.txtBersama);
        txtTempat = (TextView) v.findViewById(R.id.txtTempat);


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
    public void saveServer(){
        String key = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, "8aa560b5efa8691dddcf4a11a69b3d1f98f01280bb6ef6efbd32ca39d9b8ac33"));
        RequestBody requestBody = new FormBody.Builder()
                .add("access_token", key)
                .add("id_aktivitas", "3")
                .add("id_ibadah",encryptedPreferences.getString("SHOLAT", "1"))
                .add("tempat", txtTempat.getText().toString())
                .add("bersama", txtBersama.getText().toString())
                .add("gambar", "null")
                .add("nominal", "0")
                .add("point", "50")
                .add("tanggal", getDate())
                .add("jam", getTime())
                .build();
        Request request = client.newRequest()
                .url(endUri)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    @Override
    public void onClick(View v) {
        uncheckButton();

        switch (v.getId()){
            case R.id.btnSubuh :
                encryptedPreferences.edit().putString("SHOLAT", "1").commit();
                checkSubuh.setChecked(TRUE);
                break;
            case R.id.btnDhuhur :
                encryptedPreferences.edit().putString("SHOLAT", "2").commit();
                checkDhuhur.setChecked(TRUE);
                break;
            case R.id.btnAshar :
                encryptedPreferences.edit().putString("SHOLAT", "3").commit();
                checkAshar.setChecked(TRUE);
                break;
            case R.id.btnMaghrib :
                encryptedPreferences.edit().putString("SHOLAT", "4").commit();
                checkMaghrib.setChecked(TRUE);
                break;
            case R.id.btnIsya :
                encryptedPreferences.edit().putString("SHOLAT", "5").commit();
                checkIsya.setChecked(TRUE);
                break;
            case R.id.btnSunnah :
                encryptedPreferences.edit().putString("SHOLAT", "6").commit();
                checkSunnah.setChecked(TRUE);
                break;
        }
    }
}
