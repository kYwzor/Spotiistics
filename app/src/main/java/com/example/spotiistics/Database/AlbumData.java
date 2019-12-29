package com.example.spotiistics.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class AlbumData {
    @NonNull
    @PrimaryKey
    public String id;
    public String name;
    public String artistName;
    public String artistId;
    public String releaseDate;
    public ArrayList<String> trackNames;
    public ArrayList<String> trackIds;
    public long totalDuration;
    public long meanDuration;
    public long minDuration;
    public long maxDuration;
    public float meanTempo;
    public float meanMood;
    public long timestamp;

    public AlbumData(@NonNull String id, String name, String artistName, String artistId, String releaseDate, ArrayList<String> trackNames, ArrayList<String> trackIds, long totalDuration, long meanDuration, long minDuration, long maxDuration, float meanTempo, float meanMood) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.artistId = artistId;
        this.releaseDate = releaseDate;
        this.trackNames = trackNames;
        this.trackIds = trackIds;
        this.totalDuration = totalDuration;
        this.meanDuration = meanDuration;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.meanTempo = meanTempo;
        this.meanMood = meanMood;
        timestamp = System.currentTimeMillis();
    }
}
