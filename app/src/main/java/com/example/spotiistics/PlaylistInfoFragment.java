package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.client.Response;

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
        TextView n_track = rootview.findViewById(R.id.playlist_n_track);
        n_track.setText(pa.playlist.tracks.total + " tracks");     //TODO: Hardcoded text);

        LinearLayout base = rootview.findViewById(R.id.lista_tracks);
        itemClickListener itemClickListener = new itemClickListener();
        for(final PlaylistTrack track : pa.playlist.tracks.items) {
            LinearLayout ll = Helper.createHorizontalLinearLayout(track.track.name, track.track.id, new ImageView(getContext()), getContext());
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            if (track.track.album.images.size() != 0){
                Glide
                        .with(pa)
                        .load(track.track.album.images.get(0).url)
                        .placeholder(R.drawable.noalbum)
                        .into((ImageView) ll.getChildAt(0));
            }
        }


    }

    public class itemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(TrackActivity.class, (String) v.getTag(R.id.ID));
        }
    }
}
