package salam.gopray.id.Adapter.model;

/**
 * Created by Mindha on 08/06/2017.
 */

public class MessageModel {
    int id, type;
    String photo, message, date, time;

    /*1 : chat text; 2 : chat image*/

    public MessageModel(int id, int type, String photo, String message, String date, String time) {
        this.id = id;
        this.type = type;
        this.photo = photo;
        this.message = message;
        this.date = date;
        this.time = time;
    }

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
}
