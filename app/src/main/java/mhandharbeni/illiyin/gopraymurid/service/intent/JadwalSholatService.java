package mhandharbeni.illiyin.gopraymurid.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.database.JadwalSholat;
import mhandharbeni.illiyin.gopraymurid.database.helper.JadwalSholatHelper;
import mhandharbeni.illiyin.gopraymurid.database.helper.TimelineHelper;
import sexy.code.Callback;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.Response;

public class JadwalSholatService extends IntentService implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    JadwalSholatHelper jHelper;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;

    String endUri;
    public JadwalSholatService() {
        super("JADWAL SHOLAT");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //init encrypting
        encryptedPreferences = new EncryptedPreferences.Builder(getBaseContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        //init encrypting
        //httppizza
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        //httppizza
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/master/jadwalsholat?access_token=";
        jHelper = new JadwalSholatHelper(getBaseContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (encryptedPreferences.getString("NETWORK","0").equalsIgnoreCase("1")){
            syncData();
        }
    }

    public void syncData(){
        String token = encryptedPreferences.getString(KEY, "0");
        if(!token.equalsIgnoreCase("0")){
            String decryptToken = encryptedPreferences.getUtils().decryptStringValue(token);
            endUri += decryptToken;
            endUri += "&timezone=7";
            Request request = client.newRequest().url(endUri).get().build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Boolean retur = jsonObject.getBoolean("return");
                        if(retur){
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            if (jsonArray.length() > 0){
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject objectData = jsonArray.getJSONObject(i);
                                    String tanggal = (objectData.getString("tanggal"));
                                    String subuh = (objectData.getString("subuh"));
                                    String dhuha = (objectData.getString("dhuha"));
                                    String dhuhur = (objectData.getString("dhuhur"));
                                    String ashar = (objectData.getString("ashar"));
                                    String maghrib = (objectData.getString("maghrib"));
                                    String isya = (objectData.getString("isya"));
                                    if(!jHelper.checkDuplicate(tanggal)){
                                        JadwalSholat jadwalSholat = new JadwalSholat();
                                        jadwalSholat.setTanggal(tanggal);
                                        jadwalSholat.setSubuh(subuh);
                                        jadwalSholat.setDhuha(dhuha);
                                        jadwalSholat.setDhuhur(dhuhur);
                                        jadwalSholat.setAshar(ashar);
                                        jadwalSholat.setMaghrib(maghrib);
                                        jadwalSholat.setIsya(isya);
                                        jHelper.AddJadwal(jadwalSholat);
                                    }
                                }
                            }else{

                            }
                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }
    }
    public Date convertDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }
    public Date convertTime(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
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
}
