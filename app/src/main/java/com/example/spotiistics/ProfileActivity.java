package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        JSONObject json_object;
        try {
            json_object = new JSONObject(getIntent().getStringExtra("object"));
            TextView tv = findViewById(R.id.textView);
            tv.setText("Logged in as " + json_object.getString("display_name"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
