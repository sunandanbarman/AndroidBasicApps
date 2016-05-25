package com.example.sunandan.mediaplayer;

/**
 * Created by sunandan on 5/19/16.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Used code from :-
 * 1. https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 * 2. https://www.caveofprogramming.com/guest-posts/custom-listview-with-imageview-and-textview-in-android.html
 */

/**
 * 1. To use a custom adapter (i.e. adapter for user defined classes, we need to override the getView method, where
 * we define the way to add the custom objects
 * 2. Plus, the default constructor needs to be called too
 * 3. Use a ViewHolder to speed up the populating the listView items
 *
 */
public class SongAdapter extends ArrayAdapter<Song> {
    //private List<Song> songs;
    private Song song;
    private int song_pos;
    //View lookup cache

    private static class ViewHolder {
        TextView songName;
        TextView songAlbumName;
        TextView songDuration;
    }
    public SongAdapter(Context context,List<Song> songs) {
        //this.songs = null;
        super(context,0,songs);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Get data item for this position
        song_pos = position;
        song = getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView             = inflater.inflate(R.layout.song_item, parent, false);
            viewHolder.songName     = (TextView) convertView.findViewById(R.id.tvSongName);
            viewHolder.songAlbumName= (TextView) convertView.findViewById(R.id.tvSongAlbum);
            viewHolder.songDuration = (TextView) convertView.findViewById(R.id.txt_listitem_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.songName.setText(song.getmSongName());
        viewHolder.songAlbumName.setText(song.getmSongAlbumName());
        viewHolder.songDuration.setText(Utilities.milliSecondsToTimer(Long.valueOf(song.getmSongDuration())));
        // Return the completed view to render on screen
        /*Generate onClick event for the view*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.playSong(position);

            }
        });
        return convertView;
    }
}
