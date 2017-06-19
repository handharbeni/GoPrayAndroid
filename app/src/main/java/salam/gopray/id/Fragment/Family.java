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
import android.widget.ListView;

import com.dant.centersnapreyclerview.SnappingRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import salam.gopray.id.Adapter.MessageAdapter;
import salam.gopray.id.Adapter.ParentAdapter;
import salam.gopray.id.Adapter.model.HorizontalParentAdapter;
import salam.gopray.id.Adapter.model.MessageModel;
import salam.gopray.id.Adapter.model.ParentModel;
import salam.gopray.id.MainActivity;
import salam.gopray.id.R;
import salam.gopray.id.database.MasterKerabat;
import salam.gopray.id.database.MessageParent;
import salam.gopray.id.database.helper.MasterKerabatHelper;
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
    MasterKerabatHelper mkHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mpHelper = new MessageParentHelper(getActivity().getApplicationContext());
        mkHelper = new MasterKerabatHelper(getActivity().getApplicationContext());
        v = inflater.inflate(R.layout.fragment_family, container, false);
        listViewMessage = (ListView) v.findViewById(R.id.listMessage);
        snapRC = (SnappingRecyclerView) v.findViewById(R.id.snapRC);

        dataModels = new ArrayList<>();
        parentModels = new ArrayList<>();

        initMessage();
        initAdapter();

        initKerabat();
        initAdapterParent();
        return v;
    }
    public void updateDataToRead(){
        RealmResults<MessageParent> mpResults = mpHelper.getMessageParent(1);
        for (int i=0;i<mpResults.size();i++){
            /*update status*/
            mpHelper.updateStatus(mpResults.get(i).getId(), 2);
        }
    }
    public void initKerabat(){
        RealmResults<MasterKerabat> resultMasterKerabat = mkHelper.getKerabat();
        if (resultMasterKerabat.size() > 0){
            for (int i=0;i<resultMasterKerabat.size();i++){
                parentModels.add(new ParentModel(resultMasterKerabat.get(i).getNama(),
                        resultMasterKerabat.get(i).getGambar(),
                        resultMasterKerabat.get(i).getKerabat()));
            }
            parentModels.add(new ParentModel("Tambah",
                    "https://cdn0.iconfinder.com/data/icons/math-business-icon-set/93/1_1-512.png",
                    "Kerabat"));
        }else{
            parentModels = new ArrayList<>();
            parentModels.add(new ParentModel("Tambah",
                    "https://cdn0.iconfinder.com/data/icons/math-business-icon-set/93/1_1-512.png",
                    "Kerabat"));
        }
    }
    public void initMessage(){
        RealmResults<MessageParent> resultMessageParent = mpHelper.getMessageParent();
        if (resultMessageParent.size() > 0){
            for (int i=0;i<resultMessageParent.size();i++){
                dataModels.add(new MessageModel(
                        resultMessageParent.get(i).getId(),
                        resultMessageParent.get(i).getType(),
                        resultMessageParent.get(i).getPhoto(),
                        resultMessageParent.get(i).getMessage(),
                        resultMessageParent.get(i).getDate(),
                        resultMessageParent.get(i).getTime(),
                        resultMessageParent.get(i).getProfpict()
                ));
            }
        }
    }
    public void initAdapterParent(){
        horizontalParentAdapter = new HorizontalParentAdapter(parentModels, getActivity().getApplicationContext());
        snapRC.setAdapter(horizontalParentAdapter);
    }
    public void updateAdapterMessage(){
        RealmResults<MessageParent> resultMessageParent = mpHelper.getMessageParent();
        if (resultMessageParent.size() > 0){
            for (int i=0;i<resultMessageParent.size();i++){
                adapter.add(new MessageModel(
                        resultMessageParent.get(i).getId(),
                        resultMessageParent.get(i).getType(),
                        resultMessageParent.get(i).getPhoto(),
                        resultMessageParent.get(i).getMessage(),
                        resultMessageParent.get(i).getDate(),
                        resultMessageParent.get(i).getTime(),
                        resultMessageParent.get(i).getProfpict()
                ));
            }
        }
    }
    public void updateAdapterParent(){
        parentModels = new ArrayList<>();
        parentModels.clear();
        RealmResults<MasterKerabat> resultMasterKerabat = mkHelper.getKerabat();
        if (resultMasterKerabat.size() > 0){
            for (int i=0;i<resultMasterKerabat.size();i++){
                parentModels.add(new ParentModel(resultMasterKerabat.get(i).getNama(),
                        resultMasterKerabat.get(i).getGambar(),
                        resultMasterKerabat.get(i).getKerabat()));
            }
            parentModels.add(new ParentModel("Tambah",
                    "https://cdn0.iconfinder.com/data/icons/math-business-icon-set/93/1_1-512.png",
                    "Kerabat"));
        }else{
            parentModels.add(new ParentModel("Tambah",
                    "https://cdn0.iconfinder.com/data/icons/math-business-icon-set/93/1_1-512.png",
                    "Kerabat"));
        }
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
        ((MainActivity)getActivity()).sendScreen(this.getClass().getName());
        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE MESSAGE"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(MainServices.ACTION_LOCATION_BROADCAST)
        );
        updateDataToRead();
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.receiver, new IntentFilter("UPDATE MESSAGE"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
                new IntentFilter(MainServices.ACTION_LOCATION_BROADCAST)
        );
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String mode = bundle.getString("MODE");
            switch (mode){
                case "UPDATE LIST":
                    adapter.clear();
                    updateAdapterMessage();
                    adapter.notifyDataSetChanged();
                    break;
                case "UPDATE PARENT":

                    updateAdapterParent();
                    snapRC.invalidate();
                    horizontalParentAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
}
