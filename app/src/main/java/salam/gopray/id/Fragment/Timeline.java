package salam.gopray.id.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bvapp.arcmenulibrary.ArcMenu;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmResults;
import salam.gopray.id.Adapter.TimelineAdapter;
import salam.gopray.id.Adapter.model.TimelineModel;
import salam.gopray.id.Fragment.aktivitas.MainAktivitas;
import salam.gopray.id.MainActivity;
import salam.gopray.id.R;
import salam.gopray.id.database.JadwalSholat;
import salam.gopray.id.database.helper.JadwalSholatHelper;
import salam.gopray.id.database.helper.TimelineHelper;
import salam.gopray.id.service.MainServices;
import sexy.code.Callback;
import sexy.code.FormBody;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.RequestBody;
import sexy.code.Response;

/**
 * Created by root on 19/04/17.
 */

public class Timeline extends Fragment implements View.OnClickListener {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    private static final int[] ITEM_DRAWABLES = { R.drawable.timeline_sholat,
            R.drawable.timeline_mengai, R.drawable.timeline_sedekah, R.drawable.timeline_puasa};

    private String endUri;
    View v;
    ArrayList<TimelineModel> dataModels;
    ListView listView;
    private static TimelineAdapter adapter;
    RelativeLayout dimView;
    CircleImageView profpict;

    TimelineHelper tlHelper;
    JadwalSholatHelper jsHelper;


    HttPizza client;
    ArcMenu menu;

