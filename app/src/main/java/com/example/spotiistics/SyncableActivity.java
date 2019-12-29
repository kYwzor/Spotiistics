package com.example.spotiistics;

import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
        //Toast.makeText(getApplicationContext(), "Syncing done", Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sync)
                .setContentTitle("Sync done")
                .setContentText("Spotiistics syncronization is finished")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}
