package mhandharbeni.illiyin.gopraymurid.database;

import io.realm.RealmObject;

/**
 * Created by root on 25/04/17.
 */

public class Timeline extends RealmObject {
    public int id;
    public int id_user;
    public int id_aktivitas;
    public int id_ibadah;
    public String tempat;
    public String bersama;
    public int point;
    public String date;
    public String jam;
    public int status;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_aktivitas() {
        return id_aktivitas;
    }

    public void setId_aktivitas(int id_aktivitas) {
        this.id_aktivitas = id_aktivitas;
    }

    public int getId_ibadah() {
        return id_ibadah;
    }

    public void setId_ibadah(int id_ibadah) {
        this.id_ibadah = id_ibadah;
    }

    public String getTempat() {
        return tempat;
    }

    public void setTempat(String tempat) {
        this.tempat = tempat;
    }

    public String getBersama() {
        return bersama;
    }

    public void setBersama(String bersama) {
        this.bersama = bersama;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }
}
