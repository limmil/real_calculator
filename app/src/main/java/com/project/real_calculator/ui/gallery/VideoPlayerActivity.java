package com.project.real_calculator.ui.gallery;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.project.real_calculator.R;

import java.io.File;

public class VideoPlayerActivity extends AppCompatActivity {
    private String tempVideoFile;
    private File videoFile;
    private VideoView videoView;

    private PlayerView playerView;
    private ProgressBar progressBar;
    private ImageView fullscreenButton;
    private SimpleExoPlayer simpleExoPlayer;
    private boolean fullscreenSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prevents Android taking a screenshot when app goes to the background
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        // if file not found
        videoFile = new File(getIntent().getStringExtra("tempVideoFile"));
        if (!videoFile.exists()){finish();}

        setContentView(R.layout.activity_videoplayer);
        playerView = findViewById(R.id.exoplayer);
        progressBar = findViewById(R.id.exo_progress_bar);
        fullscreenButton = findViewById(R.id.exo_fullscreen);

        // make activity fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(VideoPlayerActivity.this);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(
                new AdaptiveTrackSelection.Factory(bandwidthMeter)
        );
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        /*
        MediaSource mediaSource = new ExtractorMediaSource(Uri.fromFile(videoFile),
                new DefaultDataSourceFactory(this,"exo_player"),
                new DefaultExtractorsFactory(),null,null);

         */
        MediaSource mediaSource = new ExtractorMediaSource
                .Factory(new DefaultDataSourceFactory(this,"exo_player"))
                .createMediaSource(Uri.fromFile(videoFile));

        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.addListener(new Player.EventListener(){

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                }else if (playbackState == Player.STATE_READY){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (fullscreenSwitch){
                    fullscreenButton.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_exo_fullscreen, null));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullscreenSwitch = false;
                }else{
                    fullscreenButton.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_exo_fullscreen_exit, null));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fullscreenSwitch = true;
                }
            }
        });
        /*
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoPlayer);
        videoView.setVideoPath(tempVideoFile);

        MediaController mediaController = new MediaController(this);

        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);


        videoView.start();

         */

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoFile.delete();
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        finish();
    }
}
