package salam.gopray.id.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.os.StrictMode;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import salam.gopray.id.R;
import sexy.code.Callback;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.Response;

/**
 * Created by root on 10/05/17.
 */

public class ProfilePictureService extends IntentService implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    HttPizza client;
    EncryptedPreferences encryptedPreferences;

    String endUri;
    public ProfilePictureService() {
        super("PROFILE PICTURE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        encryptedPreferences = new EncryptedPreferences.Builder(getBaseContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/profile?access_token=";
        return super.onStartCommand(intent, flags, startId);
    }
    public void getImage(){
        if (encryptedPreferences.getString("NETWORK","1").equalsIgnoreCase("1")){
            String accessToken = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, getResources().getString(R.string.dummyToken)));
            endUri = endUri+accessToken;
            Request request = client.newRequest().url(endUri).get().build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        boolean retur = jsonObject.getBoolean("return");
                        if (retur){
                            changePref(jsonObject.getString("picture"));
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
    public void changePref(String image){
        encryptedPreferences.edit().putString(PICTURE, image).apply();
        sendBroadCast();
    }
    public void sendBroadCast(){
        this.sendBroadcast(new Intent().setAction("UPDATE TIMELINE").putExtra("MODE", "UPDATE PROFPICT"));
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (encryptedPreferences.getString("NETWORK","0").equalsIgnoreCase("1")){
            getImage();
            stopSelf();
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
}
