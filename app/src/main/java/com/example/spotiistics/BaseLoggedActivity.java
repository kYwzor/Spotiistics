package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public abstract class BaseLoggedActivity extends AppCompatActivity {
    private String mAccessToken;
    SpotifyService spotify;
    String id;

    public abstract int getItemSize();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mAccessToken = intent.getStringExtra("token");
        id = intent.getStringExtra("id"); // Might be null but it's ok

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        spotify = api.getService();
    }

    public void onPlaylistsButtonClicked(View view){
        Toast.makeText(getApplicationContext(), "Switching to playlists", Toast.LENGTH_LONG).show();
        changeActivity(UserPlaylistsActivity.class);
    }

    public void onSearchButtonClicked(View view){
        Toast.makeText(getApplicationContext(), "Switching to search", Toast.LENGTH_LONG).show();
        changeActivity(SearchActivity.class);
    }

    public void onSyncButtonClicked(View view){
        Toast.makeText(getApplicationContext(), "Switching to sync", Toast.LENGTH_LONG).show();
        //TODO
        //changeActivity(SyncActivity.class);
    }
    void changeActivity(Class c){
        Intent mIntent = new Intent(getApplicationContext(), c);
        mIntent.putExtra("token", mAccessToken);
        startActivity(mIntent);
    }

    void changeActivity(Class c, String id){
        Intent mIntent = new Intent(getApplicationContext(), c);
        mIntent.putExtra("token", mAccessToken);
        mIntent.putExtra("id", id);
        startActivity(mIntent);
    }

    void setPlaceHolder(ImageView iv) {
        Drawable dr = getResources().getDrawable(R.drawable.noalbum);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, getItemSize(), getItemSize(), true);
        iv.setImageBitmap(resized);
    }
}
