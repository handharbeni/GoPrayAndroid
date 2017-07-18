package salam.gopray.id.Fragment.aktivitas.sub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import salam.gopray.id.R;

/**
 * Created by root on 19/06/17.
 */

public class TextFragment extends Fragment {
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.text_fragment_freetext, container, false);
        return v;
    }
}
