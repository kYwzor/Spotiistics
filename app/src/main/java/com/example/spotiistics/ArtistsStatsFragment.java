package com.example.spotiistics;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.client.Response;

public class ArtistsStatsFragment extends ItemFragment {
    private WeakReference<ArtistsActivity> activityReference;
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
        activityReference = new WeakReference<>((ArtistsActivity) getActivity());
    }

    @Override
    public void updateData() {
        final ArtistsActivity aa = activityReference.get();
        if (aa==null) return;
        Artist artist = aa.artist;

        TextView followers = rootview.findViewById(R.id.n_seguidores);
        followers.setText(String.valueOf(artist.followers.total));

        TextView isFollowing = rootview.findViewById(R.id.follow_artist);
        if(aa.isFollowing) isFollowing.setText("Sim");
        else isFollowing.setText("Não");

        TextView pop = rootview.findViewById(R.id.popularidade);
        pop.setText(Helper.stringPop(artist.popularity));

        TextView genero = rootview.findViewById(R.id.artista_genero);
        if(artist.genres.size()!=0) genero.setText(TextUtils.join(" | ", artist.genres));
        else genero.setText("Não disponível.");

        Map<String, Object> options  = new HashMap<>();
        options.put("market", BaseActivity.user.country);
        BaseActivity.spotify.getArtistAlbums(artist.id, options, new SpotifyCallback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> ap, Response response) {
                TextView n_album = rootview.findViewById(R.id.n_track_album);
                n_album.setText(String.valueOf(ap.items.size()));
            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(aa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
            }
        });

        BaseActivity.spotify.getArtistTopTrack(aa.artist.id, "PT", new SpotifyCallback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                LinearLayout base = rootview.findViewById(R.id.toptracks);

                itemClickListener itemClickListener = new itemClickListener();

                for (int i = 0; i<tracks.tracks.size(); i++) {
                    if(i >= 5) break;
                    Track a = tracks.tracks.get(i);
                    LinearLayout ll = aa.createLinearLayout(a.name, a.id);
                    ll.setOnClickListener(itemClickListener);
                    base.addView(ll);
                    if(a.album.images.size() !=0){
                        // new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(a.album.images.get(0).url);
                        Glide
                                .with(aa)
                                .load(a.album.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .into((ImageView) ll.getChildAt(0));
                    }
                }
            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(aa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
            }
        });
    }


    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(TrackActivity.class, (String) v.getTag(R.id.ID));
        }
    }
}
