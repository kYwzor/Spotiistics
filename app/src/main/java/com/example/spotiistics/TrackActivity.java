package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class TrackActivity extends AppCompatActivity {
    String mAccessToken;
    SpotifyService spotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);

        Intent intent = getIntent();
        mAccessToken = intent.getStringExtra("token");
        final String trackID = intent.getStringExtra("track_id");

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        spotify = api.getService();

        spotify.getTrack(trackID, new SpotifyCallback<Track>() {
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

        View playlistButton = findViewById(R.id.include).findViewById(R.id.imageView4);
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent mIntent = new Intent(TrackActivity.this, PlaylistsActivity.class);
                mIntent.putExtra("token", mAccessToken);
                startActivity(mIntent);
            }
        });

        View searchButton = findViewById(R.id.include).findViewById(R.id.imageView5);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent mIntent = new Intent(TrackActivity.this, SearchActivity.class);
                mIntent.putExtra("token", mAccessToken);
                startActivity(mIntent);
            }
        });

    }
    private void setTrackDetails(Track track){
        getPlaceHolder();
        new TrackActivity.DownloadImageTask((ImageView) findViewById(R.id.image_album)).execute(track.album.images.get(0).url);

        TextView track_name = findViewById(R.id.track_name);
        track_name.setText(track.name);

        TextView artist_name = findViewById(R.id.track_artist);
        List<String> artist_string = new LinkedList<>();

        for(ArtistSimple artist :track.artists){
            artist_string.add(artist.name);
        }

        artist_name.setText(join(artist_string,", "));

        TextView album_name = findViewById(R.id.album_name);
        album_name.setText(track.album.name);

        TextView pop = findViewById(R.id.popularidade);
        pop.setText( stringPop(track.popularity));

        TextView duracao = findViewById(R.id.duracao_track);

        duracao.setText(mstostring(track.duration_ms));

        spotify.getAlbum(track.album.id, new SpotifyCallback<Album>() {
            @Override
            public void failure(SpotifyError spotifyError) {
            }

            @Override
            public void success(Album album, Response response) {
                setAlbumDetails(album);
            }
        });
    }

    private String stringPop(int pop){
        HashMap<Integer,String> hashmap = new HashMap();
        hashmap.put(0,"1- O que é isto?");
        hashmap.put(1,"2- Underground");
        hashmap.put(2,"3- Só para alguns");
        hashmap.put(3,"4- Bastante Popular");
        hashmap.put(4,"5- Topo das tabelas");
        hashmap.put(5,"6- A mais popular");

        int score = pop / 20; //6 categories

        return hashmap.get(score);
    }

    private void getPlaceHolder() {
        Drawable dr = getResources().getDrawable(R.drawable.noalbum);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,350, 350, true);
        ImageView imageView =  findViewById(R.id.image_album);
        imageView.setImageBitmap(resized);
    }


    //https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
    private static String join(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }

    private String mstostring(long ms){
        long minutes;
        long seconds;
        minutes = (ms / 1000) / 60;
        seconds = (ms / 1000) % 60;
        String s = minutes + "min " + seconds +"sec";
        return s;
    }

    private void setAlbumDetails(Album album) {
        TextView track_year = findViewById(R.id.ano);
        track_year.setText(album.release_date);
    }

    //https://stackoverflow.com/questions/14332296/how-to-set-image-from-url-using-asynctask/15797963
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Bitmap resized = Bitmap.createScaledBitmap(result,350, 350, true);
            bmImage.setImageBitmap(resized);
        }
    }
}
