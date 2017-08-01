package salam.gopray.id.database;

import io.realm.RealmObject;

/**
 * Created by root on 24/07/17.
 */

public class TransaksiStiker extends RealmObject {
    int id, id_stiker;
    String stiker;

    public TransaksiStiker() {
    }

    public TransaksiStiker(int id, int id_stiker, String stiker) {

        this.id = id;
        this.id_stiker = id_stiker;
        this.stiker = stiker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_stiker() {
        return id_stiker;
    }

    public void setId_stiker(int id_stiker) {
        this.id_stiker = id_stiker;
    }

    public String getStiker() {
        return stiker;
    }

    public void setStiker(String stiker) {
        this.stiker = stiker;
    }
}
