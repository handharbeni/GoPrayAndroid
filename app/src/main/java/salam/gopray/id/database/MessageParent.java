package salam.gopray.id.database;

import io.realm.RealmObject;

/**
 * Created by root on 12/06/17.
 */

public class MessageParent extends RealmObject {
    int id, type, status;
    String photo, message, date, time, profpict;
    /*type*/
    /*1 : chat text; 2 : chat image*/

    /*status*/
    /*1 : baru; 2 : sudah dibaca*/


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfpict() {
        return profpict;
    }

    public void setProfpict(String profpict) {
        this.profpict = profpict;
    }
}
