package com.example.spotiistics;

import android.view.View;
import android.widget.Toast;

public abstract class SyncableActivity extends BaseLoggedActivity {
    abstract void startSync();
    abstract boolean isReady();

    @Override
    public void onSyncButtonClicked(View view) {
        if (isReady()){
            Toast.makeText(getApplicationContext(), "Syncing started", Toast.LENGTH_SHORT).show();
            startSync();
        }
        else {
            Toast.makeText(getApplicationContext(), "Not ready to sync", Toast.LENGTH_SHORT).show();
        }
    }

    void onSyncDone(){
        Toast.makeText(getApplicationContext(), "Syncing done", Toast.LENGTH_SHORT).show();
    }
}
