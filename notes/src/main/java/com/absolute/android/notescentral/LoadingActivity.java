package com.absolute.android.notescentral;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;

public class LoadingActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(com.absolute.android.notescentral.utils.SomaPeriod.themeResId(
            com.absolute.android.notescentral.utils.SomaPeriod.resolveId(this)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // The themes paint the status bar with ?attr/somaBg, which is the page
        // white - correct everywhere except here, where it left a white band
        // above the accent splash. Set it to match the ground, and ask for dark
        // status-bar icons because that ground is light in both themes.
        getWindow().setStatusBarColor(
            androidx.core.content.ContextCompat.getColor(this, R.color.soma_splash_bg));
        getWindow().setNavigationBarColor(
            androidx.core.content.ContextCompat.getColor(this, R.color.soma_splash_bg));
        androidx.core.view.WindowInsetsControllerCompat controller =
            new androidx.core.view.WindowInsetsControllerCompat(
                getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        // Gentle fade + scale-in for the mark, instead of appearing instantly.
        View logo = findViewById(R.id.loading_logo);
        View title = findViewById(R.id.loading_title);
        android.view.animation.Animation entrance =
            AnimationUtils.loadAnimation(this, R.anim.splash_mark_entrance);
        logo.startAnimation(entrance);
        title.startAnimation(entrance);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            finish();
        }, SPLASH_DURATION_MS);
    }
}
