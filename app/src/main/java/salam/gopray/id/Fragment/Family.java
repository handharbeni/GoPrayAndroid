package salam.gopray.id.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import salam.gopray.id.Adapter.MessageAdapter;
import salam.gopray.id.Adapter.QuoteAdapter;
import salam.gopray.id.Adapter.model.MessageModel;
import salam.gopray.id.Adapter.model.QuoteModel;
import salam.gopray.id.R;

/**
 * Created by root on 19/04/17.
 */

public class Family extends Fragment {
    View v;
    private static MessageAdapter adapter;
    ArrayList<MessageModel> dataModels;
    ListView listViewMessage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_family, container, false);
        listViewMessage = (ListView) v.findViewById(R.id.listMessage);
        dummyDataMessage();
        initAdapter();
        return v;
    }
    public void dummyDataParent(){
        for (int i=0;i<5;i++){
            /*disini mindaa*/
        }
    }

    public void dummyDataMessage(){
        dataModels = new ArrayList<>();
        for (int i=0;i<10;i++){
            /*disini mindaa*/
            dataModels.add(new MessageModel(i, 1, "https://www.w3schools.com/css/trolltunga.jpg", "Hello "+i, "2017-02-02", "02:02:02"));
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
}
