package com.example.mp3project;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class MusicData {
    private String id;
    private String artists;
    private String title;
    private String albumArt;
    private String duration;
    private int count;
    private int liked;

    public MusicData(String id, String artists, String title, String albumArt, String duration,int count, int liked) {
        this.id = id;
        this.artists = artists;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
        this.count = count;
        this.liked = liked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj instanceof MusicData) {
            MusicData data = (MusicData) obj;
            equal = (this.id).equals(data.getId());
        }

        return equal;
    }
}
