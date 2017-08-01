package salam.gopray.id.Adapter.model;

/**
 * Created by root on 26/07/17.
 */

public class StikerModel {
    int id;
    String stiker;

    public StikerModel(int id, String stiker) {
        this.id = id;
        this.stiker = stiker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStiker() {
        return stiker;
    }

    public void setStiker(String stiker) {
        this.stiker = stiker;
    }
}
