package salam.gopray.id.Fragment.aktivitas;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import salam.gopray.id.Fragment.aktivitas.sub.ImageFragment;
import salam.gopray.id.Fragment.aktivitas.sub.TextFragment;
import salam.gopray.id.R;

/**
 * Created by root on 12/05/17.
 */

public class AddFreeText extends Fragment {
    private String TAG = "AddFreeTextFragment";
    View v;
    TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_layout_freetext, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.MaterialTab);
        setTab();

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainAktivitas)getActivity()).sendScreen(this.getClass().getName());
    }

    public void setTab(){
        changeFragment(new TextFragment());
        tabLayout.removeAllTabs();
        final TabLayout.Tab freetext = tabLayout.newTab();
        final TabLayout.Tab sticker = tabLayout.newTab();
        final String[] item = getResources().getStringArray(R.array.tab_freetext);
        freetext.setText(item[0]);
        sticker.setText(item[1]);


        tabLayout.addTab(freetext, 0);
        tabLayout.addTab(sticker, 1);


        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getActivity().getApplicationContext(), R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.indicator));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String text = null;
                switch (tab.getPosition()){
                    case 0:
                        text = item[0];
                        changeFragment(new TextFragment());
                        break;
                    case 1:
                        text = item[1];
                        changeFragment(new ImageFragment());
                        break;
                }
                tab.setText(text);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        changeFragment(new TextFragment());
                        break;
                    case 1:
                        changeFragment(new ImageFragment());
                        break;
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.getTabAt(0).isSelected();
    }
    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_freetext, fragment);
        ft.commit();
    }
}
