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
import salam.gopray.id.database.helper.TimelineHelper;
import sexy.code.Callback;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.Response;

/**
 * Created by root on 02/05/17.
 */

public class ServiceTimeline extends IntentService implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    TimelineHelper tHelper;
    TimelineHelper tHelper2;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;

    String endUri;

    public ServiceTimeline() {
        super("TIMELINE SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        encryptedPreferences = new EncryptedPreferences.Builder(getBaseContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/timeline?access_token=";
        tHelper = new TimelineHelper(getBaseContext());
        tHelper2 = new TimelineHelper(getBaseContext());
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
                                    int id = Integer.valueOf(objectData.getString("id_timeline"));
                                    int idUser = Integer.valueOf(objectData.getString("id_user"));
                                    int idAktivitas = Integer.valueOf(objectData.getString("id_aktivitas"));
                                    int idIbadah = Integer.valueOf(objectData.getString("id_ibadah"));
                                    String namaAktivitas = objectData.getString("nama_aktivitas");
                                    String namaIbadah = objectData.getString("ibadah");
                                    String tempat = objectData.getString("tempat");
                                    String bersama = objectData.getString("bersama");
                                    String image = objectData.getString("image");
                                    int nNominal = 0;
                                    if (isInteger(objectData.getString("nominal"))){
                                        nNominal = Integer.valueOf(objectData.getString("nominal"));
                                    }
                                    int nominal = nNominal;
                                    int poit = Integer.valueOf(objectData.getString("point"));
                                    int status = 3; /*1 not uploaded, 2 uploaded, 3 sync server*/
                                    String tanggal = objectData.getString("tanggal");
                                    String jam = objectData.getString("jam");

                                    boolean duplicate = tHelper.checkDuplicate(id);
                                    if(!duplicate){
                                        salam.gopray.id.database.Timeline tl = new salam.gopray.id.database.Timeline();
                                        tl.setId(id);
                                        tl.setId_user(idUser);
                                        tl.setId_aktivitas(idAktivitas);
                                        tl.setId_ibadah(idIbadah);
                                        tl.setNama_aktivitas(namaAktivitas);
                                        tl.setNama_ibadah(namaIbadah);
                                        tl.setImage(image);
                                        tl.setTempat(tempat);
                                        tl.setBersama(bersama);
                                        tl.setPoint(poit);
                                        tl.setNominal(nominal);
                                        tl.setDate(tanggal);
                                        tl.setJam(jam);
                                        tl.setStatus(status);
                                        tHelper.AddTimeline(tl);
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
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    @Override
    public void onDestroy() {
        tHelper.closeRealm();
        super.onDestroy();
    }
    public void sendBroadCast(){
        this.sendBroadcast(new Intent().setAction("UPDATE TIMELINE").putExtra("MODE", "UPDATE LIST"));
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
