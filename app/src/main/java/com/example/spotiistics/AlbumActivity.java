package com.example.spotiistics;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.client.Response;

public class AlbumActivity extends BaseLoggedActivity {
    private static final String TAG = AlbumActivity.class.getSimpleName();
    TabLayout tabLayout;
    Album album;
    private ItemFragment statsFragment;
    private ItemFragment infoFragment;

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

        spotify.getAlbum(id,  new SpotifyCallback<Album>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, spotifyError.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error loading content", Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(final Album a, Response response) {
                album = a;


                ImageView iv = findViewById(R.id.image_album);
                if(a.images.size() != 0) {
                    // new DownloadImageTask(iv).execute(a.images.get(0).url);
                    Glide
                            .with(getApplicationContext())
                            .load(a.images.get(0).url)
                            .placeholder(R.drawable.noalbum)
                            .into(iv);
                }

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
