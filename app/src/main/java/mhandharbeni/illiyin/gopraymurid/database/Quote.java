package mhandharbeni.illiyin.gopraymurid.database;

import io.realm.RealmObject;

/**
 * Created by root on 15/05/17.
 */

public class Quote extends RealmObject {
    int id;
    String path_meme, tanggal, jam;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath_meme() {
        return path_meme;
    }

    public void setPath_meme(String path_meme) {
        this.path_meme = path_meme;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }
}
