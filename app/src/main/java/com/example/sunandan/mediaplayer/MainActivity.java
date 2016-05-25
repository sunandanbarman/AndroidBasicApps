package com.example.sunandan.mediaplayer;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * I gratefully acknowledge the video tutorial of "profgustin" (Video link https://www.youtube.com/watch?v=W1JE-jUisVU)
 * which I have used to create this project.
 * Other inspiration I used is ACADGILD ( link : https://www.youtube.com/channel/UCaQfgvMsjpImSxrJQDBjd-Q)
 * from their video at https://www.youtube.com/watch?v=b_-9_ekEfFk
 *
 * Thank you !
 */
class Mp3Filter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {
        return filename.endsWith(".mp3");
    }
}

public class MainActivity extends ListActivity {
    private static Context appContext;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static int songCounter = 0;

    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
    };

    private static boolean bIsSongsPresent = false;
    private static List<Song> songsList;
    private static Handler mHandler;
    private static int seekBarInterval = 10;
    /*GUI elements*/
    private static TextView songCurrentDurationLabel;
    private static TextView songTotalDurationLabel;
    public static ImageButton btnStop, btnPlayPause, btnNext, btnPrev;
    public static SeekBar seekBar;
    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
    /**/
    public static void getNextSongCounter() {
        songCounter = (songCounter+ 1 ) % songsList.size();
    }
    /**/
    public static void getPrevSongCounter() {
        songCounter = (songCounter -1) % songsList.size();
        if (songCounter < 0) {
            songCounter = songsList.size()-1; //roll over
        }
    }
    /**/
    public static Context getAppContext() {
        return MainActivity.appContext;
    }
    /**
     * Method to play a song, this is used by both the listItem onClick and next,prev buttons to play the songs
     * */
    public static void playSong(int songIndex) {
        try {
            mediaPlayer.reset();
            songCounter = songIndex;
            seekBar.setProgress(0);
            seekBar.setMax(100);
            mediaPlayer.setDataSource(songsList.get(songIndex).getmSongFullPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            //update progress bar
            updateProgressBar();
            MainActivity.btnPlayPause.setImageResource(R.drawable.ic_pause_white_18dp);
            showToast("Played song at index " + songIndex);
        } catch(IOException e) {
            e.printStackTrace();
        }


    }

    public static void showToast(String message) {
        Toast toast = Toast.makeText(getAppContext(),message,Toast.LENGTH_LONG);
        toast.show();
    }

    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
    private void createEventHandlers() {
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                seekBar.setProgress(0);
                btnPlayPause.setImageResource(R.drawable.ic_play_arrow_white_18dp);
            }
        });
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    //btnPlayPause.setText(getString(R.string.btn_play));
                    btnPlayPause.setImageResource(R.drawable.ic_play_arrow_white_18dp);
                    //btnPlayPause.setBackgroundResource(R.drawable.ic_play_arrow_white_18dp);
                } else {
                    mediaPlayer.start();
                    btnPlayPause.setImageResource(R.drawable.ic_pause_white_18dp);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextSongCounter();
                Log.e("Next","songCounter " + songCounter);
                playSong(songCounter);

            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrevSongCounter();
                Log.e("Prev","songCounter " + songCounter);
                playSong(songCounter);
            }
        });
        try {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // remove message Handler from updating progress bar
                    mHandler.removeCallbacks(mSeekBarUpdateTask);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mHandler.removeCallbacks(mSeekBarUpdateTask);
                    int totalDuration = mediaPlayer.getDuration();
                    int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);

                    // forward or backward to certain seconds
                    mediaPlayer.seekTo(currentPosition);
                    // update timer progress again
                    updateProgressBar();
                }
            });
        } catch(Exception e) {
            showToast(e.getMessage());
        }
        /*Play next song automatically*/
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                getNextSongCounter();
                playSong(songCounter);
            }
        });

    }

    private void setButtonState(boolean enable) {
        btnStop.setClickable(enable);
        btnPlayPause.setClickable(enable);
        btnNext.setClickable(enable);
        btnPrev.setClickable(enable);

        btnStop.setEnabled(enable);
        btnPlayPause.setEnabled(enable);
        btnNext.setEnabled(enable);
        btnPrev.setEnabled(enable);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            MainActivity.appContext = getApplicationContext();
            songsList = new ArrayList<>();
            mHandler= new Handler();
            songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel) ;
            songTotalDurationLabel   = (TextView) findViewById(R.id.songTotalDurationLabel);
            btnStop = (ImageButton) findViewById(R.id.btnStop);
            btnPlayPause = (ImageButton) findViewById(R.id.btnPause);
            btnNext = (ImageButton) findViewById(R.id.btnNext);
            btnPrev = (ImageButton) findViewById(R.id.btnPrev);

            setButtonState(false);
            seekBar = (SeekBar) findViewById(R.id.seekBar);

            updatePlayList();
            createEventHandlers();

            int width = this.getResources().getDisplayMetrics().widthPixels;
            btnStop.setMaxWidth(width / 4 );
            btnPlayPause.setMaxWidth(width / 4);
            btnNext.setMaxWidth(width /4);
            btnPrev.setMaxWidth(width /4);
        } catch(Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(), ex.getMessage(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void updatePlayList() {
        try {
            /*File home = new File(SD_PATH);
            if (home.listFiles(new Mp3Filter()).length > 0) {
                for(File f:home.listFiles(new Mp3Filter())) {
                    songs.add(f.getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.song_item,songs);
                setListAdapter(arrayAdapter);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),"No files",Toast.LENGTH_SHORT);
                toast.show();
            }*/
            ContentResolver contentResolver = getContentResolver();
            //Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
            Cursor cur = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    selection, null, sortOrder);

            int count = 0;
            if (cur.moveToFirst()) {

                int dispNameColIndex = cur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int albumColIndex = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int fullPathIndex = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
                int durationIndex = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int IDIndex = cur.getColumnIndex(MediaStore.Audio.Media._ID);
                String songName;
                do {

                    Song song = new Song();
                    songName  = cur.getString(dispNameColIndex);
                    song.setmSongName(songName.substring(0,songName.length()-4));
                    //song.setmSongName(song.getmSongName().substring(0,song.get));
                    song.setmSongAlbumName(cur.getString(albumColIndex));
                    Log.e("songName",song.getmSongName()) ;
                    song.setmSongFullPath(cur.getString(fullPathIndex));
                    song.setmSongDuration(cur.getString(durationIndex));
                    song.setID(cur.getString(IDIndex));

                    Log.e("SongDetails",song.getmSongName()
                            + ":" + song.getmSongAlbumName()
                            + ":" + song.getmSongDuration()
                            + ":" + song.getmSongFullPath());

                    songsList.add(song);
                } while (cur.moveToNext());

                if (songsList.size() >0) {
                    setButtonState(true);
                }
                /*Attach custom adapter to the listView*/
                SongAdapter adapter = new SongAdapter(this, songsList);
                // Attach the adapter to a ListView
                getListView().setAdapter(adapter);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.no_songs), Toast.LENGTH_LONG);
                toast.show();
            }
            cur.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }

    @Override
    protected void onListItemClick(ListView List, View view, int position, long id) {
        playSong(position);
    }

    /*Launch the progress bar update after 100 ms*/
    private static void updateProgressBar() {
        mHandler.postDelayed(mSeekBarUpdateTask,seekBarInterval);
    }
    /*Code referred from http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/*/
    /*Updates the seekbar every 10 ms*/
    private static Runnable mSeekBarUpdateTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            //Displaying Total Duration time
            songTotalDurationLabel.setText(""+ Utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+ Utilities.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = Utilities.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 10 milliseconds
            mHandler.postDelayed(this, seekBarInterval);
        }
    };
}
