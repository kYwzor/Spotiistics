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
import com.example.spotiistics.Database.PlaylistData;
import com.example.spotiistics.Database.PlaylistDataDao;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.client.Response;

public class PlaylistActivity extends SyncableActivity implements FragmentListener {
    private static final String TAG = PlaylistActivity.class.getSimpleName();
    TabLayout tabLayout;
    private PlaylistStatsFragment statsFragment;
    private PlaylistInfoFragment infoFragment;
    PlaylistDataDao playlistDataDao;
    PlaylistData playlistData;
    ArrayList<ImageView> trackIvs;
    ArrayList<ImageView> artistIvs;
    ArrayList<ImageView> albumIvs;

    boolean inDatabase;
    boolean statsReady;
    boolean infoReady;
    boolean[] dataReady = new boolean[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        statsFragment = new PlaylistStatsFragment();
        infoFragment = new PlaylistInfoFragment();

        final ViewPager pager = findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), statsFragment, infoFragment);
        pager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);


        loadBitmap(id, (ImageView) findViewById(R.id.image_playlist)); // might fail but it's ok
        playlistDataDao = database.playlistDataDao();
        PlaylistData[] ps = playlistDataDao.get(id);
        if (ps.length==0){
            startSync();
        }
        else {
            inDatabase = true;
            playlistData = ps[0];

            trackIvs = new ArrayList<>();
            for (String tracAlbumkId : playlistData.trackAlbums){
                ImageView iv = new ImageView(this);
                trackIvs.add(iv);
                loadBitmap(tracAlbumkId, iv);
            }

            artistIvs = new ArrayList<>();
            for (String artistId : playlistData.topArtistIds){
                ImageView iv = new ImageView(this);
                artistIvs.add(iv);
                loadBitmap(artistId, iv);
            }

            albumIvs = new ArrayList<>();
            for (String albumId : playlistData.topAlbumIds){
                ImageView iv = new ImageView(this);
                albumIvs.add(iv);
                loadBitmap(albumId, iv);
            }

            Arrays.fill(dataReady, true);
            updateView();
        }
    }

    public void startSync() {
        Arrays.fill(dataReady, false);
        playlistData = new PlaylistData(id);
        spotify.getPlaylist(user.id, id, new SpotifyCallback<Playlist>() {
            @Override
            public void success(final Playlist p, Response response) {

                spotify.getTracksAudioFeatures(join(p.tracks.items), new SpotifyCallback<AudioFeaturesTracks>() {
                    @Override
                    public void success(AudioFeaturesTracks af, Response response) {
                        float total_tempo = 0;
                        float total_mood = 0;
                        for(AudioFeaturesTrack a : af.audio_features){
                            total_tempo += a.tempo;
                            total_mood += a.valence;

                        }
                        playlistData.meanTempo = total_tempo / p.tracks.items.size();
                        playlistData.meanMood = total_mood / p.tracks.items.size();
                        checkReady(1);
                    }

                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Toast.makeText(getApplicationContext(), R.string.sync_fail, Toast.LENGTH_LONG).show();
                        Log.e(TAG, spotifyError.getMessage());
                    }
                });


                playlistData.name = p.name;
                playlistData.owner = p.owner.display_name;

                ImageView iv = findViewById(R.id.image_playlist);
                if(p.images.size() != 0) {
                    Glide
                            .with(PlaylistActivity.this)
                            .load(p.images.get(0).url)
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

                long minDuration = Long.MAX_VALUE;
                long maxDuration = 0L;
                long totalDuration = 0L;

                ArrayList<String> trackNames = new ArrayList<>();
                ArrayList<String> trackIds = new ArrayList<>();
                ArrayList<String> trackAlbums = new ArrayList<>();
                trackIvs = new ArrayList<>();

                Map<String, Integer> maxArtist = new HashMap<>();
                Map<String, ArtistSimple> stringToArtist = new HashMap<>();
                Map<String, Integer> maxAlbuns = new HashMap<>();
                Map<String, AlbumSimple> stringToAlbum = new HashMap<>();
                for (final PlaylistTrack track : p.tracks.items){
                    long duration_ms = track.track.duration_ms;
                    totalDuration += duration_ms;
                    if(duration_ms < minDuration){
                        minDuration = duration_ms;
                    }
                    if(duration_ms > maxDuration){
                        maxDuration = duration_ms;
                    }

                    trackNames.add(track.track.name);
                    trackIds.add(track.track.id);
                    trackAlbums.add(track.track.album.id);
                    iv = new ImageView(getApplicationContext());
                    trackIvs.add(iv);

                    if (track.track.album.images.size() != 0){
                        Glide
                                .with(PlaylistActivity.this)
                                .load(track.track.album.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        saveBitmap(track.track.album.id, Helper.drawableToBitmap(resource));
                                        return false;
                                    }
                                })
                                .into(iv);
                    }

                    for(ArtistSimple a:  track.track.artists) {
                        if (! stringToArtist.containsKey(a.id)) {
                            stringToArtist.put(a.id, a);
                        }
                        if (maxArtist.containsKey(a.id)) {
                            maxArtist.put(a.id, maxArtist.get(a.id) + 1);
                        } else {
                            maxArtist.put(a.id, 1);
                        }
                    }
                    if (! stringToAlbum.containsKey(track.track.album.id)) {
                        stringToAlbum.put(track.track.album.id, track.track.album);
                    }
                    if(maxAlbuns.containsKey(track.track.album.id)){
                        maxAlbuns.put(track.track.album.id, maxAlbuns.get(track.track.album.id)+1);
                    }else{
                        maxAlbuns.put(track.track.album.id,1);
                    }
                }
                long meanDuration = 0L;
                if(p.tracks.items.size() > 0) {
                    meanDuration = totalDuration / p.tracks.items.size();
                }
                playlistData.minDuration = minDuration;
                playlistData.maxDuration = maxDuration;
                playlistData.totalDuration = totalDuration;
                playlistData.meanDuration = meanDuration;

                playlistData.trackNames = trackNames;
                playlistData.trackIds = trackIds;
                playlistData.trackAlbums = trackAlbums;


                List<Map.Entry<String, Integer>> greatestArtists = Helper.findGreatest(maxArtist, 5);
                ArrayList<String> topArtistNames = new ArrayList<>();
                ArrayList<String> topArtistIds = new ArrayList<>();
                artistIvs = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : greatestArtists){
                    ArtistSimple a = stringToArtist.get(entry.getKey());
                    topArtistNames.add(a.name);
                    topArtistIds.add(a.id);

                    final ImageView finalIv = new ImageView(PlaylistActivity.this);;
                    artistIvs.add(finalIv);
                    spotify.getArtist(a.id, new SpotifyCallback<Artist>() {
                        @Override
                        public void success(final Artist artist, Response response) {
                            // We use this just to get artists' images so we don't really need to sync
                            if(artist.images.size() !=0){
                                Glide
                                        .with(PlaylistActivity.this)
                                        .load(artist.images.get(0).url)
                                        .placeholder(R.drawable.noalbum)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                saveBitmap(artist.id, Helper.drawableToBitmap(resource));
                                                return false;
                                            }
                                        })
                                        .into(finalIv);
                            }
                        }

                        @Override
                        public void failure(SpotifyError spotifyError) {
                            Toast.makeText(getApplicationContext(), R.string.sync_fail, Toast.LENGTH_LONG).show();
                            Log.e(TAG, spotifyError.getMessage());
                        }
                    });
                }
                playlistData.topArtistNames = topArtistNames;
                playlistData.topArtistIds = topArtistIds;


                List<Map.Entry<String, Integer>> greatestAlbuns = Helper.findGreatest(maxAlbuns, 5);
                ArrayList<String> topAlbumNames = new ArrayList<>();
                ArrayList<String> topAlbumIds = new ArrayList<>();
                albumIvs = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : greatestAlbuns){
                    AlbumSimple a = stringToAlbum.get(entry.getKey());
                    topAlbumNames.add(a.name);
                    topAlbumIds.add(a.id);
                    iv = new ImageView(getApplicationContext());
                    albumIvs.add(iv);

                    if (a.images.size() != 0) {
                        // No listener because we don't need to save them to file again
                        Glide
                                .with(PlaylistActivity.this)
                                .load(a.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .into(iv);
                    }
                }
                playlistData.topAlbumNames = topAlbumNames;
                playlistData.topAlbumIds = topAlbumIds;
                checkReady(0);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), R.string.sync_fail, Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }
        });
    }

    public void checkReady(int n){
        dataReady[n] = true;

        if (isReady()){
            if(inDatabase){
                playlistDataDao.update(playlistData);
            }
            else {
                playlistDataDao.insert(playlistData);
                inDatabase = true;
            }
            onSyncDone();
            updateView();
        }
    }

    @Override
    boolean isReady() {
        return dataReady[0] && dataReady[1];
    }

    private void updateView(){
        TextView playlistName = findViewById(R.id.playlist_name);
        playlistName.setText(playlistData.name);

        TextView playlistOwner = findViewById(R.id.playlist_creator);
        playlistOwner.setText(playlistData.owner);

        if(statsReady) statsFragment.updateData(playlistData, artistIvs, albumIvs);
        if(infoReady) infoFragment.updateData(playlistData, trackIvs);
    }

    // based on https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
    private static String join(List<PlaylistTrack> list) {
        StringBuilder sb = new StringBuilder();
        String loopDelim = "";

        for(PlaylistTrack a : list) {
            sb.append(loopDelim);
            sb.append(a.track.id);
            loopDelim = ",";
        }
        return sb.toString();
    }

    @Override
    public void onFragmentSet(boolean isStats) {
        if (isStats){
            statsReady = true;
            if (isReady()) statsFragment.updateData(playlistData, artistIvs, albumIvs);
        }
        else {
            infoReady = true;
            if (isReady()) infoFragment.updateData(playlistData, trackIvs);
        }
    }
}
