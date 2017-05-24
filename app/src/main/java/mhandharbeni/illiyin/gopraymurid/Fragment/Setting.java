package mhandharbeni.illiyin.gopraymurid.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;

import io.realm.Realm;
import mhandharbeni.illiyin.gopraymurid.MainActivity;
import mhandharbeni.illiyin.gopraymurid.R;
import mhandharbeni.illiyin.gopraymurid.service.MainServices;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by root on 19/04/17.
 */

public class Setting extends Fragment {
    View v;
    ListView listViewAbout, listViewSetting;
    Realm realm;
    EncryptedPreferences encryptedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        encryptedPreferences = new EncryptedPreferences.Builder(getActivity()).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        v = inflater.inflate(R.layout.fragment_setting, container, false);
        listViewAbout = (ListView) v.findViewById(R.id.listViewAbout);

        listViewAbout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :
                        break;
                    case 1 :
                        dialogAbout();
                        break;
                    case 2 :
                        dialogHelp();
                        break;
                    case 3 :
                        dialogLogout();
                        break;
                }
            }
        });
        return v;
    }
    public void dialogLogout(){
        new LovelyStandardDialog(getActivity())
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_logo)
                .setTitle("CONFIRM")
                .setMessage("Yakin Akan Keluar?")
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopServices();
                        deleteAllPreferences();
                        deleteAllRealmData();
                        checkSessionMainActivity();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    public void dialogAbout(){
        new LovelyInfoDialog(getActivity())
                .setTopColorRes(R.color.colorPrimary)
                .setTitle(R.string.app_name)
                .setMessage(R.string.about)
                .show();
    }
    public void dialogHelp(){
        new LovelyInfoDialog(getActivity())
                .setTopColorRes(R.color.colorPrimary)
                .setTitle(R.string.app_name)
                .setMessage(R.string.helpInfo)
                .show();
    }
    public void checkSessionMainActivity(){
        ((MainActivity)getActivity()).checkSession();
    }
    public void deleteAllRealmData(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }
    public void stopServices(){
        ((MainActivity)getActivity()).stopServices();
    }
    public void deleteAllPreferences(){
        encryptedPreferences.forceDeleteExistingPreferences();
    }
}
