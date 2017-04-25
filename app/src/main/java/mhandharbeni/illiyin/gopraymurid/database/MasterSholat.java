package mhandharbeni.illiyin.gopraymurid.database;

import io.realm.RealmObject;

/**
 * Created by root on 25/04/17.
 */

public class MasterSholat extends RealmObject {
    public int id;
    public String nama;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
