package mhandharbeni.illiyin.gopraymurid.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mhandharbeni.illiyin.gopraymurid.Fragment.Family;
import mhandharbeni.illiyin.gopraymurid.Fragment.Meme;
import mhandharbeni.illiyin.gopraymurid.Fragment.Setting;
import mhandharbeni.illiyin.gopraymurid.Fragment.Timeline;

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
