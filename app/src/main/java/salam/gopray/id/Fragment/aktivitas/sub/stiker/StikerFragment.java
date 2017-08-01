package salam.gopray.id.Fragment.aktivitas.sub.stiker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import salam.gopray.id.Adapter.StikerAdapter;
import salam.gopray.id.Adapter.model.StikerModel;
import salam.gopray.id.R;
import salam.gopray.id.database.TransaksiStiker;
import salam.gopray.id.database.helper.TransaksiStikerHelper;
import salam.gopray.id.util.FreeTextInterface;

/**
 * Created by root on 26/07/17.
 */

@SuppressLint("ValidFragment")
public class StikerFragment extends Fragment implements FreeTextInterface {
    View v;
    ArrayList<StikerModel> listStiker;

    Integer id;

    TransaksiStikerHelper transaksiStikerHelper;

    RecyclerView rvStiker;

    FreeTextInterface freeTextInterface;

    public StikerFragment(FreeTextInterface freeTextInterface) {
        this.freeTextInterface = freeTextInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
        }
        transaksiStikerHelper = new TransaksiStikerHelper(getActivity().getApplicationContext());

        v = inflater.inflate(R.layout.container_stiker, container, false);

        rvStiker = (RecyclerView) v.findViewById(R.id.rvStiker);

        initData();
        initContent();
        return v;
    }

    public void initData(){
        listStiker = new ArrayList<>();
        RealmResults<TransaksiStiker> result = transaksiStikerHelper.getStiker(id);
        if (result.size() > 0){
            for (int i=0;i<result.size();i++){
                TransaksiStiker transaksiStiker = result.get(i);
                listStiker.add(new StikerModel(transaksiStiker.getId(), transaksiStiker.getStiker()));
            }
        }
    }
    public void initContent(){
        rvStiker.setHasFixedSize(true);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        rvStiker.setLayoutManager(gaggeredGridLayoutManager);

        StikerAdapter rcAdapter = new StikerAdapter(getActivity().getApplicationContext(), listStiker, this);
        rvStiker.setAdapter(rcAdapter);
    }

    @Override
    public void changeStiker() {
        freeTextInterface.changeStiker();
    }
}
