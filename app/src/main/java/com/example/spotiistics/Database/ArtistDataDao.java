package com.example.spotiistics.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ArtistDataDao {
    @Insert
    void insert(ArtistData artistData);

    @Update
    void update(ArtistData artistData);

    @Query("SELECT * FROM ArtistData WHERE id = :artistId")
    ArtistData[] get(String artistId);
}
