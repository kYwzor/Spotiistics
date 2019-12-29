package com.example.spotiistics;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.spotiistics.Database.ArtistData;

import java.util.ArrayList;

public class ArtistsStatsFragment extends ItemFragment {
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.artista_statistics_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mFragmentListener.onFragmentSet(true);
    }

    void updateData(ArtistData artistData, ArrayList<ImageView> topIvs) {
        TextView followers = rootview.findViewById(R.id.n_seguidores);
        followers.setText(String.valueOf(artistData.n_followers));

        TextView isFollowing = rootview.findViewById(R.id.follow_artist);
        if(artistData.isFollowing) isFollowing.setText("Sim");  //TODO: Hardcoded strings
        else isFollowing.setText("Não");

        TextView pop = rootview.findViewById(R.id.popularidade);
        pop.setText(Helper.stringPop(artistData.popularity));

        TextView genero = rootview.findViewById(R.id.artista_genero);
        if(artistData.genres.size()!=0) genero.setText(TextUtils.join(" | ", artistData.genres));
        else genero.setText("Não disponível."); //TODO: Hardcoded strings

        TextView n_album = rootview.findViewById(R.id.n_track_album);
        n_album.setText(String.valueOf(artistData.albumNames.size()));


        LinearLayout base = rootview.findViewById(R.id.toptracks);
        itemClickListener itemClickListener = new itemClickListener();

        for (int i = 0; i<artistData.topTrackNames.size(); i++) {
            if(i >= 5) break;
            LinearLayout ll = Helper.createVerticalLinearLayout(artistData.topTrackNames.get(i), artistData.topTrackIds.get(i), topIvs.get(i), getContext());
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
        }
    }


    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((ArtistsActivity) getActivity()).changeActivity(TrackActivity.class, (String) v.getTag(R.id.ID));
        }
    }
}
