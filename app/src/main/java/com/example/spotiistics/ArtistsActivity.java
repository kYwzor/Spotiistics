package com.example.spotiistics;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.spotiistics.Database.ArtistData;
import com.example.spotiistics.Database.ArtistDataDao;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
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

public class ArtistsActivity extends BaseLoggedActivity implements FragmentListener {
    private static final String TAG = ArtistsActivity.class.getSimpleName();
    TabLayout tabLayout;
    private ArtistsStatsFragment statsFragment;
    private ArtistsInfoFragment infoFragment;
    ArtistDataDao artistDataDao;
    ArtistData artistData;
    ArrayList<ImageView> albumIvs;
    ArrayList<ImageView> topIvs;

    boolean inDatabase;
    boolean statsReady = false;
    boolean infoReady = false;
    boolean[] dataReady = new boolean[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artista_activity);

        statsFragment = new ArtistsStatsFragment();
        infoFragment = new ArtistsInfoFragment();

        final ViewPager pager = findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), statsFragment, infoFragment);
        pager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadBitmap(id, (ImageView) findViewById(R.id.image_artista)); // might fail but it's ok
        artistDataDao = database.artistDataDao();
        ArtistData[] as = artistDataDao.get(id);
        if (as.length==0){
            artistData = new ArtistData(id);
            startSync();
        }
        else {
            inDatabase = true;
            artistData = as[0];

            albumIvs = new ArrayList<>();
            for (String albumId : artistData.albumIds){
                ImageView iv = new ImageView(this);
                albumIvs.add(iv);
                loadBitmap(albumId, iv);
            }

            topIvs = new ArrayList<>();
            for (String albumId : artistData.topTrackAlbums){
                ImageView iv = new ImageView(this);
                topIvs.add(iv);
                loadBitmap(albumId, iv);
            }
            Arrays.fill(dataReady, true);
            updateView();
        }
    }

    public void startSync() {
        spotify.getArtist(id,  new SpotifyCallback<Artist>() {
            @Override
            public void success(Artist a, Response response) {
                artistData.n_followers = a.followers.total;
                artistData.popularity = a.popularity;
                artistData.genres = new ArrayList<>(a.genres);
                artistData.name = a.name;
                TextView artistName = findViewById(R.id.artista_name);
                artistName.setText(a.name);
                ImageView iv = findViewById(R.id.image_artista);
                if(a.images.size() != 0){
                    Glide
                            .with(ArtistsActivity.this)
                            .load(a.images.get(0).url)
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
                checkReady(0);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }
        });

        spotify.isFollowingArtists(id, new SpotifyCallback<boolean[]>() {
            @Override
            public void success(boolean[] fol, Response response) {
                artistData.isFollowing = fol[0];
                checkReady(1);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }
        });

        Map<String, Object> options  = new HashMap<>();
        options.put("market", user.country);

        spotify.getArtistAlbums(id, options, new SpotifyCallback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> ap, Response response) {
                ArrayList<String> albumNames = new ArrayList<>();
                ArrayList<String> albumIds = new ArrayList<>();
                albumIvs = new ArrayList<>();
                for(final Album a : ap.items) {
                    albumNames.add(a.name);
                    albumIds.add(a.id);
                    ImageView iv = new ImageView(getApplicationContext());
                    albumIvs.add(iv);
                    if (a.images.size() != 0){
                        Glide
                                .with(ArtistsActivity.this)
                                .load(a.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        saveBitmap(a.id, Helper.drawableToBitmap(resource));
                                        return false;
                                    }
                                })
                                .into(iv);
                    }
                }
                artistData.albumNames = albumNames;
                artistData.albumIds = albumIds;
                checkReady(2);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }
        });

        spotify.getArtistTopTrack(id, "PT", new SpotifyCallback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                ArrayList<String> topTrackNames = new ArrayList<>();
                ArrayList<String> topTrackIds = new ArrayList<>();
                ArrayList<String> topTrackAlbums = new ArrayList<>();

                topIvs = new ArrayList<>();
                for (int i = 0; i<tracks.tracks.size(); i++) {
                    if (i >= 5) break;
                    final Track track = tracks.tracks.get(i);
                    topTrackNames.add(track.name);
                    topTrackIds.add(track.id);
                    topTrackAlbums.add(track.album.id);
                    ImageView iv = new ImageView(getApplicationContext());
                    topIvs.add(iv);
                    if(track.album.images.size() !=0){
                        Glide
                                .with(ArtistsActivity.this)
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
                }
                artistData.topTrackNames = topTrackNames;
                artistData.topTrackIds = topTrackIds;
                artistData.topTrackAlbums = topTrackAlbums;
                checkReady(3);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), "Error syncing", Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }
        });
    }


    public void checkReady(int n){
        dataReady[n] = true;

        if (dataReady[0] && dataReady[1] && dataReady[2] && dataReady[3]){
            if(inDatabase){
                artistDataDao.update(artistData);
            }
            else {
                artistDataDao.insert(artistData);
                inDatabase = true;
            }
            updateView();
        }
    }

    private void updateView() {
        if(statsReady) statsFragment.updateData(artistData, topIvs);
        if(infoReady) infoFragment.updateData(artistData, albumIvs);
    }

    @Override
    public void onFragmentSet(boolean isStats) {
        if (isStats){
            statsReady = true;
            if (dataReady[0] && dataReady[1] && dataReady[2] && dataReady[3]) statsFragment.updateData(artistData, topIvs);
        }
        else {
            infoReady = true;
            if (dataReady[0] && dataReady[1] && dataReady[2] && dataReady[3]) infoFragment.updateData(artistData, albumIvs);
        }
    }
}
