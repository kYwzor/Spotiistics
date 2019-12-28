package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlaylistStatsFragment extends Fragment {
    private WeakReference<PlaylistActivity> activityReference;
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.playlist_statistics_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = new WeakReference<>((PlaylistActivity) getActivity());
    }

    void updateData() {
        PlaylistActivity pa = activityReference.get();
        if (pa==null) return;

        long minDuration = Long.MAX_VALUE;
        long maxDuration = 0L;
        long totalDuration = 0L;
        for (PlaylistTrack track : pa.playlist.tracks.items){
            long duration_ms = track.track.duration_ms;
            totalDuration += duration_ms;
            if(duration_ms < minDuration){
                minDuration = duration_ms;
            }
            if(duration_ms > maxDuration){
                maxDuration = duration_ms;
            }
        }
        long meanDuration = 0L;
        if(pa.playlist.tracks.items.size() > 0) {
            meanDuration = totalDuration / pa.playlist.tracks.items.size();
        }
        TextView dt = rootview.findViewById(R.id.duracao_total);
        dt.setText(Helper.msToString(totalDuration));

        TextView dm = rootview.findViewById(R.id.duracao_media);
        dm.setText(Helper.msToString(meanDuration));

        TextView tb = rootview.findViewById(R.id.track_maior);
        tb.setText(Helper.msToString(maxDuration));

        TextView ts = rootview.findViewById(R.id.track_menor);
        ts.setText(Helper.msToString(minDuration));

    }
}
