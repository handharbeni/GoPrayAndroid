package mhandharbeni.illiyin.gopraymurid.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bvapp.arcmenulibrary.ArcMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmResults;
import mhandharbeni.illiyin.gopraymurid.Adapter.TimelineAdapter;
import mhandharbeni.illiyin.gopraymurid.Adapter.model.TimelineModel;
import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.database.helper.TimelineHelper;

/**
 * Created by root on 19/04/17.
 */

public class Timeline extends Fragment {
    private static final int[] ITEM_DRAWABLES = { R.drawable.timeline_sholat,
            R.drawable.timeline_mengai, R.drawable.timeline_sedekah, R.drawable.timeline_puasa, R.drawable.timeline_meme};

    View v;
    ArrayList<TimelineModel> dataModels;
    ListView listView;
    private static TimelineAdapter adapter;
    RelativeLayout dimView;

    TimelineHelper tlHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_timeline, container, false);
        tlHelper = new TimelineHelper(getActivity().getApplicationContext());
        listView=(ListView) v.findViewById(R.id.listViewTimeline);
        dimView = (RelativeLayout) v.findViewById(R.id.dimScreen);
//        insertDummy();
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
    public void insertDummy(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        String currentDate = sdf.format(new Date());
        for (int i = 0;i < 10; i++){
            SimpleDateFormat ghj = new SimpleDateFormat("HH:mm:ss");
            String currentTime = ghj.format(new Date());

            mhandharbeni.illiyin.gopraymurid.database.Timeline
                    tl =
                        new
                            mhandharbeni.illiyin.gopraymurid.database.Timeline();
            tl.setId(i);
            tl.setId_user(i);
            tl.setId_aktivitas(i);
            tl.setId_ibadah(i);
            tl.setTempat("Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid Masjid "+i);
            tl.setBersama("Dia "+i);
            tl.setPoint(10*i);
            tl.setDate(currentDate);
            tl.setJam(currentTime);

            tlHelper.AddTimeline(tl);
        }
    }
    public void initData(){
        dataModels= new ArrayList<>();
        RealmResults<mhandharbeni.illiyin.gopraymurid.database.Timeline>
                result = tlHelper.getTimeline();
        for (int i=0; i<result.size(); i++){
//            dataModels.add(new TimelineModel(result.get(i).getTempat(), result.get(i).getDate(), R.drawable.timeline_puasa));
            dataModels.add(new TimelineModel(2, R.drawable.timeline_sholat, "",
                    "Dhuhur", result.get(i).getBersama(), result.get(i).getTempat(),result.get(i).getDate()));
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
}
