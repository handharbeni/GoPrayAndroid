package salam.gopray.id.Fragment.setting;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import salam.gopray.id.R;
import salam.gopray.id.service.intent.ProfilePictureService;

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 26/05/17.
 */

public class FragmentSettingAkun extends Fragment implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    EncryptedPreferences encryptedPreferences;
    CircleImageView profpict;
    private static final int REQUEST_CODE_CHOOSE = 23;

    String endUri;

    LovelyProgressDialog lovelyProgressDialog;
    OkHttpClient client;

    MaterialEditText txtSEmail, txtSPassword, txtSNama;

    View v;
    ScrollView scAkun;
    RelativeLayout rlAkun;
    int width, height;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new OkHttpClient();

        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE TIMELINE"));
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        encryptedPreferences.edit().putString("imagepath", "nothing").apply();
        encryptedPreferences.edit().putString("imagename", "nothing").apply();
    }
    private final void focusOnView(final View parent, final View v){
        parent.post(new Runnable() {
            @Override
            public void run() {
                parent.scrollTo(0, v.getBottom());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        endUri = getResources().getString(R.string.server)+"/"+getResources().getString(R.string.vServer)+"/"+"users/self/profile";
        v = inflater.inflate(R.layout.setting_akun, container, false);
        profpict = (CircleImageView) v.findViewById(R.id.profile_image);
        txtSEmail = (MaterialEditText) v.findViewById(R.id.txtSEmail);
        txtSPassword = (MaterialEditText) v.findViewById(R.id.txtSPassword);
        txtSNama = (MaterialEditText) v.findViewById(R.id.txtSNama);

        txtSEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(scAkun, rlAkun);
                }
            }
        });
        txtSNama.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(scAkun, rlAkun);
                }
            }
        });
        txtSPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focusOnView(scAkun, rlAkun);
                }
            }
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        scAkun = (ScrollView) v.findViewById(R.id.scAkun);
        rlAkun = (RelativeLayout) v.findViewById(R.id.rlAkun);
        rlAkun.setMinimumHeight(height+25);
        initAkun();

        initProfPic();
        profpict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profPictClick();
            }
        });
        return v;
    }
    public void initAkun(){
        String sNama     = encryptedPreferences.getString(NAMA, "");
        String sEmail    = encryptedPreferences.getString(EMAIL, "");
        String sPassword = "DUMMYPASSWORD";

        txtSEmail.setText(sEmail);
        txtSNama.setText(sNama);
        txtSPassword.setText(sPassword);
        txtSNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onNamaChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void initProfPic(){
        String image = encryptedPreferences.getString(PICTURE, getResources().getString(R.string.dummyPicture));
        if (!image.equalsIgnoreCase(null) || !image.equalsIgnoreCase("")){
            profpict = (CircleImageView) v.findViewById(R.id.profile_image);
            Glide.with(getActivity().getApplicationContext()).load(image).into(profpict);
        }
    }
    public void profPictClick(){
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(false)
                .maxSelectable(1)
                .theme(R.style.Matisse_Dracula)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }
    public void savePropic(){
        String accessToken = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, getResources().getString(R.string.dummyToken)));
        if (!encryptedPreferences.getString("imagepath", "nothing").equalsIgnoreCase("nothing")){
            File sourceFile = new File(encryptedPreferences.getString("imagepath", ""));
            final MediaType MEDIA_TYPE = encryptedPreferences.getString("imagepath", "").endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("access_token", accessToken)
                    .addFormDataPart("method", "2")
                    .addFormDataPart("gambar", encryptedPreferences.getString("imagename", ""), RequestBody.create(MEDIA_TYPE, sourceFile))
                    .build();
            Request request = new Request.Builder()
                    .url(endUri)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    encryptedPreferences.edit().putString("imagepath", "nothing").apply();
                    encryptedPreferences.edit().putString("imagename", "nothing").apply();
                    encryptedPreferences.edit().putString("SUCCESS", "0").putString("MESSAGE", "GAGAL").apply();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Boolean results = jsonObject.getBoolean("return");
                        if (results){
                            encryptedPreferences.edit().putString("SUCCESS", "1").apply();
                        }else{
                            String errorMessage = jsonObject.getString("error_message");
                            encryptedPreferences.edit().putString("SUCCESS", "0").putString("MESSAGE", errorMessage).apply();
                        }
                        encryptedPreferences.edit().putString("imagepath", "nothing").apply();
                        encryptedPreferences.edit().putString("imagename", "nothing").apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            lovelyProgressDialog.dismiss();
        }else{
            /*tidak ada gambar dipilih*/
            Log.d("PROFPICT", "savePropic: TIDAK ADA IMAGE DIPILIH");
//            lovelyProgressDialog.dismiss();
        }
        if(!checkIsRunning(ProfilePictureService.class)){
                    /*PROFILE PICTURE SERVICE*/
            Intent pp = new Intent(getActivity().getApplicationContext(), ProfilePictureService.class);
            getActivity().startService(pp);
                    /*PROFILE PICTURE SERVICE*/
        }
    }
    public void passClick(){
        new LovelyCustomDialog(getActivity())
                .setView(R.layout.form_change_password)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle(R.string.app_name)
                .setMessage("Ganti Password")
                .setListener(R.id.ld_btn_yes, null)
                .show();
    }
    public void savePass(){

    }
    public void onNamaChange(final String s){
        String accessToken = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, getResources().getString(R.string.dummyToken)));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("access_token", accessToken)
                    .addFormDataPart("method", "1")
                    .addFormDataPart("nama", s)
                    .build();
            Request request = new Request.Builder()
                    .url(endUri)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Boolean results = jsonObject.getBoolean("return");
                        if (results){
                            encryptedPreferences.edit().putString("SUCCESS", "1").apply();
                        }else{
                            String errorMessage = jsonObject.getString("error_message");
                            encryptedPreferences.edit().putString("SUCCESS", "0").putString("MESSAGE", errorMessage).apply();
                        }
                        encryptedPreferences.edit().putString(NAMA, s).apply();
//                        initAkun();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            for (Uri row : Matisse.obtainResult(data)) {
                String[] filePath = getPath(row).split("/");
                encryptedPreferences.edit().putString("imagepath", getPath(row)).apply();
                encryptedPreferences.edit().putString("imagename", filePath[filePath.length-1]).apply();
            }
            savePropic();
        }
    }
    public String getPath(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE TIMELINE"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE TIMELINE"));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }
    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(this.receiver);
        super.onDestroy();
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String mode = bundle.getString("MODE");
            switch (mode){
                case "UPDATE PROFPICT":
                    initProfPic();
                    break;
                default:
                    break;
            }


        }
    };
    private boolean checkIsRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
