package mhandharbeni.illiyin.gopraymurid.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import mhandharbeni.illiyin.gopraymurid.database.JadwalSholat;

/**
 * Created by root on 02/05/17.
 */

public class JadwalSholatHelper {
    private static final String TAG = "JadwalSholatHelper";

    private Realm realm;
    private RealmResults<JadwalSholat> realmResult;
    public Context context;
    public JadwalSholatHelper(Context context) {
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
    public void AddJadwal(JadwalSholat js){
        JadwalSholat jds = new JadwalSholat();

        jds.setTanggal(js.getTanggal());
        jds.setSubuh(js.getSubuh());
        jds.setDhuha(js.getDhuha());
        jds.setDhuhur(js.getDhuhur());
        jds.setAshar(js.getAshar());
        jds.setMaghrib(js.getMaghrib());
        jds.setIsya(js.getIsya());

        realm.beginTransaction();
        realm.copyToRealm(jds);
        realm.commitTransaction();
    }
    public RealmResults<JadwalSholat> getJadwalSholat(String tanggal){
        realmResult = realm.where(JadwalSholat.class).equalTo("tanggal", tanggal).findAll();
        return realmResult;
    }
    public boolean checkDuplicate(String tanggal){
        realmResult = realm.where(JadwalSholat.class)
                .equalTo("tanggal", tanggal)
                .findAll();
        if (realmResult.size() > 0){
            return true;
        }else{
            return false;
        }
    }
}
