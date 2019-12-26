package com.example.spotiistics;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class ArtistsActivity extends ListsActivity {
    private static final String TAG = ArtistsActivity.class.getSimpleName();
    TabLayout tabLayout;
    Artist artist;
    UserPrivate user;
    private ItemFragment statsFragment;
    private ItemFragment infoFragment;
    boolean isFollowing;



    @Override
    public int getItemSize() {
        return 350;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artista_activity);

        statsFragment = new ArtistsStatsFragment();
        infoFragment = new ArtistsInfoFragment();

        final ViewPager pager = findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), statsFragment, infoFragment);
        pager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_artista);
        tabLayout.setupWithViewPager(pager);

        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                user = userPrivate;
                getArtist();
            }

            @Override
            public void failure(SpotifyError error) {
                //TODO should we retry?
                if(error.hasErrorDetails()){
                    Log.e(TAG, error.getErrorDetails().message);
                }
                Toast.makeText(getApplicationContext(), "Failure getting user data", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getArtist() {
        spotify.getArtist(id,  new SpotifyCallback<Artist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                if(spotifyError.hasErrorDetails()){
                    Log.e(TAG, spotifyError.getErrorDetails().message);
                }
                Toast.makeText(getApplicationContext(),
                        "Error loading content", Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(Artist a, Response response) {
                artist = a;

                spotify.isFollowingArtists(a.id, new SpotifyCallback<boolean[]>() {
                    @Override
                    public void success(boolean[] fol, Response response) {
                        isFollowing = fol[0];
                    }

                    @Override
                    public void failure(SpotifyError error) {
                        Toast.makeText(getApplicationContext(), "Error loading following status", Toast.LENGTH_LONG).show();
                    }
                });

                ImageView iv = findViewById(R.id.image_artista);
                setPlaceHolder(iv);
                if(a.images.size() != 0) new DownloadImageTask(iv, getItemSize()).execute(a.images.get(0).url);

                TextView artistName = findViewById(R.id.artista_name);
                artistName.setText(a.name);

                statsFragment.updateData();
                infoFragment.updateData();
            }
        });
    }
}
