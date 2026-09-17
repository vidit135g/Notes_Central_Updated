/*
 * Copyright (C) 2013-2026 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.absolute.android.notescentral;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Locale;
import java.util.Random;

/**
 * Full-screen voice-note recorder, replicating the Soma mockup's dedicated
 * recording sheet (big timer, live waveform, pulsing record button) in place
 * of routing the mic quick-create icon through the generic attachments
 * popup. Recording itself is still done by DetailFragment's MediaRecorder
 * (beginAudioRecording/finishAudioRecordingAndAttach/cancelAudioRecording) -
 * this class only owns the presentation and the amplitude-polling animation.
 */
public class VoiceRecordDialogFragment extends DialogFragment {

  private static final int BAR_COUNT = 28;
  private static final int TICK_MS = 90;

  private final Handler handler = new Handler(Looper.getMainLooper());
  private final Random random = new Random();
  private View[] bars;
  private TextView timerView;
  private long recordingStartElapsed;
  private boolean saved = false;
  private ObjectAnimator ringAnimator;

  private final Runnable tick = new Runnable() {
    @Override
    public void run () {
      DetailFragment host = getHostFragment();
      if (host == null || !isAdded()) {
        return;
      }
      updateTimer();
      updateWaveform(host.getRecordingAmplitude());
      handler.postDelayed(this, TICK_MS);
    }
  };


  private DetailFragment getHostFragment () {
    if (getParentFragment() instanceof DetailFragment) {
      return (DetailFragment) getParentFragment();
    }
    return null;
  }


  @Override
  public void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_FRAME, 0);
  }


  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialog_voice_record, container, false);
  }


  @Override
  public void onStart () {
    super.onStart();
    Window window = getDialog() != null ? getDialog().getWindow() : null;
    if (window != null) {
      window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
  }


  @Override
  public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    timerView = view.findViewById(R.id.voice_timer);
    LinearLayout waveRow = view.findViewById(R.id.voice_wave);
    buildBars(waveRow);

    View ring = view.findViewById(R.id.voice_record_ring);
    ringAnimator = ObjectAnimator.ofPropertyValuesHolder(ring,
        android.animation.PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.45f),
        android.animation.PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.45f),
        android.animation.PropertyValuesHolder.ofFloat(View.ALPHA, 0.9f, 0f));
    ringAnimator.setDuration(1400);
    ringAnimator.setRepeatCount(ValueAnimator.INFINITE);
    ringAnimator.setInterpolator(new LinearInterpolator());
    ringAnimator.start();

    view.findViewById(R.id.voice_cancel).setOnClickListener(v -> {
      saved = false;
      dismiss();
    });
    view.findViewById(R.id.voice_save).setOnClickListener(v -> {
      saved = true;
      dismiss();
    });

    DetailFragment host = getHostFragment();
    if (host != null) {
      host.beginAudioRecording(() -> {
        recordingStartElapsed = SystemClock.elapsedRealtime();
        handler.post(tick);
      });
    }
  }


  private void buildBars (LinearLayout waveRow) {
    bars = new View[BAR_COUNT];
    int barWidth = (int) (4 * getResources().getDisplayMetrics().density);
    int barMargin = (int) (3 * getResources().getDisplayMetrics().density);
    for (int i = 0; i < BAR_COUNT; i++) {
      View bar = new View(getContext());
      bar.setBackgroundResource(R.drawable.bg_soma_wave_bar);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(barWidth, barWidth * 3);
      lp.setMarginEnd(barMargin);
      lp.gravity = android.view.Gravity.CENTER_VERTICAL;
      waveRow.addView(bar, lp);
      bars[i] = bar;
    }
  }


  private void updateTimer () {
    if (timerView == null) {
      return;
    }
    long seconds = (SystemClock.elapsedRealtime() - recordingStartElapsed) / 1000;
    timerView.setText(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));
  }


  private void updateWaveform (int amplitude) {
    if (bars == null) {
      return;
    }
    // MediaRecorder amplitude tops out well under Short.MAX_VALUE for normal speech;
    // normalize generously so quiet input still animates instead of sitting flat.
    float level = Math.min(1f, amplitude / 9000f);
    float minPx = 6 * getResources().getDisplayMetrics().density;
    float maxPx = 90 * getResources().getDisplayMetrics().density;
    long now = SystemClock.elapsedRealtime();
    for (int i = 0; i < bars.length; i++) {
      double phase = now / 140.0 + i * 0.6;
      float variance = (float) (0.35 + 0.65 * Math.abs(Math.sin(phase)));
      float px = minPx + (maxPx - minPx) * level * variance;
      ViewGroup.LayoutParams lp = bars[i].getLayoutParams();
      lp.height = (int) px;
      bars[i].setLayoutParams(lp);
    }
  }


  @Override
  public void onDismiss (@NonNull DialogInterface dialog) {
    handler.removeCallbacks(tick);
    if (ringAnimator != null) {
      ringAnimator.cancel();
    }
    DetailFragment host = getHostFragment();
    if (host != null) {
      if (saved) {
        host.finishAudioRecordingAndAttach();
      } else {
        host.cancelAudioRecording();
      }
    }
    super.onDismiss(dialog);
  }
}
