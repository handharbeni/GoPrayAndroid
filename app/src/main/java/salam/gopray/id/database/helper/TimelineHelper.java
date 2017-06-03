package salam.gopray.id.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import salam.gopray.id.database.Timeline;

/**
 * Created by root on 26/04/17.
 */

public class TimelineHelper {
    private static final String TAG = "Timeline Helper";

    private Realm realm;
    private RealmResults<Timeline> realmResult;
    public Context context;
    public TimelineHelper(Context context) {
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
        closeRealm();
    }
    public void AddTimelineOffline(Timeline tls){
        Timeline tl = new Timeline();
        tl.setId(tls.getId());
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
        closeRealm();
    }
    public RealmResults<Timeline> getTimeline(int type){
        if (type == 1){
            /* id */
            realmResult = realm.where(Timeline.class).equalTo("status", 3).findAllSorted("id", Sort.DESCENDING);
        }else if(type == 2){
            /* status */
            realmResult = realm.where(Timeline.class).equalTo("status", 1).findAll();
        }
        closeRealm();
        return realmResult;
    }
    public void checkContext(){
        realm.where(Timeline.class).equalTo("id",1).findAll();
    }
    public boolean checkDuplicate(int id){
        realmResult = realm.where(Timeline.class)
                .equalTo("id", id)
                .findAll();
        closeRealm();
        if (realmResult.size() > 0){
            return true;
        }else{
            return false;
        }
    }
    public void updateStatus(int id, int status){
        realm.beginTransaction();
        Timeline tl = realm.where(Timeline.class).equalTo("id", id).findFirst();
        tl.setStatus(status);
        realm.copyToRealmOrUpdate(tl);
        realm.commitTransaction();
        closeRealm();
    }
    public boolean deleteData(int id){
        realm.beginTransaction();
        RealmResults realmResults = realm.where(Timeline.class)
                .equalTo("id", id)
                .findAll();
        boolean deleted = realmResults.deleteFirstFromRealm();
        realm.commitTransaction();
        return deleted;
    }
    public void closeRealm(){
//        realm.close();
    }
}
