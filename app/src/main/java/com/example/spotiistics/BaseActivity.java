package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;

public abstract class BaseActivity extends AppCompatActivity {
    protected static SpotifyService spotify;
    protected static UserPrivate user;
}
