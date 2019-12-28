package com.example.spotiistics;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.spotiistics.Database.TrackData;
import com.example.spotiistics.Database.TrackDataDao;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class TrackActivity extends BaseLoggedActivity {
    private static final String TAG = TrackActivity.class.getSimpleName();
    Track track;
    Album album;
    boolean inDatabase = false;
    TrackDataDao trackDataDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);

        trackDataDao = database.trackDataDao();
        TrackData[] ts = trackDataDao.get(id);;
        if(ts.length==0){
            //Toast.makeText(TrackActivity.this, "Nothing in database", Toast.LENGTH_LONG).show();
            startSync();
        }
        else {
            inDatabase = true;
            //Toast.makeText(TrackActivity.this, "Read from database", Toast.LENGTH_LONG).show();
            loadBitmap(ts[0].albumdId, (ImageView) findViewById(R.id.image_album));
            updateView(ts[0]);
        }

    }

    public void startSync() {
        spotify.getTrack(id, new SpotifyCallback<Track>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }

            @Override
            public void success(Track track, Response response) {
                TrackActivity.this.track = track;
                midSync();
            }
        });
    }


    private void midSync(){
        // Only needed because library is bugged
        spotify.getAlbum(track.album.id, new SpotifyCallback<Album>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }

            @Override
            public void success(Album album, Response response) {
                TrackActivity.this.album = album;
                endSync();
            }
        });
    }

    private void endSync(){
        ImageView iv = findViewById(R.id.image_album);
        if (track.album.images.size() != 0) {
            Glide
                .with(getApplicationContext())
                .load(track.album.images.get(0).url)
                .placeholder(R.drawable.noalbum)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        saveBitmap(track.album.id, Helper.drawableToBitmap(resource));
                        return false;
                    }
                })
                .into(iv);
        }

        TrackData t = new TrackData(
                id,
                track.name,
                join(track.artists),
                track.album.name,
                track.album.id,
                Helper.stringPop(track.popularity),
                Helper.msToString(track.duration_ms),
                album.release_date
        );
        if(inDatabase){
            trackDataDao.update(t);
        }
        else {
            trackDataDao.insert(t);
            inDatabase = true;
        }
        updateView(t);
    }


    private void updateView(final TrackData t){
        TextView track_name = findViewById(R.id.track_name);
        track_name.setText(t.name);

        TextView artist_name = findViewById(R.id.track_artist);
        artist_name.setText(t.artists);

        TextView album_name = findViewById(R.id.album_name);
        album_name.setText(t.albumName);
        album_name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                changeActivity(AlbumActivity.class, t.albumdId);
            }
        });

        TextView pop = findViewById(R.id.popularidade);
        pop.setText(t.popularity);

        TextView duracao = findViewById(R.id.duracao_track);
        duracao.setText(t.duration);

        TextView track_year = findViewById(R.id.ano);
        track_year.setText(t.releaseDate);
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
}
