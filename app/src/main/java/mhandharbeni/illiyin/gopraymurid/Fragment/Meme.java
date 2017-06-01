package mhandharbeni.illiyin.gopraymurid.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.golovin.fluentstackbar.FluentSnackbar;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.realm.RealmResults;
import mhandharbeni.illiyin.gopraymurid.Adapter.QuoteAdapter;
import mhandharbeni.illiyin.gopraymurid.Adapter.model.QuoteModel;
import mhandharbeni.illiyin.gopraymurid.MainActivity;
import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.database.Quote;
import mhandharbeni.illiyin.gopraymurid.database.helper.QuoteHelper;
import mhandharbeni.illiyin.gopraymurid.service.intent.QuoteService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

//import com.dd.morphingbutton.MorphingButton;

/**
 * Created by root on 19/04/17.
 */

public class Meme extends Fragment  implements ConnectivityChangeListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    private static final int REQUEST_CODE_CHOOSE = 23;
    ListView listView;
    View v;
    ImageButton imagePick;
    EditText txtText;
    OkHttpClient client;
    EncryptedPreferences encryptedPreferences;
    Button btnSimpan;
    String endUri;
    String endUriDelete;

    ArrayList<QuoteModel> dataModels;
    QuoteHelper qHelper;
    private static QuoteAdapter adapter;
    private FluentSnackbar mFluentSnackbar;
    LovelyProgressDialog lovelyProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(this.receiver, new IntentFilter("QUOTE"));
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        encryptedPreferences.edit().putString("imagepath", "nothing").apply();
        encryptedPreferences.edit().putString("imagename", "nothing").apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new OkHttpClient();
        qHelper = new QuoteHelper(getActivity().getApplicationContext());
        v = inflater.inflate(R.layout.fragment_meme, container, false);
        endUri = getResources().getString(R.string.server)+"/"+getResources().getString(R.string.vServer)+"/"+"users/self/meme";
        endUriDelete = getResources().getString(R.string.server)+"/"+getResources().getString(R.string.vServer)+"/"+"users/self/deletememe";
        imagePick = (ImageButton) v.findViewById(R.id.imgPick);
        txtText = (EditText) v.findViewById(R.id.txtText);
        btnSimpan = (Button) v.findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpload();
            }
        });
        imagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
        listView = (ListView) v.findViewById(R.id.listViewQuote);
        registerForContextMenu(listView);
        initData();
        initAdapter();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        return v;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        Adapter adapter = getListAdapter();
        Object item = adapter.getItem(info.position);



        menu.setHeaderTitle("Choose");
        menu.add(0, adapter.getItem(info.position).getId(), 0, "Share");
        menu.add(1, adapter.getItem(info.position).getId(), 0, "Delete");

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle() == "Delete") {
            dialogDelete(item.getItemId());
        } else if (item.getTitle() == "Share") {
            shareQuote(item.getItemId());
        } else {
            return false;
        }
        return true;
    }

    public void shareQuote(int id){
        String paths = qHelper.getUrlImage(id);
        if (!paths.equalsIgnoreCase("nothing")){
            Picasso.with(getActivity().getApplicationContext()).load(paths).into(new Target() {
                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("image/*");
                    i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                    startActivity(Intent.createChooser(i, "Share Quote"));
                }
                @Override public void onBitmapFailed(Drawable errorDrawable) { }
                @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
            });
        }
    }

    public void doUpload(){
        if(encryptedPreferences.getString("NETWORK","0").equalsIgnoreCase("1")){
            String text = txtText.getText().toString();
            if(text.isEmpty()){
                txtText.setError("Tidak Boleh Kosong");
            }else{
                btnSimpan.setEnabled(FALSE);
                ((MainActivity)getActivity()).showProgress();
                uploadQuote();
            }
        }
    }
    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    public void getImage(){
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
    public void dialogDelete(final int id){
        new LovelyStandardDialog(getActivity())
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setTitle("HAPUS")
                .setMessage("Hapus Quote?")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                /*HAPUS AKTIVITAS*/
                        deleteTimeline(id);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    public void deleteTimeline(final int id){
        final int idTemp;
        idTemp = id;
        String token = encryptedPreferences.getString(KEY, getString(R.string.dummyToken));
        String decryptToken = encryptedPreferences.getUtils().decryptStringValue(token);
        /*delete server*/
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("access_token", decryptToken)
                .addFormDataPart("id_meme", String.valueOf(id))
                .build();
        Request request = new Request.Builder()
                .url(endUriDelete)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                reload(id);
            }
        });
        /*delete server*/
    }
    public void reload(final int id){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qHelper.deleteData(id);
                adapter.clear();
                addDataAdapter();
                adapter.notifyDataSetChanged();
            }
        });
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
        }
    }
    public void showSnackBars(String message){
        mFluentSnackbar = FluentSnackbar.create(getActivity());
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
    public void uploadQuote(){
        lovelyProgressDialog = new LovelyProgressDialog(getActivity());
        lovelyProgressDialog
                .setIcon(R.drawable.ic_logo)
                .setTitle(R.string.prosesing)
                .setTopColorRes(R.color.colorPrimary)
                .setCancelable(false)
                .show();
        String accessToken = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, getResources().getString(R.string.dummyToken)));
        if (!encryptedPreferences.getString("imagepath", "nothing").equalsIgnoreCase("nothing")){
            File sourceFile = new File(encryptedPreferences.getString("imagepath", ""));
            final MediaType MEDIA_TYPE = encryptedPreferences.getString("imagepath", "").endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("access_token", accessToken)
                    .addFormDataPart("gambar", encryptedPreferences.getString("imagename", ""), RequestBody.create(MEDIA_TYPE, sourceFile))
                    .addFormDataPart("text", txtText.getText().toString())
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
                    showMessage();
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
                        showMessage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("access_token", accessToken)
                    .addFormDataPart("text", txtText.getText().toString())
                    .build();
            Request request = new Request.Builder()
                    .url(endUri)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    encryptedPreferences.edit().putString("SUCCESS", "0").putString("MESSAGE", "GAGAL").apply();
                    showMessage();
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
                        showMessage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    public void showMessage(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String success = encryptedPreferences.getString("SUCCESS", "0");
                String message = encryptedPreferences.getString("MESSAGE", "GAGAL");
                if (success.equalsIgnoreCase("1")){
                    lovelyProgressDialog.setTitle(R.string.selesai);
                    btnSimpan.setEnabled(TRUE);
                    Intent iQuote = new Intent(getActivity(), QuoteService.class);
                    getActivity().startService(iQuote);
                    lovelyProgressDialog.setTitle(R.string.mengunggah);
                    lovelyProgressDialog.setTitle(R.string.selesai);
                    lovelyProgressDialog.dismiss();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

//                    showSnackBar("SUKSES");
                }else{
                    btnSimpan.setEnabled(TRUE);
                    lovelyProgressDialog.dismiss();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

//                    showSnackBar(message);
                }
            }
        });
    }
    public void initData(){
        dataModels = new ArrayList<>();
        RealmResults<Quote>
                result = qHelper.getQuote();
        int z = 0;
        for (int i=0; i<result.size(); i++){
            String tl;
            if (z == (result.size()-1)) {
                tl = "start";
            }else{
                tl = "content";
            }
            z++;
            QuoteModel qm = new QuoteModel(result.get(i).getId(),
                                result.get(i).getPath_meme(),
                                result.get(i).getTanggal()+" "+result.get(i).getJam(),
                                result.get(i).getJam(),
                                tl);
            dataModels.add(qm);
        }
    }
    public void addDataAdapter(){
        RealmResults<Quote>
                result = qHelper.getQuote();
        int z = 0;
        for (int i=0; i<result.size(); i++){
            String tl;
            if (z == (result.size()-1)) {
                tl = "start";
            }else{
                tl = "content";
            }
            z++;
            QuoteModel qm = new QuoteModel(result.get(i).getId(),
                    result.get(i).getPath_meme(),
                    result.get(i).getTanggal()+" "+result.get(i).getJam(),
                    result.get(i).getJam(),
                    tl);
            adapter.add(qm);
        }
    }
    public void initAdapter(){
        adapter= new QuoteAdapter(dataModels,getActivity().getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuoteModel dataModel= dataModels.get(position);
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
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.receiver, new IntentFilter("QUOTE"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.receiver, new IntentFilter("QUOTE"));
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
                case "UPDATE QUOTE":
                    adapter.clear();
                    addDataAdapter();
                    adapter.notifyDataSetChanged();
                    ((MainActivity)getActivity()).stopProgress();
                    break;
                default:
                    break;
            }


        }
    };
}
