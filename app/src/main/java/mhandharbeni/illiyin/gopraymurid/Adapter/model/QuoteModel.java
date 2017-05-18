package mhandharbeni.illiyin.gopraymurid.Adapter.model;

/**
 * Created by root on 15/05/17.
 */

public class QuoteModel {
    int id;
    String path_meme, tanggal, jam;
    String tl;


    public QuoteModel(int id, String path_meme, String tanggal, String jam, String tl) {
        this.id = id;
        this.path_meme = path_meme;
        this.tanggal = tanggal;
        this.jam = jam;
        this.tl = tl;
    }
    public String getTl() {
        return tl;
    }

    public void setTl(String tl) {
        this.tl = tl;
    }

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
