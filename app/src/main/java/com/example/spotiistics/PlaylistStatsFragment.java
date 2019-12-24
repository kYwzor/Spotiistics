package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlaylistStatsFragment extends ItemFragment {
    WeakReference<PlaylistActivity> activityReference;
    View rootview;
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

    @Override
    public void updateData() {
        PlaylistActivity pa = activityReference.get();
        if (pa==null) return;

        Long minDuration = Long.MAX_VALUE;
        Long maxDuration = -1L;
        Long totalDuration = 0L;
        int count = 0;
        for (PlaylistTrack track : pa.playlist.tracks.items){
            Long duration_ms = track.track.duration_ms;
            totalDuration += duration_ms;
            if(duration_ms < minDuration){
                minDuration = duration_ms;
            }
            if(duration_ms > maxDuration){
                maxDuration = duration_ms;
            }
            count ++;
        }
        Long meanDuration = 0L;
        if(count > 0) {
            meanDuration = totalDuration / count;
        }
        TextView dt = rootview.findViewById(R.id.duracao_total);
        dt.setText(totalDuration.toString());

        TextView dm = rootview.findViewById(R.id.duracao_media);
        dm.setText(meanDuration.toString());

        TextView tb = rootview.findViewById(R.id.track_maior);
        tb.setText(maxDuration.toString());

        TextView ts = rootview.findViewById(R.id.track_menor);
        ts.setText(minDuration.toString());

    }
}
