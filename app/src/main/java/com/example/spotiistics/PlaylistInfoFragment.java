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

public class PlaylistInfoFragment extends ItemFragment {
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
        this.mFragmentListener.onFragmentSet(false);
    }

    void updateData(PlaylistData playlistData, ArrayList<ImageView> trackIvs) {
        TextView n_track = rootview.findViewById(R.id.playlist_n_track);
        String n_track_string = String.format(getResources().getString(R.string.n_tracks), playlistData.trackNames.size());
        n_track.setText(n_track_string);

        LinearLayout base = rootview.findViewById(R.id.lista_tracks);
        itemClickListener itemClickListener = new itemClickListener();
        for (int i=0; i<playlistData.trackNames.size(); i++) {
            LinearLayout ll = Helper.createHorizontalLinearLayout(playlistData.trackNames.get(i), playlistData.trackIds.get(i), trackIvs.get(i), getContext());
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
        }
    }

    public class itemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((PlaylistActivity) getActivity()).changeActivity(TrackActivity.class, (String) v.getTag(R.id.ID));
        }
    }
}
