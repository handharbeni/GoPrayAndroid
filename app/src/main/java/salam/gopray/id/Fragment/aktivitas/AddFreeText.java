package salam.gopray.id.Fragment.aktivitas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import salam.gopray.id.R;

/**
 * Created by root on 12/05/17.
 */

public class AddFreeText extends Fragment {
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_family, container, false);
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainAktivitas)getActivity()).sendScreen(this.getClass().getName());
    }
}
