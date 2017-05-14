package mhandharbeni.illiyin.gopraymurid.Fragment.aktivitas;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import mhandharbeni.illiyin.gopraymurid.R;

/**
 * Created by root on 12/05/17.
 */

public class MainAktivitas extends AppCompatActivity {
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
                /*sholat*/
                fragment = new AddSholat();
                break;
            case 1:
                /*mengaji*/
                fragment = new AddMengaji();
                break;
            case 2:
                fragment = new AddSholat();
                break;
            case 3:
                fragment = new AddSholat();
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
