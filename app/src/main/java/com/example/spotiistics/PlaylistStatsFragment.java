package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.spotiistics.Database.PlaylistData;

import java.util.ArrayList;

public class PlaylistStatsFragment extends ItemFragment {
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
        this.mFragmentListener.onFragmentSet(true);
    }


    void updateData(PlaylistData playlistData, ArrayList<ImageView> artistIvs, ArrayList<ImageView> albumIvs) {
        TextView dt = rootview.findViewById(R.id.duracao_total);
        dt.setText(Helper.msToString(playlistData.totalDuration));

        TextView dm = rootview.findViewById(R.id.duracao_media);
        dm.setText(Helper.msToString(playlistData.meanDuration));

        TextView tb = rootview.findViewById(R.id.track_maior);
        tb.setText(Helper.msToString(playlistData.maxDuration));

        TextView ts = rootview.findViewById(R.id.track_menor);
        ts.setText(Helper.msToString(playlistData.minDuration));


        LinearLayout base = rootview.findViewById(R.id.top_artists);
        artistClickListener artistClickListener = new artistClickListener();
        for (int i = 0; i<playlistData.topArtistNames.size(); i++){
            LinearLayout ll = Helper.createVerticalLinearLayout(playlistData.topArtistNames.get(i), playlistData.topArtistIds.get(i), artistIvs.get(i), getContext());
            ll.setOnClickListener(artistClickListener);
            base.addView(ll);
        }

        base = rootview.findViewById(R.id.top_albums);
        albumClickListener albumClickListener = new albumClickListener();
        for (int i = 0; i<playlistData.topAlbumNames.size(); i++){
            LinearLayout ll = Helper.createVerticalLinearLayout(playlistData.topAlbumNames.get(i), playlistData.topAlbumIds.get(i), albumIvs.get(i), getContext());
            ll.setOnClickListener(albumClickListener);
            base.addView(ll);
        }

        TextView ritmo = rootview.findViewById(R.id.ritmo);
        String ritmo_string = playlistData.meanTempo + " bpm";
        ritmo.setText(ritmo_string);    // TODO: Hardcoded strings

        TextView mood = rootview.findViewById(R.id.mood);
        mood.setText(String.valueOf(playlistData.meanMood));

    }

    public class artistClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((PlaylistActivity) getActivity()).changeActivity(ArtistActivity.class, (String) v.getTag(R.id.ID));
        }
    }

    public class albumClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((PlaylistActivity) getActivity()).changeActivity(AlbumActivity.class, (String) v.getTag(R.id.ID));
        }
    }
}
