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
import kaaes.spotify.webapi.android.models.Playlist;
import retrofit.client.Response;

public class PlaylistActivity extends BaseLoggedActivity {
    private static final String TAG = PlaylistActivity.class.getSimpleName();
    TabLayout tabLayout;
    Playlist playlist;
    private PlaylistStatsFragment statsFragment;
    private PlaylistInfoFragment infoFragment;

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


        spotify.getPlaylist(user.id, id, new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                if(spotifyError.hasErrorDetails()){
                    Log.e(TAG, spotifyError.getErrorDetails().message);
                }
                Toast.makeText(getApplicationContext(),
                        "Error loading content", Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(Playlist p, Response response) {
                playlist = p;

                ImageView iv = findViewById(R.id.image_playlist);
                //new DownloadImageTask(iv).execute(playlist.images.get(0).url);
                if(playlist.images.size() != 0) {
                    // new DownloadImageTask(iv).execute(a.images.get(0).url);
                    Glide
                            .with(getApplicationContext())
                            .load(playlist.images.get(0).url)
                            .placeholder(R.drawable.noalbum)
                            .into(iv);
                }

                TextView playlistName = findViewById(R.id.playlist_name);
                playlistName.setText(p.name);

                TextView playlistOwner = findViewById(R.id.playlist_creator);
                playlistOwner.setText(p.owner.display_name);

                statsFragment.updateData();
                infoFragment.updateData();
            }
        });
    }
}
