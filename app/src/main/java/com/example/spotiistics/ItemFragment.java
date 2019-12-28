package com.example.spotiistics;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public class ItemFragment extends Fragment {
    InflationListener mInflationListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mInflationListener = (InflationListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement" + InflationListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInflationListener = null;
    }

}
