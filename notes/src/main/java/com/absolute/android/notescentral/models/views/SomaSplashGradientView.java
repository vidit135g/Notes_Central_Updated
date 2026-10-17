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
package com.absolute.android.notescentral.models.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import com.absolute.android.notescentral.R;
import android.view.animation.LinearInterpolator;

/**
 * Splash-screen background: a soft white-to-yellow diagonal sheen that slowly sweeps across the
 * screen, in the spirit of an iOS launch/loading gradient (smooth, minimal, no spinner). One
 * continuously-repeating ValueAnimator drives a moving LinearGradient - no bitmaps involved.
 */
public class SomaSplashGradientView extends View {

  private static final long SWEEP_DURATION_MS = 3200;

  /** Fallbacks only; the live colours come from the active dosha-period theme. */
  private static final int FALLBACK_GROUND = Color.parseColor("#FFFFFEFA");
  private static final int FALLBACK_SHEEN = Color.parseColor("#FFE7A733");

  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private ValueAnimator animator;
  private float phase = 0f;


  public SomaSplashGradientView (Context context) {
    super(context);
  }


  public SomaSplashGradientView (Context context, AttributeSet attrs) {
    super(context, attrs);
  }


  private int resolveThemeColor (int attr, int fallback) {
    android.util.TypedValue tv = new android.util.TypedValue();
    if (getContext().getTheme().resolveAttribute(attr, tv, true)) {
      return tv.data;
    }
    return fallback;
  }


  @Override
  protected void onAttachedToWindow () {
    super.onAttachedToWindow();
    if (animator == null) {
      animator = ValueAnimator.ofFloat(0f, 1f);
      animator.setDuration(SWEEP_DURATION_MS);
      animator.setRepeatCount(ValueAnimator.INFINITE);
      animator.setRepeatMode(ValueAnimator.REVERSE);
      animator.setInterpolator(new LinearInterpolator());
      animator.addUpdateListener(a -> {
        phase = (float) a.getAnimatedValue();
        invalidate();
      });
    }
    animator.start();
  }


  @Override
  protected void onDetachedFromWindow () {
    if (animator != null) {
      animator.cancel();
    }
    super.onDetachedFromWindow();
  }


  @Override
  protected void onDraw (Canvas canvas) {
    super.onDraw(canvas);
    float w = getWidth();
    float h = getHeight();
    if (w <= 0 || h <= 0) {
      return;
    }

    // A diagonal band slides from top-left to bottom-right and back over the
    // period's own ground - a slow, minimal sheen rather than a hard color swap.
    int ground = resolveThemeColor(R.attr.somaBg, FALLBACK_GROUND);
    int sheen = resolveThemeColor(R.attr.somaMuted, FALLBACK_SHEEN);
    float span = w + h;
    float offset = -span + phase * 2 * span;
    LinearGradient gradient = new LinearGradient(
        offset, 0, offset + span, h,
        new int[]{ground, sheen, ground},
        new float[]{0f, 0.5f, 1f},
        Shader.TileMode.CLAMP);
    paint.setShader(gradient);
    canvas.drawRect(0, 0, w, h, paint);
  }
}
