package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;


public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        Intent intent = getIntent();
        final String mAccessToken = intent.getStringExtra("token");

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        final SpotifyService spotify = api.getService();

        View searchButton = findViewById(R.id.search_mag);
        searchButton.setOnClickListener(new View.OnClickListener() {
            EditText et = findViewById(R.id.search_box);
            @Override
            public void onClick(View arg0) {
                spotify.searchTracks(et.getText().toString(), new SpotifyCallback<TracksPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                    }

                    @Override
                    public void success(TracksPager tracks, Response response) {
                        Toast.makeText(SearchActivity.this,
                                String.valueOf(response.getStatus()), Toast.LENGTH_LONG).show();
                        displaySearch(tracks);
                    }
                });
            }
        });

    }

    private void displaySearch(TracksPager tracks) {
        Pager<Track> tracksPager = tracks.tracks;
        LinearLayout LL = findViewById(R.id.results);

        for (Track t : tracksPager.items) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText(t.name);
            LL.addView(textView);
        }
    }
}
