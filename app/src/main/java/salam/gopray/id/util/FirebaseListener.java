package salam.gopray.id.util;

/**
 * Created by root on 25/06/17.
 */

public interface FirebaseListener {
    void sendCrash(Exception e);
    void sendLog(String TAG, String message);
    void sendAnalytic(String message);

}
