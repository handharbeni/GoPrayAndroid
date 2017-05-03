package mhandharbeni.illiyin.gopraymurid.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bvapp.arcmenulibrary.ArcMenu;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmResults;
import mhandharbeni.illiyin.gopraymurid.Adapter.TimelineAdapter;
import mhandharbeni.illiyin.gopraymurid.Adapter.model.TimelineModel;
import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.database.JadwalSholat;
import mhandharbeni.illiyin.gopraymurid.database.helper.JadwalSholatHelper;
import mhandharbeni.illiyin.gopraymurid.database.helper.TimelineHelper;
import sexy.code.Callback;
import sexy.code.HttPizza;
import sexy.code.Request;
import sexy.code.Response;

/**
 * Created by root on 19/04/17.
 */

public class Timeline extends Fragment {
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    private static final int[] ITEM_DRAWABLES = { R.drawable.timeline_sholat,
            R.drawable.timeline_mengai, R.drawable.timeline_sedekah, R.drawable.timeline_puasa, R.drawable.timeline_meme};

    private String endUri;
    View v;
    ArrayList<TimelineModel> dataModels;
    ListView listView;
    private static TimelineAdapter adapter;
    RelativeLayout dimView;

    TimelineHelper tlHelper;
    JadwalSholatHelper jsHelper;

    HttPizza client;

    EncryptedPreferences encryptedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //init encrypting
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        //init encrypting

        v = inflater.inflate(R.layout.fragment_timeline, container, false);
        tlHelper = new TimelineHelper(getActivity().getApplicationContext());
        jsHelper = new JadwalSholatHelper(getActivity().getApplicationContext());
        listView=(ListView) v.findViewById(R.id.listViewTimeline);
        dimView = (RelativeLayout) v.findViewById(R.id.dimScreen);
        client = new HttPizza();
        endUri = getString(R.string.server)+"/"+getString(R.string.vServer)+"/users/self/timeline?access_token=";
//        syncData();
//        insertDummy();
        displayInfo();
        getJadwalSholat();
        getCurrentSholat();
        initArc();
        initData();
        initAdapter();
        return v;
    }
    public void initArc(){
        final ArcMenu menu = (ArcMenu) v.findViewById(R.id.arcMenu);
        menu.setMenuGravity(ArcMenu.BOTTOM_MIDDLE);
        menu.attachToListView(listView);
        menu.showTooltip(false);
        menu.setAnim(300,300,ArcMenu.ANIM_BOTTOM_TO_DOWN,ArcMenu.ANIM_BOTTOM_TO_DOWN,
                ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE,ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE);
        final int itemCount = ITEM_DRAWABLES.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(getActivity().getApplicationContext());
            Glide.with(getActivity().getApplicationContext()).load(ITEM_DRAWABLES[i]).into(item);
            final int position = i;
            menu.addItem(item, "", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //You can access child click in here

                }
            });
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), String.valueOf(menu.isOpen()), Toast.LENGTH_SHORT).show();
                if (menu.isOpen()){
                    dimView.setVisibility(View.VISIBLE);
                }else if(menu.isClose()){
                    dimView.setVisibility(View.GONE);
                }
            }
        });
    }
    public void syncData(){
        String token = encryptedPreferences.getUtils().decryptStringValue(encryptedPreferences.getString(KEY, "0"));
        endUri += token;
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
                                int nominal = Integer.valueOf(objectData.getString("nominal"));
                                int poit = Integer.valueOf(objectData.getString("point"));
                                int status = 3; /*1 not uploaded, 2 uploaded, 3 sync server*/
                                String tanggal = objectData.getString("tanggal");
                                String jam = objectData.getString("jam");

                                boolean duplicate = tlHelper.checkDuplicate(id);
                                if(!duplicate){
                                    mhandharbeni.illiyin.gopraymurid.database.Timeline tl = new mhandharbeni.illiyin.gopraymurid.database.Timeline();
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
                                    tlHelper.AddTimeline(tl);
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
    public void initData(){
        dataModels= new ArrayList<>();
        RealmResults<mhandharbeni.illiyin.gopraymurid.database.Timeline>
                result = tlHelper.getTimeline();
        int z = 0;
        for (int i=0; i<result.size(); i++){
            String tl;
            if (z == (result.size()-1)) {
                tl = "start";
            }else{
                tl = "content";
            }
            z++;
            int dr = R.drawable.timeline_berdoa;
            switch (result.get(i).getId_aktivitas()){
                case 1:
                    dr = R.drawable.timeline_berdoa;
                    break;
                case 2:
                    dr = R.drawable.timeline_puasa;
                    break;
                case 3:
                    dr = R.drawable.timeline_sholat;
                    break;
                case 4:
                    dr = R.drawable.timeline_sedekah;
                    break;
                case 5:
                    dr = R.drawable.timeline_meme;
                    break;
                case 6:
                    dr = R.drawable.timeline_meme;
                    break;
            }
            dataModels.add(new TimelineModel(result.get(i).getId_aktivitas(), dr, result.get(i).getImage(),
                    result.get(i).getNama_ibadah(), result.get(i).getBersama(), result.get(i).getTempat(),
                    result.get(i).getDate(), tl));
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
    }
    public void displayInfo(){
        TextView txtPoint = (TextView) v.findViewById(R.id.txtPoint);
        RealmResults<mhandharbeni.illiyin.gopraymurid.database.Timeline>
                result = tlHelper.getTimeline();
        int point = 0;
        for (int i=0;i<result.size();i++){
            point += result.get(i).getPoint();
        }
        txtPoint.setText(String.valueOf(point));
    }
    public void getJadwalSholat(){
//        String tanggal = (getDateTime());
        String tanggal = "2017-04-11";
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
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
    public void getCurrentSholat(){
        String tanggal = "2017-04-11";
        String jam = getTime();
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

            long[] subuh = calculateTime(jam, dSubuh);
            long[] dhuha = calculateTime(jam, dDhuha);
            long[] dhuhur = calculateTime(jam, dDhuhur);
            long[] ashar = calculateTime(jam, dAshar);
            long[] maghrib = calculateTime(jam, dMaghrib);
            long[] isya = calculateTime(jam, dIsya);

            Log.d("JADWAL SHOLAT", "getCurrentSholat: "+subuh[3]+"-"+subuh[2]+"-"+subuh[1]+"-"+subuh[0]);
            Log.d("JADWAL SHOLAT", "getCurrentSholat: "+dhuha[3]+"-"+dhuha[2]+"-"+dhuha[1]+"-"+dhuha[0]);
            Log.d("JADWAL SHOLAT", "getCurrentSholat: "+dhuhur[3]+"-"+dhuhur[2]+"-"+dhuhur[1]+"-"+dhuhur[0]);
            Log.d("JADWAL SHOLAT", "getCurrentSholat: "+ashar[3]+"-"+ashar[2]+"-"+ashar[1]+"-"+ashar[0]);
            Log.d("JADWAL SHOLAT", "getCurrentSholat: "+maghrib[3]+"-"+maghrib[2]+"-"+maghrib[1]+"-"+maghrib[0]);
            Log.d("JADWAL SHOLAT", "getCurrentSholat: "+isya[3]+"-"+isya[2]+"-"+isya[1]+"-"+isya[0]);

            txtNamaSholat.setText(dSubuh);
        }
    }
    public void checkLewatWaktu(){

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
}