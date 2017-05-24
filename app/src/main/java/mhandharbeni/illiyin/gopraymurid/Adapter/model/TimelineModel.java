package mhandharbeni.illiyin.gopraymurid.Adapter.model;

/**
 * Created by root on 26/04/17.
 */

public class TimelineModel {
    /*type 1 sholat, 2 mengaji, 3 sedekah, 4 berdoa, 5 freetext*/
    int id;
    int type;
    int icon;
    String stiker;
    String di;
    String bersama;
    String keterangan;
    String tanggal;
    String tl;
    String nominal;

    public TimelineModel(int id, int type,  int icon, String stiker,
                         String keterangan, String bersama, String di, String tanggal, String lineTime, String nominal){
        this.id = id;
        this.type = type;
        this.icon = icon;
        this.stiker = stiker;
        this.keterangan = keterangan;
        this.bersama = bersama;
        this.di = di;
        this.tanggal = tanggal;
        this.tl = lineTime;
        this.nominal = nominal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getTl() {
        return tl;
    }

    public void setTl(String tl) {
        this.tl = tl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStiker() {
        return stiker;
    }

    public void setStiker(String stiker) {
        this.stiker = stiker;
    }

    public String getDi() {
        return di;
    }

    public void setDi(String di) {
        this.di = di;
    }

    public String getBersama() {
        return bersama;
    }

    public void setBersama(String bersama) {
        this.bersama = bersama;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
