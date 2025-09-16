package com.example.pizzaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PreparingActivity extends AppCompatActivity {

    private static final long DELAY_MS = 2500; // 2.5s spinner time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparing);

        ImageView gifView = findViewById(R.id.gifView);

        // If your GIF is in res/raw (as in your screenshot)
        Glide.with(this).asGif().load(R.raw.order_processing).into(gifView);
        // (If you move it to res/drawable/, use R.drawable.order_processing)

        // After a short delay, go to success screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(PreparingActivity.this, OrderSuccessActivity.class);
            // Optional: pass any extras
            // i.putExtra("orderId", orderId);
            startActivity(i);
            finish(); // remove Processing screen from back stack
        }, DELAY_MS);
    }
}
