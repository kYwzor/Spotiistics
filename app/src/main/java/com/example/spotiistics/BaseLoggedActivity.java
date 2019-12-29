package com.example.spotiistics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class BaseLoggedActivity extends BaseActivity {
    private static final String TAG = BaseLoggedActivity.class.getSimpleName();
    protected String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id"); // Might be null but it's ok
    }

    public void onPlaylistsButtonClicked(View view){
        //Toast.makeText(getApplicationContext(), "Switching to playlists", Toast.LENGTH_LONG).show();
        changeActivity(UserPlaylistsActivity.class);
    }

    public void onSearchButtonClicked(View view){
        //Toast.makeText(getApplicationContext(), "Switching to search", Toast.LENGTH_LONG).show();
        changeActivity(SearchActivity.class);
    }

    public void onSyncButtonClicked(View view){
        // Has no effect it it's not a SyncableActivity
    }
    void changeActivity(Class c){
        Intent mIntent = new Intent(getApplicationContext(), c);
        startActivity(mIntent);
    }

    void changeActivity(Class c, String id){
        Intent mIntent = new Intent(getApplicationContext(), c);
        mIntent.putExtra("id", id);
        startActivity(mIntent);
    }

    public void saveBitmap(String fileName, Bitmap bitmap) {
        FileOutputStream outputStream;
        try {
            outputStream = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Writing bitmap error", e);
        }
    }

    public void loadBitmap(String fileName, ImageView iv){
        FileInputStream inputStream;
        try {
            inputStream = getApplicationContext().openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            iv.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Reading bitmap error", e);
        }
    }
}
