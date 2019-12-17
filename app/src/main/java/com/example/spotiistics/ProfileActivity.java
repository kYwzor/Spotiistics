package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import kaaes.spotify.webapi.android.models.UserPrivate;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_playlist);
        UserPrivate user = getIntent().getParcelableExtra("user");
        TextView tv = findViewById(R.id.textView);
        tv.setText("Logged in as " + user.display_name);

    }
}
