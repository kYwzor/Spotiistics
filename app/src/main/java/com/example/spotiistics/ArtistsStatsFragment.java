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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.client.Response;

public class ArtistsStatsFragment extends ItemFragment {
    WeakReference<ArtistsActivity> activityReference;
    View rootview;
    int total;
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
        pop.setText(stringPop(artist.popularity));

        TextView genero = rootview.findViewById(R.id.artista_genero);
        if(artist.genres.size()!=0) genero.setText(join(artist.genres));
        else genero.setText("Não disponível.");

        Map<String, Object> options  = new HashMap<>();
        options.put("market", "PT");
        aa.spotify.getArtistAlbums(artist.id, options, new SpotifyCallback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> ap, Response response) {
                TextView n_album = rootview.findViewById(R.id.n_track_album);
                int na = 0;
                for( Album a : ap.items){
                    na++;
                }
                n_album.setText(String.valueOf(na));
            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(aa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();

            }
        });

        aa.spotify.getArtistTopTrack(aa.artist.id, "PT", new SpotifyCallback<Tracks>() {
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
                    if(a.album.images.size() !=0) new DownloadImageTask((ImageView) ll.getChildAt(0), aa.getItemSize()).execute(a.album.images.get(0).url);
                }
            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(aa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String stringPop(int pop){
        HashMap<Integer,String> hashmap = new HashMap();
        // TODO: hardcoded strings
        hashmap.put(0,"1- O que é isto?");
        hashmap.put(1,"2- Underground");
        hashmap.put(2,"3- Só para alguns");
        hashmap.put(3,"4- Bastante Popular");
        hashmap.put(4,"5- Topo das tabelas");
        hashmap.put(5,"6- A mais popular");

        int score = pop / 20; //6 categories
        return hashmap.get(score);
    }


    // based on https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
    private static String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        String loopDelim = "";

        for(String a : list) {
            sb.append(loopDelim);
            sb.append(a);
            loopDelim = " | ";
        }
        return sb.toString();
    }

    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(TrackActivity.class, (String) v.getTag(R.id.ID));
        }
    }
}
