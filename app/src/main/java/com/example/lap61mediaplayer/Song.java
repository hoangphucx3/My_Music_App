package com.example.lap61mediaplayer;

public class Song {
    private int index;
    private String name;
    private String path;
    private boolean isCrSong;

    public Song(int index, String name, String path) {
        this.index = index;
        this.name = name;
        this.path = path;
        this.isCrSong = false;
    }

    public boolean isCrSong() {
        return isCrSong;
    }

    public void setCrSong(boolean crSong) {
        isCrSong = crSong;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
