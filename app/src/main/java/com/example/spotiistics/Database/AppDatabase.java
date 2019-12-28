package com.example.spotiistics.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TrackData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TrackDataDao trackDataDao();
}
