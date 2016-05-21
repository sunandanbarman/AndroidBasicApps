package com.example.sunandan.mediaplayer;

/**
 * Created by sunandan on 5/19/16.
 */
public class Song {

    private String mSongName, mSongAlbumName, mSongFullPath, mSongDuration;
    private String ID;
    Song() {

    }
    Song(String songName, String songAlbumName, String songFullPath, String songDuration) {
        this.setmSongName(songName);
        this.setmSongAlbumName(songAlbumName);
        this.setmSongFullPath(songFullPath);
        this.setmSongDuration(songDuration);
    }
    public String getmSongName() {
        return mSongName;
    }

    public void setmSongName(String mSongName) {
        this.mSongName = mSongName;
    }

    public String getmSongAlbumName() {
        return mSongAlbumName;
    }

    public void setmSongAlbumName(String mSongAlbumName) {
        this.mSongAlbumName = mSongAlbumName;
    }

    public String getmSongFullPath() {
        return mSongFullPath;
    }

    public void setmSongFullPath(String mSongFullPath) {
        this.mSongFullPath = mSongFullPath;
    }

    public String getmSongDuration() {
        return mSongDuration;
    }

    public void setmSongDuration(String mSongDuration) {
        this.mSongDuration = mSongDuration;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
