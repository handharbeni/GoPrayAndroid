package mhandharbeni.illiyin.gopraymurid.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mhandharbeni.illiyin.gopraymurid.R;

/**
 * Created by root on 19/04/17.
 */

public class Meme extends Fragment {
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_meme, container, false);
        return v;
    }

}
