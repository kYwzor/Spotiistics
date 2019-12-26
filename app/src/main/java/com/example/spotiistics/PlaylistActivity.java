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
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class PlaylistActivity extends BaseLoggedActivity {
    private static final String TAG = PlaylistActivity.class.getSimpleName();
    TabLayout tabLayout;
    Playlist playlist;
    UserPrivate user;
    private ItemFragment statsFragment;
    private ItemFragment infoFragment;

    @Override
    public int getItemSize() {
        return 150;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        statsFragment = new PlaylistStatsFragment();
        infoFragment = new PlaylistInfoFragment();

        final ViewPager pager = findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), statsFragment, infoFragment);
        pager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_playlist);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(pager);

        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                user = userPrivate;
                getPlaylist();
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

    private void getPlaylist() {
        spotify.getPlaylist(id, user.id, new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                // TODO: This fails because endpoint changed!
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
                setPlaceHolder(iv);
                new DownloadImageTask(iv, getItemSize()).execute(playlist.images.get(0).url);

                TextView playlistName = findViewById(R.id.playlist_name);
                playlistName.setText(p.name);

                statsFragment.updateData();
                infoFragment.updateData();
            }
        });
    }
}
