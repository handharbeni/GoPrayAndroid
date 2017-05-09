package mhandharbeni.illiyin.gopraymurid.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import mhandharbeni.illiyin.gopraymurid.database.Timeline;

/**
 * Created by root on 09/05/17.
 */

public class UploadHelper {
    private static final String TAG = "PresensiHelper";

    private Realm realm;
    private RealmResults<Timeline> realmResult;
    public Context context;
    public UploadHelper(Context context) {
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
    public RealmResults<Timeline> getTimeline(){
        realmResult = realm.where(Timeline.class).findAll();
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
    public void updateStatus(int id, int status){
        Timeline tl = realm.where(Timeline.class).equalTo("id", id).findFirst();
        tl.setStatus(status);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tl);
        realm.commitTransaction();
    }
}
