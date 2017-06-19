package salam.gopray.id.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import salam.gopray.id.database.MasterKerabat;

/**
 * Created by root on 13/06/17.
 */

public class MasterKerabatHelper {
    private static final String TAG = "MessageParentHelper";

    private Realm realm;
    private RealmResults<MasterKerabat> realmResult;
    public Context context;
    public MasterKerabatHelper(Context context) {
        this.context = context;
        Realm.init(context);
        try{
            realm = Realm.getDefaultInstance();
        }catch (Exception e){
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);

        }
    }
    public void addKerabat(MasterKerabat mp){
        MasterKerabat mpn = new MasterKerabat();

        mpn.setId(mp.getId());
        mpn.setKerabat(mp.getKerabat());
        mpn.setEmail(mp.getEmail());
        mpn.setGambar(mp.getGambar());
        mpn.setNama(mp.getNama());
        mpn.setNohp(mp.getNohp());

        realm.beginTransaction();
        realm.copyToRealm(mpn);
        realm.commitTransaction();
    }
    public void updateStatus(int id, int status){
        realm.beginTransaction();
        MasterKerabat mp = realm.where(MasterKerabat.class).equalTo("id", id).findFirst();
//        mp.setStatus(status);
        realm.copyToRealmOrUpdate(mp);
        realm.commitTransaction();
    }
    public RealmResults<MasterKerabat> getKerabat(){
        realmResult = realm.where(MasterKerabat.class).findAll();
        return realmResult;
    }
    public boolean checkDuplicate(int id){
        realmResult = realm.where(MasterKerabat.class)
                .equalTo("id", id)
                .findAll();
        if (realmResult.size() > 0){
            return true;
        }else{
            return false;
        }
    }
}