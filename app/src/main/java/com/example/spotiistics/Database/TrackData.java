package com.example.spotiistics.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TrackData {
    @NonNull
    @PrimaryKey
    public String id;
    public String name;
    public String artists;
    public String albumName;
    public String albumdId;
    public String popularity;
    public String duration;
    public String releaseDate;
    public long timestamp;

    public TrackData(String id, String name, String artists, String albumName, String albumdId, String popularity, String duration, String releaseDate) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.albumName = albumName;
        this.albumdId = albumdId;
        this.popularity = popularity;
        this.duration = duration;
        this.releaseDate = releaseDate;
        timestamp = System.currentTimeMillis();
    }
}
