package com.example.spotiistics;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public class ItemFragment extends Fragment {
    FragmentListener mFragmentListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mFragmentListener = (FragmentListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement" + FragmentListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

}
