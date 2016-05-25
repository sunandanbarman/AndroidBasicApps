package com.example.sunandan.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by sunandan on 5/21/16.
 */
public class SongService extends Service {

    private MediaPlayer mediaPlayer;
    private int songCounter;
    private List<Song> songsList;
    private Uri songUri;
    /*Intent action types*/
    private final String ACTION_STOP     = "com.example.sunandan.mediaplayer.STOP";
    private final String ACTION_NEXT     = "com.example.sunandan.mediaplayer.NEXT";
    private final String ACTION_PREVIOUS = "com.example.sunandan.mediaplayer.PREVIOUS";
    private final String ACTION_PAUSE    = "com.example.sunandan.mediaplayer.PAUSE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*Call appropiate method of service based on Intent*/
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equalsIgnoreCase(ACTION_PAUSE)) {
                    playPauseSong();
                } else if (action.equalsIgnoreCase(ACTION_STOP)) {
                    //stopSong();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        initPlayer();
        createMediaPlayerEvents();
    }
    /*%%%%%%%%%%%%%%%%%%HELPER FUNCTIONS START%%%%%%%%%%%%%%%%%%%%%%%%*/
    /**
     * Method to play a song, this is used by both the listItem onClick and next,prev buttons to play the songs
     * */
    private void playSong(int songIndex) {
//        try {
//            mediaPlayer.reset();
//            songCounter = songIndex;
//            seekBar.setProgress(0);
//            seekBar.setMax(100);
//            mediaPlayer.setDataSource(songsList.get(songIndex).getmSongFullPath());
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            //update progress bar
//            updateProgressBar();
//            MainActivity.btnPlayPause.setImageResource(R.drawable.ic_pause_white_18dp);
//            showToast("Played song at index " + songIndex);
//        } catch(IOException e) {
//            e.printStackTrace();
//        }


    }

    private void getNextSongCounter() {
        songCounter = (songCounter+ 1 ) % songsList.size();
    }
    /**/
    private void getPrevSongCounter() {
        songCounter = (songCounter -1) % songsList.size();
        if (songCounter < 0) {
            songCounter = songsList.size()-1; //roll over
        }
    }

    private void initPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
    private void createMediaPlayerEvents() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                getNextSongCounter();
                try {
                    mediaPlayer.setDataSource(songsList.get(songCounter).getmSongFullPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    private void playPauseSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }
}
