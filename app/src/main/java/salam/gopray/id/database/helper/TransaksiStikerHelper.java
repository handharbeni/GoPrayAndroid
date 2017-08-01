package salam.gopray.id.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import salam.gopray.id.database.MasterStiker;
import salam.gopray.id.database.TransaksiStiker;

/**
 * Created by root on 24/07/17.
 */

public class TransaksiStikerHelper {
    private static final String TAG = "MasterStikerHelper";

    private Realm realm;
    private RealmResults<TransaksiStiker> realmResult;
    public Context context;

    public TransaksiStikerHelper(Context context) {
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

    public void addStiker(TransaksiStiker js) {
        TransaksiStiker jds = new TransaksiStiker();

        jds.setId(js.getId());
        jds.setId_stiker(js.getId_stiker());
        jds.setStiker(js.getStiker());

        realm.beginTransaction();
        realm.copyToRealm(jds);
        realm.commitTransaction();
    }
    public RealmResults<TransaksiStiker> getStiker() {
        realmResult = realm.where(TransaksiStiker.class).findAll();
        return realmResult;
    }
    public RealmResults<TransaksiStiker> getStiker(Integer id) {
        realmResult = realm.where(TransaksiStiker.class).equalTo("id_stiker", id).findAll();
        return realmResult;
    }

    public boolean checkDuplicate(Integer id) {
        realmResult = realm.where(TransaksiStiker.class)
                .equalTo("id", id)
                .findAll();
        if (realmResult.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
