package mhandharbeni.illiyin.gopraymurid.database;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by root on 25/04/17.
 */

public class JadwalSholat extends RealmObject {
    public Date tanggal;
    public Date subuh;
    public Date dhuha;
    public Date dhuhur;
    public Date ashar;
    public Date maghrib;
    public Date isya;


    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Date getSubuh() {
        return subuh;
    }

    public void setSubuh(Date subuh) {
        this.subuh = subuh;
    }

    public Date getDhuha() {
        return dhuha;
    }

    public void setDhuha(Date dhuha) {
        this.dhuha = dhuha;
    }

    public Date getDhuhur() {
        return dhuhur;
    }

    public void setDhuhur(Date dhuhur) {
        this.dhuhur = dhuhur;
    }

    public Date getAshar() {
        return ashar;
    }

    public void setAshar(Date ashar) {
        this.ashar = ashar;
    }

    public Date getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(Date maghrib) {
        this.maghrib = maghrib;
    }

    public Date getIsya() {
        return isya;
    }

    public void setIsya(Date isya) {
        this.isya = isya;
    }
}
