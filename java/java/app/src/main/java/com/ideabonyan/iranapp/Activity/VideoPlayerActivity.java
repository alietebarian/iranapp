package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.ShowToast;

/**
 * Fullscreen playback of an ad's video. Opened from ShowAdActivity, which is locked to
 * portrait; this one follows the sensor and hands the playback position back on exit.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "video_url";
    public static final String EXTRA_POSITION = "video_position";
    private static final String STATE_PLAY_WHEN_READY = "play_when_ready";

    private PlayerView playerView;
    private ExoPlayer player;
    private String url;
    private long position;
    private boolean playWhenReady = true;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.videoPlayerView);
        url = getIntent().getStringExtra(EXTRA_URL);
        position = getIntent().getLongExtra(EXTRA_POSITION, 0);
        if (savedInstanceState != null) {
            position = savedInstanceState.getLong(EXTRA_POSITION, position);
            playWhenReady = savedInstanceState.getBoolean(STATE_PLAY_WHEN_READY, true);
        }

        // The same button that opened fullscreen closes it.
        playerView.setFullscreenButtonClickListener(isFullScreen -> finish());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), playerView);
        insetsController.hide(WindowInsetsCompat.Type.systemBars());
        insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        player = new ExoPlayer.Builder(this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                ShowToast.failure("پخش ویدیو امکان پذیر نیست", VideoPlayerActivity.this);
            }
        });
        playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(url), position);
        player.setPlayWhenReady(playWhenReady);
        player.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            position = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
            playerView.setPlayer(null);
            player.release();
            player = null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_POSITION, player != null ? player.getCurrentPosition() : position);
        outState.putBoolean(STATE_PLAY_WHEN_READY, player != null ? player.getPlayWhenReady() : playWhenReady);
    }

    @Override
    public void finish() {
        Intent result = new Intent();
        result.putExtra(EXTRA_POSITION, player != null ? player.getCurrentPosition() : position);
        setResult(RESULT_OK, result);
        super.finish();
    }
}
