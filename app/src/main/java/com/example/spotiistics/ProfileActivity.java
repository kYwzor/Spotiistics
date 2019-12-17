package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_playlist);
        Intent intent = getIntent();
        String mAccessToken = intent.getStringExtra("token");
        UserPrivate user = intent.getParcelableExtra("user");
        TextView tv = findViewById(R.id.textView);
        tv.setText("Logged in as " + user.display_name);
        Log.d(TAG, "Here");

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        SpotifyService spotify = api.getService();
        spotify.getPlaylists(user.id, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, "Get Playlists failure: "+ spotifyError.toString());
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                Log.d(TAG,"Get Playlists sucess");
                for (PlaylistSimple playlist:playlistSimplePager.items) {
                    Log.d(TAG,playlist.name);
                }
            }
        });

    }
}
