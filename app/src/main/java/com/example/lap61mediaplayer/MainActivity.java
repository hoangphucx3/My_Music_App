package com.example.lap61mediaplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SendSelectSong {

    private TextView tvSongName, tvTimeRun, tvTimeFull;
    private ImageButton ibtnBack, ibtnNext, ibtnStop, ibtnPlay;
    private SeekBar skbTime;
    private ArrayList<Song> listSong;
    private SongAdapter adapter;
    private MediaPlayer mediaPlayer;
    private int crSongIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initView();
        setOnClickMediaButton();
    }

    private void setOnClickMediaButton() {

        ibtnPlay.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                // Dang phat nhac
                mediaPlayer.pause();
                ibtnPlay.setImageResource(R.drawable.ic_play);
            } else {
                // Dang stop/pause
                playCrSong();
            }
        });

        ibtnStop.setOnClickListener(view -> {
            mediaPlayer.stop();
            setCrSong();
            ibtnPlay.setImageResource(R.drawable.ic_play);
        });

        ibtnBack.setOnClickListener(view -> {
            mediaPlayer.stop();
            crSongIndex = (crSongIndex == 0) ? (listSong.size() - 1) : (--crSongIndex);
            setCrSong();
            playCrSong();
        });

        ibtnNext.setOnClickListener(view -> {
            mediaPlayer.stop();
            crSongIndex = (crSongIndex == (listSong.size() - 1)) ? 0 : ++crSongIndex;
            setCrSong();
            playCrSong();
        });
    }

    private void setCrSong() {
        mediaPlayer.reset();
        Song crSong = listSong.get(crSongIndex);
        try {
            File file = new File(crSong.getPath());
            Uri uri = Uri.fromFile(file);
            mediaPlayer.setDataSource(getApplicationContext(),uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvSongName.setText(crSong.getName());
        int timeSong = mediaPlayer.getDuration();
        tvTimeFull.setText(String.format("%02d:%02d", timeSong / 60000, (timeSong / 1000) % 60));
        skbTime.setMax(timeSong);
        skbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void playCrSong() {
        mediaPlayer.start();
        ibtnPlay.setImageResource(R.drawable.ic_pause);
        for (int i=0;i<listSong.size();i++) {
            listSong.get(i).setCrSong(false);
        }
        listSong.get(crSongIndex).setCrSong(true);
        adapter.notifyDataSetChanged();
        updateTimeSong();
        autoNextSong();
    }

    private void updateTimeSong() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int crTimeSong = mediaPlayer.getCurrentPosition();
                tvTimeRun.setText(String.format("%02d:%02d", crTimeSong / 60000, (crTimeSong / 1000) % 60));
                skbTime.setProgress(crTimeSong);
                autoNextSong();
                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void autoNextSong() {
        if (skbTime.getProgress() >= skbTime.getMax()) {
            crSongIndex = (crSongIndex == (listSong.size() - 1)) ? 0 : ++crSongIndex;
            setCrSong();
            mediaPlayer.start();
            for (int i=0;i<listSong.size();i++) {
                listSong.get(i).setCrSong(false);
            }
            listSong.get(crSongIndex).setCrSong(true);
            adapter.notifyDataSetChanged();
        }
    }

    private void addListSong() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cursor = getContentResolver().query(songUri, null, null, null);
        }
        if (cursor != null) {
            cursor.moveToFirst();
            listSong.clear();
            int index = 0;
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                listSong.add(new Song(index++, name, path));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private void initView() {
        tvSongName = findViewById(R.id.tvSongName);
        tvTimeRun = findViewById(R.id.tvTimeRun);
        tvTimeFull = findViewById(R.id.tvTimeFull);
        ibtnBack = findViewById(R.id.ibtnBack);
        ibtnNext = findViewById(R.id.ibtnNext);
        ibtnStop = findViewById(R.id.ibtnStop);
        ibtnPlay = findViewById(R.id.ibtnPlay);
        skbTime = findViewById(R.id.skbTime);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        RecyclerView recyclerView = findViewById(R.id.listSong);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        listSong = new ArrayList<>();
        addListSong();
        adapter = new SongAdapter(listSong, MainActivity.this);
        recyclerView.setAdapter(adapter);
        setCrSong();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 112);
        }
    }


    @Override
    public void SetData(Song song) {
        crSongIndex = song.getIndex();
        mediaPlayer.stop();
        setCrSong();
        playCrSong();
    }

//    private void addListSong() {
//        String[] arrSongName = getResources().getStringArray(R.array.list_song);
//        String[] arrSongNameVN = getResources().getStringArray(R.array.list_song_vn);
//        int index=0;
//        for (int i=0;i< arrSongName.length;i++) {
//            listSong.add(new Song(index++, arrSongName[i], arrSongNameVN[i],
//                    getResources().getIdentifier(arrSongName[i],"raw",getPackageName())));
//        }
//    }

}