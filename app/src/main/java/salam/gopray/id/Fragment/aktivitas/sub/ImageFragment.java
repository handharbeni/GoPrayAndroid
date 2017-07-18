package salam.gopray.id.Fragment.aktivitas.sub;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import salam.gopray.id.R;
import salam.gopray.id.util.KeyboardHeightObserver;
import salam.gopray.id.util.KeyboardHeightProvider;

/**
 * Created by root on 19/06/17.
 */

public class ImageFragment extends Fragment implements KeyboardHeightObserver {
    View v;

    private final static String TAG = "ImageFreeFragment";
    private KeyboardHeightProvider keyboardHeightProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.image_fragment_freetext, container, false);

        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());

        View view = v.findViewById(R.id.imageFreeTextLayout);
        view.post(new Runnable() {
            public void run() {
                keyboardHeightProvider.start();
            }
        });
        return v;
    }
    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }
    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }
    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {

        String or = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        Log.i(TAG, "onKeyboardHeightChanged in pixels: " + (height * 0.3) + " " + or);

    }
}
