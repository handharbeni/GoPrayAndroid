package salam.gopray.id.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import salam.gopray.id.Fragment.Family;
import salam.gopray.id.Fragment.Meme;
import salam.gopray.id.Fragment.Setting;
import salam.gopray.id.Fragment.Timeline;

/**
 * Created by root on 19/04/17.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new Timeline();
            case 1:
                return new Family();
            case 2:
                return new Meme();
            case 3:
                return new Setting();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
