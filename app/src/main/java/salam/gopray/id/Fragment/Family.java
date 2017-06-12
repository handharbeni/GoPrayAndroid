package salam.gopray.id.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.dant.centersnapreyclerview.SnappingRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import salam.gopray.id.Adapter.MessageAdapter;
import salam.gopray.id.Adapter.ParentAdapter;
import salam.gopray.id.Adapter.QuoteAdapter;
import salam.gopray.id.Adapter.model.HorizontalParentAdapter;
import salam.gopray.id.Adapter.model.MessageModel;
import salam.gopray.id.Adapter.model.ParentModel;
import salam.gopray.id.Adapter.model.QuoteModel;
import salam.gopray.id.MainActivity;
import salam.gopray.id.R;
import salam.gopray.id.database.MessageParent;
import salam.gopray.id.database.helper.MessageParentHelper;
import salam.gopray.id.service.MainServices;

/**
 * Created by root on 19/04/17.
 */

public class Family extends Fragment {
    View v;
    private static MessageAdapter adapter;
    private static ParentAdapter parentAdapter;
    ArrayList<MessageModel> dataModels;

    HorizontalParentAdapter horizontalParentAdapter;
    private List<ParentModel> parentModels;
    ListView listViewMessage;
    SnappingRecyclerView snapRC;

    MessageParentHelper mpHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mpHelper = new MessageParentHelper(getActivity().getApplicationContext());

        v = inflater.inflate(R.layout.fragment_family, container, false);
        listViewMessage = (ListView) v.findViewById(R.id.listMessage);
        snapRC = (SnappingRecyclerView) v.findViewById(R.id.snapRC);

        dataModels = new ArrayList<>();
        parentModels = new ArrayList<>();

        dummyDataMessage();
        dummyDataParent();
        initAdapter();

        dummyParent();
        initAdapterParent();
        return v;
    }
    public void updateDataToRead(){
        RealmResults<MessageParent> mpResults = mpHelper.getMessageParent(1);
        for (int i=0;i<mpResults.size();i++){
            /*update status*/
            mpHelper.updateStatus(i, 2);
        }
    }
    public void dummyParent(){
        for (int i = 0; i < 4 ; i++){
            parentModels.add(new ParentModel("Parent "+i, "https://organicthemes.com/demo/profile/files/2012/12/profile_img.png", "Orang Tua"+i));
        }
    }
    public void dummyDataParent(){
        for (int i=0;i<5;i++){
            dataModels.add(new MessageModel(i, 2, "https://www.w3schools.com/css/trolltunga.jpg", "Hello "+i, "2017-02-02", "02:02:02", "https://organicthemes.com/demo/profile/files/2012/12/profile_img.png"));
        }
    }

    public void dummyDataMessage(){

        for (int i=0;i<10;i++){
            dataModels.add(new MessageModel(i, 1, "https://www.w3schools.com/css/trolltunga.jpg", "Hello "+i, "2017-02-02", "02:02:02", "https://organicthemes.com/demo/profile/files/2012/12/profile_img.png"));
        }
    }

    public void initAdapterParent(){
        horizontalParentAdapter = new HorizontalParentAdapter(parentModels, getActivity().getApplicationContext());
        snapRC.setAdapter(horizontalParentAdapter);
        snapRC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void updateAdapterMessage(){

    }
    public void updateAdapterParent(){

    }
    public void initAdapter(){
        adapter = new MessageAdapter(dataModels,getActivity().getApplicationContext());
        listViewMessage.setAdapter(adapter);
        listViewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageModel dataModel= dataModels.get(position);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE MESSAGE"));
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
                case "UPDATE LIST":
//                    adapter.clear();
//                    updateAdapterMessage();
//                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }


        }
    };
}
