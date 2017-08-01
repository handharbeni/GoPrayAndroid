package salam.gopray.id.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import salam.gopray.id.database.JadwalSholat;
import salam.gopray.id.database.MasterStiker;
import salam.gopray.id.database.Timeline;

/**
 * Created by root on 24/07/17.
 */

public class MasterStikerHelper {
    private static final String TAG = "MasterStikerHelper";

    private Realm realm;
    private RealmResults<MasterStiker> realmResult;
    public Context context;

    public MasterStikerHelper(Context context) {
        this.context = context;
        Realm.init(context);
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);

        }
    }

    public void addStiker(MasterStiker js) {
        MasterStiker jds = new MasterStiker();

        jds.setId(js.getId());
        jds.setNama(js.getNama());
        jds.setCover(js.getCover());

        realm.beginTransaction();
        realm.copyToRealm(jds);
        realm.commitTransaction();
    }
    public RealmResults<MasterStiker> getStiker() {
        realmResult = realm.where(MasterStiker.class).findAll();
        return realmResult;
    }
    public RealmResults<MasterStiker> getStiker(Integer id) {
        realmResult = realm.where(MasterStiker.class).equalTo("id", id).findAll();
        return realmResult;
    }

    public boolean checkDuplicate(Integer id) {
        realmResult = realm.where(MasterStiker.class)
                .equalTo("id", id)
                .findAll();
        if (realmResult.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    public boolean deleteAll(){
        realm.beginTransaction();
        RealmResults realmResults = realm.where(MasterStiker.class)
                .findAll();
        boolean deleted = realmResults.deleteAllFromRealm();
        realm.commitTransaction();
        return deleted;
    }
}
