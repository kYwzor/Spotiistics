package com.example.spotiistics;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class AlbumActivity extends ListsActivity {
    private static final String TAG = AlbumActivity.class.getSimpleName();
    TabLayout tabLayout;
    Album album;
    UserPrivate user;
    private ItemFragment statsFragment;
    private ItemFragment infoFragment;

    @Override
    public int getItemSize() {
        return 350;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity);

        statsFragment = new AlbumStatsFragment();
        infoFragment = new AlbumInfoFragment();

        final ViewPager pager = findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), statsFragment, infoFragment);
        pager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_album);
        tabLayout.setupWithViewPager(pager);

        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                user = userPrivate;
                getAlbum();
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

    private void getAlbum() {
        spotify.getAlbum(id,  new SpotifyCallback<Album>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                if(spotifyError.hasErrorDetails()){
                    Log.e(TAG, spotifyError.getErrorDetails().message);
                }
                Toast.makeText(getApplicationContext(),
                        "Error loading content", Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(final Album a, Response response) {
                album = a;


                ImageView iv = findViewById(R.id.image_album);
                setPlaceHolder(iv);
                if(a.images.size() != 0) new DownloadImageTask(iv, getItemSize()).execute(a.images.get(0).url);

                TextView albumName = findViewById(R.id.album_name);
                albumName.setText(a.name);

                TextView artistName = findViewById(R.id.album_artist);
                artistName.setText(a.artists.get(0).name);
                artistName.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        changeActivity(ArtistsActivity.class, a.artists.get(0).id);
                    }
                });

                statsFragment.updateData();
                infoFragment.updateData();
            }
        });
    }
}
