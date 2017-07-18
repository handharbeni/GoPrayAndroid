package salam.gopray.id.util;

import android.view.View;

/**
 * Created by root on 22/06/17.
 */

public interface ShareListener {
    void shareWa(int id);
    void shareFb(int id);
    void delete(int id);
    void openContext(View v);
}
