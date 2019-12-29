package com.example.spotiistics.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserPlaylistsDataDao {
    @Insert
    void insert(UserPlaylistsData userPlaylistsData);

    @Update
    void update(UserPlaylistsData userPlaylistsData);

    @Query("SELECT * FROM UserPlaylistsData WHERE id = :userId")
    UserPlaylistsData[] get(String userId);
}
