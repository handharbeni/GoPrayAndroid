package salam.gopray.id.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import salam.gopray.id.R;
import salam.gopray.id.database.helper.MessageParentHelper;
import sexy.code.HttPizza;

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
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/timeline?access_token=";
        mpHelper = new MessageParentHelper(getBaseContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("MessageParentService", "MessageParentServiceIsRunning: ");
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
