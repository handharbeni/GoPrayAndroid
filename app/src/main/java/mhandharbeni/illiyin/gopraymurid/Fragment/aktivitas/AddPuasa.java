package mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mhandharbeni.illiyin.gopraymurid.R;

/**
 * Created by root on 12/05/17.
 */

public class AddPuasa extends Fragment {
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_family, container, false);
        return v;
    }
}
