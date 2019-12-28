package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import kaaes.spotify.webapi.android.models.TrackSimple;

public class AlbumInfoFragment extends ItemFragment {
    private WeakReference<AlbumActivity> activityReference;
    private View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.album_info_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = new WeakReference<>((AlbumActivity) getActivity());
    }

    @Override
    public void updateData() {
        final AlbumActivity aa = activityReference.get();
        if (aa==null) return;
        TextView data_lancamento = rootview.findViewById(R.id.album_data);
        data_lancamento.setText(aa.album.release_date);

        TextView n_track = rootview.findViewById(R.id.n_track);
        n_track.setText(aa.album.tracks.total + " tracks");     //TODO: Hardcoded text

        LinearLayout base = rootview.findViewById(R.id.lista_tracks);
        itemClickListener itemClickListener = new itemClickListener();

        for( TrackSimple a : aa.album.tracks.items) {
            TextView tv = aa.createTextView(a.name);
            tv.setOnClickListener(itemClickListener);
            tv.setTag(a.id);
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
