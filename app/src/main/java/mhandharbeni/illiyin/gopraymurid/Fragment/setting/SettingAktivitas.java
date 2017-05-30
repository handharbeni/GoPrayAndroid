package mhandharbeni.illiyin.gopraymurid.Fragment.setting;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas.AddMengaji;
import mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas.AddPuasa;
import mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas.AddSedekah;
import mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas.AddSholat;
import mhandharbeni.illiyin.gopraymurid.R;

/**
 * Created by root on 26/05/17.
 */

public class SettingAktivitas extends AppCompatActivity {
    ImageView imageBack;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.layout_main_aktivitas);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Fragment fragment = null;
        Bundle bundle = getIntent().getExtras();
        int mode = bundle.getInt("MODE");
        switch (mode){
            case 0:
                /*akun*/
                fragment = new FragmentSettingAkun();
                break;
            case 1:
                /*mengaji*/
                fragment = new AddMengaji();
                break;
            case 2:
                fragment = new AddSedekah();
                break;
            case 3:
                fragment = new AddPuasa();
                break;
            case 4:
                fragment = new AddSholat();
                break;
            case 5:
                fragment = new AddSholat();
                break;
        }
        changeFragment(fragment);
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FrameContainer, fragment);
        ft.commit();
    }
}