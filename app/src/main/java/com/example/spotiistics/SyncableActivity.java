package com.example.spotiistics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
            //Toast.makeText(getApplicationContext(), "Syncing started", Toast.LENGTH_SHORT).show();
            startSync();
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.already_syncing, Toast.LENGTH_SHORT).show();
        }
    }

    void onSyncDone(){
        //Toast.makeText(getApplicationContext(), "Syncing done", Toast.LENGTH_SHORT).show();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sync)
                .setLargeIcon(bm)
                .setContentTitle(getResources().getString(R.string.sync_done))
                .setContentText(getResources().getString(R.string.sync_done_context))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}
