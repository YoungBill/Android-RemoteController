package com.example.baina.androidremotecontroller.model;

import android.graphics.Bitmap;

/**
 * Created by baina on 18-1-3.
 */

public class Music {

    private String artist;
    private String title;
    private Bitmap cover;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }
}
