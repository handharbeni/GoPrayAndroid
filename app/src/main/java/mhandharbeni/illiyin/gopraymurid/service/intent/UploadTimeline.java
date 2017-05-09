package mhandharbeni.illiyin.gopraymurid.service.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.database.Timeline;
import mhandharbeni.illiyin.gopraymurid.database.helper.TimelineHelper;
import mhandharbeni.illiyin.gopraymurid.database.helper.UploadHelper;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.MediaType;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

/**
 * Created by root on 09/05/17.
 */

public class UploadTimeline extends IntentService implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA = "nama", EMAIL = "email", PICTURE = "gambar";
    TimelineHelper uHelper;
    HttPizza client;
    EncryptedPreferences encryptedPreferences;

    String endUri;
    Context context;

    public UploadTimeline() {
        super("UPLOAD TIMELINE SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = getBaseContext();
        //init encrypting
        encryptedPreferences = new EncryptedPreferences.Builder(getBaseContext()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        //init encrypting
        //httppizza
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new HttPizza();
        //httppizza
        endUri = getResources().getString(R.string.server)+"/"+getResources().getString(R.string.vServer)+"/"+"users/self/timeline";
        uHelper = new TimelineHelper(getBaseContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (encryptedPreferences.getString("NETWORK", "0").equalsIgnoreCase("1")) {
            syncData();
            stopSelf();
        }
    }

    public void syncData() {
        String token = encryptedPreferences.getString(KEY, "0");
        if (!token.equalsIgnoreCase("0")) {
            final Realm realms = Realm.getDefaultInstance();
            RealmResults<Timeline> resultNonUploaded =
                    realms.where(Timeline.class).equalTo("status", 1).findAll();
            for(int i=0;i<resultNonUploaded.size();i++) {
                String key = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, "8aa560b5efa8691dddcf4a11a69b3d1f98f01280bb6ef6efbd32ca39d9b8ac33"));
                final int tempId = resultNonUploaded.get(i).getId();
                RequestBody requestBody = new FormBody.Builder()
                        .add("access_token", key)
                        .add("id_aktivitas", String.valueOf(resultNonUploaded.get(i).getId_aktivitas()))
                        .add("id_ibadah", String.valueOf(resultNonUploaded.get(i).getId_ibadah()))
                        .add("tempat", resultNonUploaded.get(i).getTempat())
                        .add("bersama", resultNonUploaded.get(i).getBersama())
                        .add("gambar", resultNonUploaded.get(i).getImage())
                        .add("nominal", String.valueOf(resultNonUploaded.get(i).getNominal()))
                        .add("point", String.valueOf(resultNonUploaded.get(i).getPoint()))
                        .add("tanggal", resultNonUploaded.get(i).getDate())
                        .add("jam", resultNonUploaded.get(i).getJam())
                        .build();
                Request request = client.newRequest()
                        .url(endUri)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Response response) {
                            /*update status timeline to 2*/
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if(jsonObject.getBoolean("return")){
                                updateStatus(tempId, 2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                            /*update status timeline to 2*/
                    }

                    @Override
                    public void onFailure(Throwable t) {
                            /*ignoring all status*/
                        Log.d("THROWABLE", "onFailure: "+t.getMessage());
                            /*ignoring all status*/
                    }
                });
            }
            realms.close();
        }

    }
    public void updateStatus(int id, int status){
        uHelper.updateStatus(id, status);
    }
    @Override
    public void onDestroy() {
        uHelper.closeRealm();
        super.onDestroy();
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        if (event.getState().getValue() == ConnectivityState.CONNECTED) {
            encryptedPreferences.edit()
                    .putString("NETWORK", "1")
                    .apply();
        } else {
            encryptedPreferences.edit()
                    .putString("NETWORK", "0")
                    .apply();
        }
    }
}