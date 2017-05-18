package mhandharbeni.illiyin.gopraymurid.service.intent;

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

import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.database.Quote;
import mhandharbeni.illiyin.gopraymurid.database.helper.QuoteHelper;
import mhandharbeni.illiyin.gopraymurid.database.helper.TimelineHelper;
import sexy.code.Callback;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.Response;

/**
 * Created by root on 15/05/17.
 */

public class QuoteService extends IntentService implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    QuoteHelper qHelper;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;

    String endUri;

    public QuoteService() {
        super("QUOTE SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //init encrypting
        encryptedPreferences = new EncryptedPreferences.Builder(getBaseContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        //init encrypting
        //httppizza
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        //httppizza
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/meme?access_token=";
        qHelper = new QuoteHelper(getBaseContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (encryptedPreferences.getString("NETWORK","0").equalsIgnoreCase("1")){
            syncData();
            sendBroadCast();
            stopSelf();
        }
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Boolean retur = jsonObject.getBoolean("return");
                        if(retur){
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray.length() > 0){
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject objectData = jsonArray.getJSONObject(i);
                                    int id = objectData.getInt("id");
                                    String pathMeme = objectData.getString("path_meme");
                                    String tanggal = objectData.getString("tanggal");
                                    String jam = objectData.getString("jam");
                                    if(!qHelper.checkDuplicate(id)){
                                        /*insert to db*/
                                        Quote q = new Quote();
                                        q.setId(id);
                                        q.setPath_meme(pathMeme);
                                        q.setTanggal(tanggal);
                                        q.setJam(jam);
                                        qHelper.addQuote(q);
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
    @Override
    public void onDestroy() {
        qHelper.closeRealm();
        super.onDestroy();
    }
    public void sendBroadCast(){
        this.sendBroadcast(new Intent().setAction("QUOTE").putExtra("MODE", "UPDATE QUOTE"));
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
