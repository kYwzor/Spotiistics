package com.example.spotiistics.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TrackDataDao {
    @Insert
    void insert(TrackData trackData);

    @Update
    void update(TrackData trackData);

    @Query("SELECT * FROM TrackData WHERE id = :trackId")
    TrackData[] get(String trackId);
}
