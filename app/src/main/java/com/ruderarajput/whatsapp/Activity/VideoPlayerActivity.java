package com.ruderarajput.whatsapp.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruderarajput.whatsapp.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        progressBar = findViewById(R.id.progressBar);

        getSupportActionBar().hide();

        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);

        Uri videoUri = getIntent().getData();

        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(videoUri);


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // Hide the progress bar when the video is prepared and ready to play
                progressBar.setVisibility(View.GONE);
                videoView.start();
            }
        });

        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

    }
    public void goToChatDetailActivity(View view) {
        Intent intent = new Intent(VideoPlayerActivity.this, ChatDetailActivity.class);
        startActivity(intent);
    }
}