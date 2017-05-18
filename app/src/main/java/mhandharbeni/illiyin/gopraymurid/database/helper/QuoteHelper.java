package mhandharbeni.illiyin.gopraymurid.database.helper;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import mhandharbeni.illiyin.gopraymurid.database.Quote;

/**
 * Created by root on 15/05/17.
 */

public class QuoteHelper {
    private static final String TAG = "JadwalSholatHelper";

    private Realm realm;
    private RealmResults<Quote> realmResult;
    public Context context;
    public QuoteHelper(Context context) {
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
    public void addQuote(Quote quote){
        Quote q = new Quote();

        q.setId(quote.getId());
        q.setPath_meme(quote.getPath_meme());
        q.setTanggal(quote.getTanggal());
        q.setJam(quote.getJam());

        realm.beginTransaction();
        realm.copyToRealm(q);
        realm.commitTransaction();
    }
    public RealmResults<Quote> getQuote(){
        realmResult = realm.where(Quote.class).findAllSorted("id", Sort.DESCENDING);
        return realmResult;
    }
    public boolean checkDuplicate(int id){
        realmResult = realm.where(Quote.class)
                .equalTo("id", id)
                .findAll();
        return realmResult.size() > 0;
    }
    public void closeRealm(){
        Realm.getDefaultInstance().close();
    }
}
