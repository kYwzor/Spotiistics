package com.example.spotiistics.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class PlaylistData {
    @NonNull
    @PrimaryKey
    public String id;
    public String name;
    public String owner;
    public ArrayList<String> trackNames;
    public ArrayList<String> trackIds;
    public ArrayList<String> trackAlbums;
    public long totalDuration;
    public long meanDuration;
    public long minDuration;
    public long maxDuration;
    public float meanTempo;
    public float meanMood;
    public ArrayList<String> topArtistNames;
    public ArrayList<String> topArtistIds;
    public ArrayList<String> topAlbumNames;
    public ArrayList<String> topAlbumIds;
    public long timestamp;

    public PlaylistData(@NonNull String id) {
        this.id = id;
        timestamp = System.currentTimeMillis();
    }
}
