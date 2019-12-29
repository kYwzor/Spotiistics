package com.example.spotiistics.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PlaylistDataDao {
    @Insert
    void insert(PlaylistData playlistData);

    @Update
    void update(PlaylistData playlistData);

    @Query("SELECT * FROM PlaylistData WHERE id = :playlistId")
    PlaylistData[] get(String playlistId);
}
