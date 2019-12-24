package com.example.spotiistics;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class TrackActivity extends BaseLoggedActivity {

    @Override
    public int getItemSize() {
        return 350; // TODO: this should be a scaling value
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);

        spotify.getTrack(id, new SpotifyCallback<Track>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(TrackActivity.this,
                        "Error loading content", Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(Track track, Response response) {
                setTrackDetails(track);
            }
        });

    }
    private void setTrackDetails(Track track){
        ImageView iv = findViewById(R.id.image_album);
        setPlaceHolder(iv);
        new DownloadImageTask(iv, getItemSize()).execute(track.album.images.get(0).url);

        TextView track_name = findViewById(R.id.track_name);
        track_name.setText(track.name);

        TextView artist_name = findViewById(R.id.track_artist);
        artist_name.setText(join(track.artists));

        TextView album_name = findViewById(R.id.album_name);
        album_name.setText(track.album.name);

        TextView pop = findViewById(R.id.popularidade);
        pop.setText(stringPop(track.popularity));

        TextView duracao = findViewById(R.id.duracao_track);
        duracao.setText(msToString(track.duration_ms));

        spotify.getAlbum(track.album.id, new SpotifyCallback<Album>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                //TODO: handle this
            }

            @Override
            public void success(Album album, Response response) {
                TextView track_year = findViewById(R.id.ano);
                track_year.setText(album.release_date);
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
        //TODO: Pretty sure this will only create 5 categories :)

        return hashmap.get(score);
    }


    // based on https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
    private static String join(List<ArtistSimple> list) {
        StringBuilder sb = new StringBuilder();
        String loopDelim = "";

        for(ArtistSimple a : list) {
            sb.append(loopDelim);
            sb.append(a.name);
            loopDelim = ", ";
        }
        return sb.toString();
    }

    private String msToString(long ms){
        long minutes;
        long seconds;
        minutes = (ms / 1000) / 60;
        seconds = (ms / 1000) % 60;
        return minutes + "min " + seconds +"sec";   // TODO: hardcoded strings
    }
}
