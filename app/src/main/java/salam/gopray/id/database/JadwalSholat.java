package salam.gopray.id.database;

import io.realm.RealmObject;

/**
 * Created by root on 25/04/17.
 */

public class JadwalSholat extends RealmObject {
    public String tanggal;
    public String subuh;
    public String dhuha;
    public String dhuhur;
    public String ashar;
    public String maghrib;
    public String isya;


    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getSubuh() {
        return subuh;
    }

    public void setSubuh(String subuh) {
        this.subuh = subuh;
    }

    public String getDhuha() {
        return dhuha;
    }

    public void setDhuha(String dhuha) {
        this.dhuha = dhuha;
    }

    public String getDhuhur() {
        return dhuhur;
    }

    public void setDhuhur(String dhuhur) {
        this.dhuhur = dhuhur;
    }

    public String getAshar() {
        return ashar;
    }

    public void setAshar(String ashar) {
        this.ashar = ashar;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(String maghrib) {
        this.maghrib = maghrib;
    }

    public String getIsya() {
        return isya;
    }

    public void setIsya(String isya) {
        this.isya = isya;
    }
}
