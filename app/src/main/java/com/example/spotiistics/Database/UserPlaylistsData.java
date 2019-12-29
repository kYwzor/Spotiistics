package com.example.spotiistics.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class UserPlaylistsData {
    @NonNull
    @PrimaryKey
    public String id;
    public ArrayList<String> ownPlaylistNames;
    public ArrayList<String> ownPlaylistIds;
    public ArrayList<String> otherPlaylistNames;
    public ArrayList<String> otherPlaylistIds;
    public long timestamp;

    public UserPlaylistsData(@NonNull String id) {
        this.id = id;
        timestamp = System.currentTimeMillis();
    }
}