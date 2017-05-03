package mhandharbeni.illiyin.gopraymurid.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import mhandharbeni.illiyin.gopraymurid.database.Timeline;

/**
 * Created by root on 26/04/17.
 */

public class TimelineHelper {
    private static final String TAG = "PresensiHelper";

    private Realm realm;
    private RealmResults<Timeline> realmResult;
    public Context context;
    public TimelineHelper(Context context) {
        this.context = context;
        Realm.init(context);
        try{
            realm = Realm.getDefaultInstance();

        }catch (Exception e){

            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);

        }
    }
    public void AddTimeline(Timeline tls){
        Timeline tl = new Timeline();

        tl.setId(tls.getId());
        tl.setId_user(tls.getId_user());
        tl.setId_aktivitas(tls.getId_aktivitas());
        tl.setId_ibadah(tls.getId_ibadah());
        tl.setNominal(tls.getNominal());
        tl.setImage(tls.getImage());
        tl.setNama_aktivitas(tls.getNama_aktivitas());
        tl.setNama_ibadah(tls.getNama_ibadah());
        tl.setTempat(tls.getTempat());
        tl.setBersama(tls.getBersama());
        tl.setPoint(tls.getPoint());
        tl.setDate(tls.getDate());
        tl.setJam(tls.getJam());
        tl.setStatus(tls.getStatus());

        realm.beginTransaction();
        realm.copyToRealm(tl);
        realm.commitTransaction();
    }
    public RealmResults<Timeline> getTimeline(){
        realmResult = realm.where(Timeline.class).findAllSorted("id", Sort.DESCENDING);
        return realmResult;
    }
    public RealmResults<Timeline> getNoneUploaded(){
        realmResult = realm.where(Timeline.class).equalTo("status", 0).findAll();
        return realmResult;
    }
    public boolean checkDuplicate(int id){
        realmResult = realm.where(Timeline.class)
                .equalTo("id", id)
                .findAll();
        if (realmResult.size() > 0){
            return true;
        }else{
            return false;
        }
    }
}
