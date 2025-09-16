package com.example.pizzaapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class PreparingActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    // Optional: hard timeout if your video is long; adjust to taste
    private static final long FALLBACK_TIMEOUT_MS = 8000; // 8 seconds
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparing);

        playerView = findViewById(R.id.playerView);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.preparing);
        MediaItem item = MediaItem.fromUri(uri);
        player.setMediaItem(item);
        player.prepare();
        player.setPlayWhenReady(true);

        // When playback ends, go to success
        player.addListener(new androidx.media3.common.Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == androidx.media3.common.Player.STATE_ENDED) {
                    goToSuccess();
                }
            }
        });

        // Safety: go to success even if the file is longer or stuck buffering
        handler.postDelayed(this::goToSuccess, FALLBACK_TIMEOUT_MS);
    }

    private void goToSuccess() {
        if (isFinishing() || isDestroyed()) return;
        handler.removeCallbacksAndMessages(null);

        // Pass any order data you need
        Intent i = new Intent(this, OrderSuccessActivity.class);
        // i.putExtra("orderId", getIntent().getStringExtra("orderId"));
        startActivity(i);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
