package com.example.spotiistics;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import retrofit.client.Response;

public class UserPlaylistsActivity extends BaseLoggedActivity {
    private static final String TAG = UserPlaylistsActivity.class.getSimpleName();

    @Override
    public void onPlaylistsButtonClicked(View view){
        // Override to do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_playlist);

        Map<String, Object> options  = new HashMap<>();
        options.put("limit", 50);
        spotify.getMyPlaylists(options, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(UserPlaylistsActivity.this,
                        "Error loading content", Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                addImagesToGallery(playlistSimplePager);
            }
        });
    }

    private void addImagesToGallery(Pager<PlaylistSimple> playlistSimplePager) {
        LinearLayout yourPlaylist = findViewById(R.id.yourPlaylist);
        LinearLayout followedPlaylist = findViewById(R.id.followedPlaylist);

        playlistClickListener playlistClickListener = new playlistClickListener();
        for (PlaylistSimple playlist : playlistSimplePager.items) {
            LinearLayout ll = createLinearLayout(playlist.name, playlist.id);
            ll.setOnClickListener(playlistClickListener);

            //new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(playlist.images.get(0).url);
            if(playlist.images.size() != 0) {
                Glide
                        .with(this)
                        .load(playlist.images.get(0).url)
                        .placeholder(R.drawable.noalbum)
                        .into((ImageView) ll.getChildAt(0));
            }

            if(playlist.owner.id.equals(user.id)) yourPlaylist.addView(ll);
            else followedPlaylist.addView(ll);
        }
    }

    public class playlistClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            changeActivity(PlaylistActivity.class, (String) v.getTag(R.id.ID));
        }
    }

}
