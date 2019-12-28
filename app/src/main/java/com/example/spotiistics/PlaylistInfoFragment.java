package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlaylistInfoFragment extends Fragment {
    private WeakReference<PlaylistActivity> activityReference;
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.playlist_info_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = new WeakReference<>((PlaylistActivity) getActivity());
    }

    void updateData() {
        final PlaylistActivity pa = activityReference.get();
        if (pa==null) return;
        LinearLayout base = rootview.findViewById(R.id.lista_tracks);

        itemClickListener itemClickListener = new itemClickListener();
        for(PlaylistTrack track : pa.playlist.tracks.items) {
            TextView tv = Helper.createTextView(track.track.name, getContext());
            tv.setOnClickListener(itemClickListener);
            tv.setTag(track.track.id);
            base.addView(tv);
        }
    }

    public class itemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(TrackActivity.class, (String) v.getTag());
        }
    }
}
