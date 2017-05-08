package mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dunst.check.CheckableImageButton;

import mhandharbeni.illiyin.gopraymurid.R;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by root on 04/05/17.
 */

public class AddSholat extends Fragment implements View.OnClickListener {
    View v;
    CheckableImageButton checkSubuh, checkDhuhur, checkAshar, checkMaghrib, checkIsya, checkSunnah;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tambah_sholat, container, false);
        checkSubuh = (CheckableImageButton) v.findViewById(R.id.btnSubuh);
        checkDhuhur = (CheckableImageButton) v.findViewById(R.id.btnDhuhur);
        checkAshar = (CheckableImageButton) v.findViewById(R.id.btnAshar);
        checkMaghrib = (CheckableImageButton) v.findViewById(R.id.btnMaghrib);
        checkIsya = (CheckableImageButton) v.findViewById(R.id.btnIsya);
        checkSunnah= (CheckableImageButton) v.findViewById(R.id.btnSunnah);
        uncheckButton();
        checkSunnah.setOnClickListener(this);
        checkSubuh.setOnClickListener(this);
        checkDhuhur.setOnClickListener(this);
        checkAshar.setOnClickListener(this);
        checkMaghrib.setOnClickListener(this);
        checkIsya.setOnClickListener(this);
        return v;
    }
    public void uncheckButton(){
        checkSubuh.setChecked(FALSE);
        checkDhuhur.setChecked(FALSE);
        checkAshar.setChecked(FALSE);
        checkMaghrib.setChecked(FALSE);
        checkIsya.setChecked(FALSE);
        checkSunnah.setChecked(FALSE);
    }
    public void uncheckButton(String s){
        
    }

    @Override
    public void onClick(View v) {
        uncheckButton();

        switch (v.getId()){
            case R.id.btnSubuh :
                checkSubuh.setChecked(TRUE);
                break;
            case R.id.btnDhuhur :
                checkDhuhur.setChecked(TRUE);
                break;
            case R.id.btnAshar :
                checkAshar.setChecked(TRUE);
                break;
            case R.id.btnMaghrib :
                checkMaghrib.setChecked(TRUE);
                break;
            case R.id.btnIsya :
                checkIsya.setChecked(TRUE);
                break;
            case R.id.btnSunnah :
                checkSunnah.setChecked(TRUE);
                break;
        }
    }
}
