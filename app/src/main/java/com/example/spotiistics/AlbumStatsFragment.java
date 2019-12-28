package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.spotiistics.Database.AlbumData;

public class AlbumStatsFragment extends ItemFragment {
    private static final String TAG = AlbumStatsFragment.class.getSimpleName();
    private View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.album_statistics_tab, container, false);
        this.mInflationListener.onViewCreated(true);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void updateData(AlbumData a) {
        TextView duracao = rootview.findViewById(R.id.duracao_total);
        duracao.setText(Helper.msToString(a.totalDuration));

        TextView duracao_media = rootview.findViewById(R.id.duracao_media);
        duracao_media.setText(Helper.msToString(a.meanDuration));

        TextView track_maior = rootview.findViewById(R.id.track_maior);
        track_maior.setText(Helper.msToString(a.maxDuration));

        TextView track_menor = rootview.findViewById(R.id.track_menor);
        track_menor.setText(Helper.msToString(a.minDuration));

        TextView ritmo = rootview.findViewById(R.id.ritmo);
        ritmo.setText(a.meanTempo + " bpm");    // TODO: Hardcoded strings

        TextView mood = rootview.findViewById(R.id.mood);
        mood.setText(String.valueOf(a.meanMood));

    }
}
