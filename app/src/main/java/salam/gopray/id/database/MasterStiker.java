package salam.gopray.id.database;

import io.realm.RealmObject;

/**
 * Created by root on 24/07/17.
 */

public class MasterStiker extends RealmObject {
    int id;
    String nama, cover;

    public MasterStiker(int id, String nama, String cover) {
        this.id = id;
        this.nama = nama;
        this.cover = cover;
    }

    public MasterStiker() {
    }

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
