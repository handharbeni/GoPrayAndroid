package salam.gopray.id.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.os.StrictMode;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import salam.gopray.id.R;
import salam.gopray.id.database.MessageParent;
import salam.gopray.id.database.helper.MessageParentHelper;
import sexy.code.Callback;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.Response;

/**
 * Created by root on 12/06/17.
 */

public class MessageParentService extends IntentService implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    MessageParentHelper mpHelper;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;

    String endUri;

    public MessageParentService() {
        super("MessageParentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        encryptedPreferences = new EncryptedPreferences.Builder(getBaseContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/pesan?access_token=";
        mpHelper = new MessageParentHelper(getBaseContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (encryptedPreferences.getString("NETWORK","0").equalsIgnoreCase("1")){
            syncData();
            sendBroadcast();
            stopSelf();
        }
        sendBroadcast();
    }

    public void syncData(){
        String token = encryptedPreferences.getString(KEY, "0");
        if(!token.equalsIgnoreCase("0")){
            String decryptToken = encryptedPreferences.getUtils().decryptStringValue(token);
            endUri += decryptToken;
            Request request = client.newRequest().url(endUri).get().build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Response response) {
                    try {
                        JSONObject retur= new JSONObject(response.body().string());
                        boolean bReturn = retur.getBoolean("return");
                        if (bReturn){
                            JSONArray subRetur = retur.getJSONArray("data");
                            if (subRetur.length() > 0){
                                for (int i=0;i<subRetur.length();i++){
                                    JSONObject objectData = subRetur.getJSONObject(i);
                                    String id = objectData.getString("id");
                                    String type = objectData.getString("type");
                                    String propict = objectData.getString("propict");
                                    String pesan = objectData.getString("pesan");
                                    String gambar = objectData.getString("gambar");
                                    String tanggal = objectData.getString("tanggal");
                                    String jam = objectData.getString("jam");
                                    boolean duplicate = mpHelper.checkDuplicate(Integer.valueOf(id));
                                    if (!duplicate){
                                        MessageParent mp = new MessageParent();
                                        mp.setId(Integer.valueOf(id));
                                        mp.setType(Integer.valueOf(type));
                                        mp.setProfpict(propict);
                                        mp.setMessage(pesan);
                                        mp.setPhoto(gambar);
                                        mp.setDate(tanggal);
                                        mp.setTime(jam);
                                        mpHelper.addMessage(mp);
                                    }
                                }
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

        }
    }
    public void sendBroadcast(){
        this.sendBroadcast(new Intent().setAction("UPDATE MESSAGE").putExtra("MODE", "UPDATE LIST"));
    }
    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        if(event.getState().getValue() == ConnectivityState.CONNECTED){
            encryptedPreferences.edit()
                    .putString("NETWORK", "1")
                    .apply();
        }
        else{
            encryptedPreferences.edit()
                    .putString("NETWORK", "0")
                    .apply();
        }
    }
}
