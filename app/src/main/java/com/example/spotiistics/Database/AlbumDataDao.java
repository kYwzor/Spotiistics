package com.example.spotiistics.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AlbumDataDao {
    @Insert
    void insert(AlbumData albumData);

    @Update
    void update(AlbumData albumData);

    @Query("SELECT * FROM AlbumData WHERE id = :albumId")
    AlbumData[] get(String albumId);
}
