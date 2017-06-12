package salam.gopray.id.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import salam.gopray.id.database.JadwalSholat;
import salam.gopray.id.database.MessageParent;
import salam.gopray.id.database.Timeline;

/**
 * Created by root on 12/06/17.
 */

public class MessageParentHelper  {
    private static final String TAG = "MessageParentHelper";

    private Realm realm;
    private RealmResults<MessageParent> realmResult;
    public Context context;
    public MessageParentHelper(Context context) {
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
    public void addMessage(MessageParent mp){
        MessageParent mpn = new MessageParent();

        mpn.setId(mp.getId());
        mpn.setDate(mp.getDate());
        mpn.setMessage(mp.getMessage());
        mpn.setPhoto(mp.getPhoto());
        mpn.setProfpict(mp.getProfpict());
        mpn.setStatus(1);
        mpn.setTime(mp.getTime());
        mpn.setType(mp.getType());

        realm.beginTransaction();
        realm.copyToRealm(mpn);
        realm.commitTransaction();
    }
    public void updateStatus(int id, int status){
        realm.beginTransaction();
        MessageParent mp = realm.where(MessageParent.class).equalTo("id", id).findFirst();
        mp.setStatus(status);
        realm.copyToRealmOrUpdate(mp);
        realm.commitTransaction();
    }
    public int countNewMessage(){
        RealmResults<MessageParent> mpResult = getMessageParent(1);
        return mpResult.size();
    }
    public RealmResults<MessageParent> getMessageParent(int status){
        realmResult = realm.where(MessageParent.class).equalTo("status", status).findAll();
        return realmResult;
    }
    public boolean checkDuplicate(int id){
        realmResult = realm.where(MessageParent.class)
                .equalTo("id", id)
                .findAll();
        if (realmResult.size() > 0){
            return true;
        }else{
            return false;
        }
    }
}
