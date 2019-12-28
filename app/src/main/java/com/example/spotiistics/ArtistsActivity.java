package com.example.spotiistics;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Artist;
import retrofit.client.Response;

public class ArtistsActivity extends BaseLoggedActivity {
    private static final String TAG = ArtistsActivity.class.getSimpleName();
    TabLayout tabLayout;
    Artist artist;
    private ItemFragment statsFragment;
    private ItemFragment infoFragment;
    boolean isFollowing;

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
                if(a.images.size() != 0){
                    Glide
                            .with(getApplicationContext())
                            .load(a.images.get(0).url)
                            .placeholder(R.drawable.noalbum)
                            .into(iv);
                }

                TextView artistName = findViewById(R.id.artista_name);
                artistName.setText(a.name);

                statsFragment.updateData();
                infoFragment.updateData();
            }
        });
    }
}
