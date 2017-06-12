package salam.gopray.id.Adapter.model;

/**
 * Created by root on 09/06/17.
 */

public class ParentModel {
    String nama, photo, grade;

    public ParentModel(String nama, String photo, String grade) {
        this.nama = nama;
        this.photo = photo;
        this.grade = grade;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
