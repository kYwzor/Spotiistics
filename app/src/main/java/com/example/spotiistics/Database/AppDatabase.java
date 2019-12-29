package com.example.spotiistics.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {TrackData.class, AlbumData.class, ArtistData.class, PlaylistData.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract TrackDataDao trackDataDao();
    public abstract AlbumDataDao albumDataDao();
    public abstract ArtistDataDao artistDataDao();
    public abstract PlaylistDataDao playlistDataDao();
}
