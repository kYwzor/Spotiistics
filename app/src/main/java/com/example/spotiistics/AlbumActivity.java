package com.example.spotiistics;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.spotiistics.Database.AlbumData;
import com.example.spotiistics.Database.AlbumDataDao;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.client.Response;

public class AlbumActivity extends BaseLoggedActivity implements InflationListener{
    private static final String TAG = AlbumActivity.class.getSimpleName();
    TabLayout tabLayout;
    Album album;
    AudioFeaturesTracks af;
    private AlbumStatsFragment statsFragment;
    private AlbumInfoFragment infoFragment;
    boolean inDatabase = false;
    boolean statsReady = false;
    boolean infoReady = false;
    boolean dataReady = false;
    AlbumDataDao albumDataDao;
    AlbumData albumData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity);

        statsFragment = new AlbumStatsFragment();
        infoFragment = new AlbumInfoFragment();

        final ViewPager pager = findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), statsFragment, infoFragment);
        pager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadBitmap(id, (ImageView) findViewById(R.id.image_album)); // might fail but it's ok
        albumDataDao = database.albumDataDao();
        AlbumData[] as = albumDataDao.get(id);
        if (as.length==0){
            startSync();
        }
        else {
            inDatabase = true;
            albumData = as[0];
            updateView();
        }
    }

    public void startSync() {
        spotify.getAlbum(id,  new SpotifyCallback<Album>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }

            @Override
            public void success(final Album a, Response response) {
                album = a;
                midSync();
            }
        });
    }

    private void midSync() {
        spotify.getTracksAudioFeatures(join(album.tracks.items), new SpotifyCallback<AudioFeaturesTracks>() {
            @Override
            public void success(AudioFeaturesTracks af, Response response) {
                AlbumActivity.this.af = af;
                endSync();
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }
        });
    }

    private void endSync() {
        ImageView iv = findViewById(R.id.image_album);
        if(album.images.size() != 0) {
            Glide
                .with(getApplicationContext())
                .load(album.images.get(0).url)
                .placeholder(R.drawable.noalbum)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        saveBitmap(id, Helper.drawableToBitmap(resource));
                        return false;
                    }
                })
                .into(iv);
        }

        ArrayList<String> trackNames = new ArrayList<>();
        ArrayList<String> trackIds = new ArrayList<>();
        long totalDuration = 0;
        long minDuration = Long.MAX_VALUE;
        long maxDuration = 0L;
        for (TrackSimple t : album.tracks.items){
            totalDuration += t.duration_ms;
            if(t.duration_ms < minDuration){
                minDuration = t.duration_ms;
            }
            if(t.duration_ms > maxDuration){
                maxDuration = t.duration_ms;
            }
            trackNames.add(t.name);
            trackIds.add(t.id);
        }

        float total_tempo = 0;
        float total_mood = 0;
        for(AudioFeaturesTrack a : af.audio_features){
            total_tempo += a.tempo;
            total_mood += a.valence;
        }

        albumData = new AlbumData(
                id,
                album.name,
                album.artists.get(0).name,
                album.artists.get(0).id,
                album.release_date,
                trackNames,
                trackIds,
                totalDuration,
                totalDuration/album.tracks.items.size(),
                minDuration,
                maxDuration,
                total_tempo/album.tracks.items.size(),
                total_mood/album.tracks.items.size()
        );
        if(inDatabase){
            albumDataDao.update(albumData);
        }
        else {
            albumDataDao.insert(albumData);
            inDatabase = true;
        }
        updateView();
    }

    private void updateView() {
        TextView albumName = findViewById(R.id.album_name);
        albumName.setText(albumData.name);

        TextView artistName = findViewById(R.id.album_artist);
        artistName.setText(albumData.artistName);
        artistName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                changeActivity(ArtistsActivity.class, albumData.artistId);
            }
        });

        dataReady = true;
        if (statsReady) statsFragment.updateData(albumData);
        if (infoReady) infoFragment.updateData(albumData);
    }

    // based on https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
    private static String join(List<TrackSimple> list) {
        StringBuilder sb = new StringBuilder();
        String loopDelim = "";

        for(TrackSimple a : list) {
            sb.append(loopDelim);
            sb.append(a.id);
            loopDelim = ",";
        }
        return sb.toString();
    }

    @Override
    public void onViewCreated(boolean isStats) {
        if(isStats){
            statsReady = true;
            if (dataReady) statsFragment.updateData(albumData);
        }
        else {
            infoReady = true;
            if (dataReady) infoFragment.updateData(albumData);
        }
    }
}
