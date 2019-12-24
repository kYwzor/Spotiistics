package com.example.spotiistics;

import android.content.Context;
import android.content.res.Resources;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private WeakReference<Context> contextReference;
    private ItemFragment statsFragment;
    private ItemFragment infoFragment;

    TabsPagerAdapter(FragmentManager fm, Context context, ItemFragment statsFragment, ItemFragment infoFragment) {
        super(fm);
        contextReference = new WeakReference<>(context);
        this.statsFragment = statsFragment;
        this.infoFragment = infoFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return statsFragment;
        }else{
            return infoFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Context context = contextReference.get();
        if(context==null) return null;
        Resources resources = context.getResources();

        if(position==0) return resources.getString(R.string.tab_statistics);
        return resources.getString(R.string.tab_info);
    }
}