    EncryptedPreferences encryptedPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE TIMELINE"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).startServices();
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        if (encryptedPreferences.getString("NOTIFTROPHY", "NOTHING").equalsIgnoreCase("NOTHING")){
            encryptedPreferences.edit().putString("NOTIFTROPHY", "0").commit();
        }
        client = new HttPizza();
        v = inflater.inflate(R.layout.fragment_timeline, container, false);
        tlHelper = new TimelineHelper(getActivity().getApplicationContext());
        jsHelper = new JadwalSholatHelper(getActivity().getApplicationContext());
        listView=(ListView) v.findViewById(R.id.listViewTimeline);
        getActivity().registerForContextMenu(listView);
        dimView = (RelativeLayout) v.findViewById(R.id.dimScreen);
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/deletetimeline";
        initProfilePicture();
        displayInfo();
        getJadwalSholat();
        getCurrentSholat();
        initArc();
        initData();
        initAdapter();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        return v;
    }
    public void initProfilePicture(){
        String image = encryptedPreferences.getString(PICTURE, getResources().getString(R.string.dummyPicture));
        if (!image.equalsIgnoreCase(null) || !image.equalsIgnoreCase("")){
            profpict = (CircleImageView) v.findViewById(R.id.profile_image);
            Glide.with(getActivity().getApplicationContext()).load(image).into(profpict);
        }
    }
    public void initArc(){
        menu = (ArcMenu) v.findViewById(R.id.arcMenu);
        menu.setMenuGravity(ArcMenu.BOTTOM_MIDDLE);
        menu.setClipChildren(true);
        menu.setPadding(0, 0, 0, 25);
        menu.setPadding(0, 0, 0, 25);
        menu.attachToListView(listView);
        menu.setArc(195.0f, 345.0f);
        menu.showTooltip(false);
        menu.setAnim(600,600,ArcMenu.ANIM_BOTTOM_TO_DOWN,ArcMenu.ANIM_BOTTOM_TO_DOWN,
                ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE,ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE);
        final int itemCount = ITEM_DRAWABLES.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(getActivity().getApplicationContext());
            Glide.with(getActivity().getApplicationContext()).load(ITEM_DRAWABLES[i]).into(item);
            final int position = i;
            item.setId(i);
            menu.addItem(item, "", new View.OnClickListener() {
                @Override
                public void onClick(View s) {
                    switch (s.getId()){
                        case 0 :
                            /*sholat*/
                            addAktivitas(0);
                            break;
                        case 1 :
                            /*mengaji*/
                            addAktivitas(1);
                            break;
                        case 2 :
                            /*sedekah*/
                            addAktivitas(2);
                            break;
                        case 3 :
                            /*puasa*/
                            addAktivitas(3);
                            break;
                    }
                }
            });
        }
        menu.setOnClickListener(this);
    }
    public void addAktivitas(int mode){
        Intent iAktivitas = new Intent(getActivity().getApplicationContext(), MainAktivitas.class);
        iAktivitas.putExtra("MODE", mode);
        startActivity(iAktivitas);
    }
    public void initData(){
        dataModels = new ArrayList<>();
        RealmResults<salam.gopray.id.database.Timeline>
                result = tlHelper.getTimeline(1);
        int z = 0;
        for (int i=0; i<result.size(); i++){
            String tl;
            if (z == (result.size()-1)) {
                tl = "start";
            }else{
                tl = "content";
            }
            z++;
            String namaIbadah = result.get(i).getNama_ibadah();
            int dr = R.drawable.timeline_berdoa;
            switch (result.get(i).getId_aktivitas()){
                case 1:
                    dr = R.drawable.timeline_berdoa;
                    break;
                case 2:
                    dr = R.drawable.timeline_puasa;
                    if (result.get(i).getId_ibadah() == 1){
                        namaIbadah = "Puasa Ramadhan";
                    }else if (result.get(i).getId_ibadah() == 2){
                        namaIbadah = "Puasa Senin Kamis";
                    }else if (result.get(i).getId_ibadah() == 3){
                        namaIbadah = "Puasa Sunnah";
                    }
                    break;
                case 3:
                    dr = R.drawable.timeline_sholat;
                    if (result.get(i).getId_ibadah() == 1){
                        namaIbadah = "Sholat Subuh";
                    }else if (result.get(i).getId_ibadah() == 2){
                        namaIbadah = "Sholat Dhuhur";
                    }else if (result.get(i).getId_ibadah() == 3){
                        namaIbadah = "Sholat Ashar";
                    }else if (result.get(i).getId_ibadah() == 4){
                        namaIbadah = "Sholat Maghrib";
                    }else if (result.get(i).getId_ibadah() == 5){
                        namaIbadah = "Sholat Isya";
                    }else if (result.get(i).getId_ibadah() == 6){
                        namaIbadah = "Sholat Sunnah";
                    }
                    break;
                case 4:
                    dr = R.drawable.timeline_sedekah;
                    break;
                case 5:
                case 6:
                    dr = R.drawable.timeline_meme;
                    break;
                case 7:
                    dr = R.drawable.timeline_mengai;
                    break;
            }
            dataModels.add(new TimelineModel(result.get(i).getId(),result.get(i).getId_aktivitas(), dr, result.get(i).getImage(),
                    namaIbadah, result.get(i).getBersama(), result.get(i).getTempat(),
                    result.get(i).getDate()+"  "+result.get(i).getJam(), tl, String.valueOf(result.get(i).getNominal())));
        }
    }
    public void addDataAdapter(){
        RealmResults<salam.gopray.id.database.Timeline>
                result = tlHelper.getTimeline(1);
        int z = 0;
        for (int i=0; i<result.size(); i++){
            String tl;
            if (z == (result.size()-1)) {
                tl = "start";
            }else{
                tl = "content";
            }
            z++;
            String namaIbadah = result.get(i).getNama_ibadah();
            int dr = R.drawable.timeline_berdoa;
            switch (result.get(i).getId_aktivitas()){
                case 1:
                    dr = R.drawable.timeline_berdoa;
                    break;
                case 2:
                    dr = R.drawable.timeline_puasa;
                    if (result.get(i).getId_ibadah() == 1){
                        namaIbadah = "Puasa Ramadhan";
                    }else if (result.get(i).getId_ibadah() == 2){
                        namaIbadah = "Puasa Senin Kamis";
                    }else if (result.get(i).getId_ibadah() == 3){
                        namaIbadah = "Puasa Sunnah";
                    }
                    break;
                case 3:
                    dr = R.drawable.timeline_sholat;
                    if (result.get(i).getId_ibadah() == 1){
                        namaIbadah = "Sholat Subuh";
                    }else if (result.get(i).getId_ibadah() == 2){
                        namaIbadah = "Sholat Dhuhur";
                    }else if (result.get(i).getId_ibadah() == 3){
                        namaIbadah = "Sholat Ashar";
                    }else if (result.get(i).getId_ibadah() == 4){
                        namaIbadah = "Sholat Maghrib";
                    }else if (result.get(i).getId_ibadah() == 5){
                        namaIbadah = "Sholat Isya";
                    }else if (result.get(i).getId_ibadah() == 6){
                        namaIbadah = "Sholat Sunnah";
                    }
                    break;
                case 4:
                    dr = R.drawable.timeline_sedekah;
                    break;
                case 5:
                case 6:
                    dr = R.drawable.timeline_meme;
                    break;
                case 7:
                    dr = R.drawable.timeline_mengai;
                    break;
            }
            adapter.add(new TimelineModel(result.get(i).getId(),result.get(i).getId_aktivitas(), dr, result.get(i).getImage(),
                    namaIbadah, result.get(i).getBersama(), result.get(i).getTempat(),
                    result.get(i).getDate()+"  "+result.get(i).getJam(), tl, String.valueOf(result.get(i).getNominal())));
        }
    }
    public SpannableStringBuilder setTextWithSpan(String text, StyleSpan style) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        int start = 0;
        int end = start + text.length();
        sb.setSpan(style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    public void initAdapter(){
        adapter= new TimelineAdapter(dataModels,getActivity().getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimelineModel dataModel= dataModels.get(position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TimelineModel dataModel= dataModels.get(position);
                new LovelyStandardDialog(getActivity())
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.colorAccent)
                        .setTitle("HAPUS")
                        .setMessage("Hapus Aktivitas?")
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*HAPUS AKTIVITAS*/
                                deleteActivity(dataModel.getId());
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return false;
            }
        });
    }
    public void deleteActivity(int id){
        deleteTimeline(id);
    }
    public void displayInfo(){
        String nama = encryptedPreferences.getString(NAMA, "");
        TextView txtRemainTime = (TextView) v.findViewById(R.id.txtRemainTime);
        ImageView imgTropi1 = (ImageView) v.findViewById(R.id.imgTropi1);
        Glide.with(getActivity().getApplicationContext()).load("").into(imgTropi1);
        txtRemainTime.setText(nama);
        TextView txtPoint = (TextView) v.findViewById(R.id.txtPoint);
        RealmResults<salam.gopray.id.database.Timeline>
                result = tlHelper.getTimeline(1);
        int point = 0;
        for (int i=0;i<result.size();i++){
            point += result.get(i).getPoint();
        }
        txtPoint.setText(String.valueOf(point));
        String NOTIFTRPY = encryptedPreferences.getString("NOTIFTROPHY", "0");
        int NTY = Integer.valueOf(NOTIFTRPY);
        if (point < 499){
            if (NTY != 0){
                if(NTY > 0){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "TROPHY ANDA KAMI HAPUS, TINGKATANKAN IBADAH ANDA :)");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT DATANG, :)");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "0").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.placeholder_trophy).into(imgTropi1);

        }else if (point > 500 && point < 999 ){

            if (NTY != 1){
                if(NTY > 1){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI BRONZE");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY BRONZE");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "1").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.bronze).into(imgTropi1);
        }else if(point > 1000 && point < 1499){
            if (NTY != 2){
                if(NTY > 2){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI SILVER");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY SILVER");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "2").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.silver).into(imgTropi1);
        }else if(point > 1500 && point < 1999){
            if (NTY != 3){
                if(NTY > 3){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI GOLD");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY GOLD");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "3").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.gold).into(imgTropi1);
        }else if(point > 2000){
            if (NTY != 4){
                if(NTY > 4){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI PLATINUM");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY PLATINUM");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "4").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.platinum).into(imgTropi1);
        }
    }
    public void showNotifTrophy(Drawable drawablex, String text){
        new LovelyStandardDialog(getActivity())
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_logo)
                .setTitle(R.string.app_name)
                .setMessage(text)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }
    public void displayInfoTropy(){
        ImageView imgTropi1 = (ImageView) v.findViewById(R.id.imgTropi1);
        Glide.with(getActivity().getApplicationContext()).load("").into(imgTropi1);
        TextView txtPoint = (TextView) v.findViewById(R.id.txtPoint);
        RealmResults<salam.gopray.id.database.Timeline>
                result = tlHelper.getTimeline(1);
        int point = 0;
        for (int i=0;i<result.size();i++){
            point += result.get(i).getPoint();
        }
        txtPoint.setText(String.valueOf(point));
        String NOTIFTRPY = encryptedPreferences.getString("NOTIFTROPHY", "0");
        int NTY = Integer.valueOf(NOTIFTRPY);
        if (point < 499){
            if (NTY != 0){
                if(NTY > 0){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "TROPHY ANDA KAMI HAPUS, TINGKATANKAN IBADAH ANDA :)");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT DATANG, :)");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "0").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.placeholder_trophy).into(imgTropi1);

        }else if (point > 500 && point < 999 ){

            if (NTY != 1){
                if(NTY > 1){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI BRONZE");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY BRONZE");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "1").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.bronze).into(imgTropi1);
        }else if(point > 1000 && point < 1499){
            if (NTY != 2){
                if(NTY > 2){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI SILVER");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY SILVER");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "2").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.silver).into(imgTropi1);
        }else if(point > 1500 && point < 1999){
            if (NTY != 3){
                if(NTY > 3){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI GOLD");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY GOLD");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "3").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.gold).into(imgTropi1);
        }else if(point > 2000){
            if (NTY != 4){
                if(NTY > 4){
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "MAAF TROPHY ANDA TURUN MENJADI PLATINUM");
                }else{
                    showNotifTrophy(getResources().getDrawable(R.drawable.bronze), "SELAMAT ANDA MENDAPAT TROPHY PLATINUM");
                }
                encryptedPreferences.edit().putString("NOTIFTROPHY", "4").commit();
            }
            Glide.with(getActivity().getApplicationContext()).load("").placeholder(R.drawable.platinum).into(imgTropi1);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listViewTimeline) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_context_timeline, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                // remove stuff here
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void getJadwalSholat(){
        String tanggal = getDate();
        JadwalSholat js = new JadwalSholat();
        RealmResults<JadwalSholat> rjs = jsHelper.getJadwalSholat(tanggal);
        if (rjs.size() > 0){
            String dSubuh     = rjs.get(0).getSubuh();
            String dDhuha     = rjs.get(0).getDhuha();
            String dDhuhur    = rjs.get(0).getDhuhur();
            String dAshar     = rjs.get(0).getAshar();
            String dMaghrib   = rjs.get(0).getMaghrib();
            String dIsya      = rjs.get(0).getIsya();
            TextView txtNamaSholat = (TextView) v.findViewById(R.id.txtNamaSholat);
            txtNamaSholat.setText(dSubuh);
        }
    }
    public Date convertDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    public void getCurrentSholat(){
        String tanggal = getDate();
        String jam = getTime();
        RealmResults<JadwalSholat> rjs = jsHelper.getJadwalSholat(tanggal);
        if (rjs.size() > 0){

            String dSubuh     = rjs.get(0).getSubuh();
            String dDhuha     = rjs.get(0).getDhuha();
            String dDhuhur    = rjs.get(0).getDhuhur();
            String dAshar     = rjs.get(0).getAshar();
            String dMaghrib   = rjs.get(0).getMaghrib();
            String dIsya      = rjs.get(0).getIsya();
            checkLewatWaktu(jam,dSubuh,dDhuha,dDhuhur,dAshar,dMaghrib,dIsya);

        }
    }
    public void checkLewatWaktu(String waktuSekarang,
                                String waktuSubuh,
                                String waktuDhuha,
                                String waktuDhuhur,
                                String waktuAshar,
                                String waktuMaghrib,
                                String waktuIsya){

        TextView txtNamaSholat = (TextView) v.findViewById(R.id.txtNamaSholat);
        TextView txtRemainTime = (TextView) v.findViewById(R.id.txtRemainTime);
        String remain = "";

        long wSekarang = stringToTime(waktuSekarang);
        long wSubuh = stringToTime(waktuSubuh);
        long wDhuha = stringToTime(waktuDhuha);
        long wDhuhur = stringToTime(waktuDhuhur);
        long wAshar = stringToTime(waktuAshar);
        long wMaghrib = stringToTime(waktuMaghrib);
        long wIsya = stringToTime(waktuIsya);
        String label = "Subuh";
        long remainTime = 0;
        if (wSekarang < wSubuh){
            label = "Subuh";
            remainTime = wSekarang - wSubuh;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wDhuha){
            label = "Dhuha";
            remainTime = wSekarang - wDhuha;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wDhuhur){
            label = "Dhuhur";
            remainTime = wSekarang - wDhuhur;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wAshar){
            label = "Ashar";
            remainTime = wSekarang - wAshar;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wMaghrib){
            label = "Maghrib";
            remainTime = wSekarang - wMaghrib;
            remain = getRemainTime(remainTime);
        }else if(wSekarang < wIsya){
            label = "Isya";
            remainTime = wSekarang - wIsya;
            remain = getRemainTime(remainTime);
        }else if (wSekarang > wIsya){
            label = "Subuh";
            remain = "Besok Jam "+waktuSubuh;
        }
        txtNamaSholat.setText(label);
        txtRemainTime.setText(remain);
    }
    public long stringToTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        try {
            d1 = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d1.getTime();
    }
    public String getRemainTime(long time){
        long diffSeconds = (time / 1000)%60;
        long diffMinutes = (time / (60 * 1000))%60;
        long diffHours = (time / (60 * 60 * 1000))%60;
        String remainTime = String.valueOf(Math.abs(diffHours))+" Jam "+String.valueOf(Math.abs(diffMinutes))+" Menit";
        return remainTime;
    }
    public long[] calculateTime(String time1, String time2){
        // date format
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(time1);
            d2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long[] data;

        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = (diff / 1000)%60;
        long diffMinutes = (diff / (60 * 1000))%60;
        long diffHours = (diff / (60 * 60 * 1000))%60;
        data = new long[]{diff, diffSeconds, diffMinutes, diffHours};
        return data;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE TIMELINE"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(MainServices.ACTION_LOCATION_BROADCAST)
        );
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
                case "UPDATE TIME":
                    getCurrentSholat();
                    displayInfoTropy();
                    break;
                case "UPDATE PROFPICT":
                    initProfilePicture();
                    break;
                case "UPDATE LIST":
                    ((MainActivity)getActivity()).showProgress();
                    adapter.clear();
                    addDataAdapter();
                    adapter.notifyDataSetChanged();
                    ((MainActivity)getActivity()).stopProgress();
                    break;
                default:
                    getCurrentSholat();
                    displayInfoTropy();
                    break;
            }


        }
    };

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity().getApplicationContext(), String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();
        if(v.getId() == R.id.arcMenu){
            if(menu.isOpen()){
                dimView.setVisibility(View.VISIBLE);
            }else{
                dimView.setVisibility(View.GONE);
            }
        }
    }
    public void deleteTimeline(int id){
        final int idTemp;
        idTemp = id;
        String token = encryptedPreferences.getString(KEY, getString(R.string.dummyToken));
        String decryptToken = encryptedPreferences.getUtils().decryptStringValue(token);
        Log.d("DECRYPT TOKEN", "deleteTimeline: "+decryptToken);
        /*delete server*/
        RequestBody requestBody = new FormBody.Builder()
                .add("id_timeline", String.valueOf(id))
                .add("access_token", decryptToken)
                .build();
        Request request = client.newRequest().url(endUri).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Boolean result = jsonObject.getBoolean("return");
                    if (result){
//                        deleteRealm(Integer.valueOf(idTemp));
                        deleteRealm(idTemp);
                        /*delete realm*/
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
        /*delete server*/
    }
    public void deleteRealm(int id){
        Boolean resultDelete = tlHelper.deleteData(id);
        if (resultDelete){
            adapter.clear();
            addDataAdapter();
            adapter.notifyDataSetChanged();
        }
    }

}