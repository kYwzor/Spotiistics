package com.example.spotiistics.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class ArtistData {
    @NonNull
    @PrimaryKey
    public String id;
    public String name;
    public int n_followers;
    public int popularity;
    public boolean isFollowing;
    public ArrayList<String> albumNames;
    public ArrayList<String> albumIds;
    public ArrayList<String> topTrackNames;
    public ArrayList<String> topTrackIds;
    public ArrayList<String> topTrackAlbums;
    public ArrayList<String> genres;
    public long timestamp;

    public ArtistData(@NonNull String id) {
        this.id = id;
        timestamp = System.currentTimeMillis()/1000;
    }
}
