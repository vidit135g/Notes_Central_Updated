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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.absolute.android.notescentral.R;

/**
 * The empty-notes-list centerpiece: a slow-breathing gradient orb with
 * outward ripple rings, in the spirit of the Soma app's "breath orb" anchor
 * illustration (a calm meditation cue) rather than a generic stock Lottie
 * animation. Colors are pulled live from the active dosha-period theme so it
 * re-themes with everything else. One continuously-repeating driver value
 * (0-1 per ~4.2s "breath") feeds both the orb's gentle scale pulse and three
 * staggered ripple rings, instead of separate animators per element.
 */
public class SomaBreathOrbView extends View {

  private static final long BREATH_DURATION_MS = 4200;
  private static final int RING_COUNT = 3;

  private final Paint orbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private ValueAnimator animator;
  private float t = 0f;

  private int colorHighlight;
  private int colorPrimary;
  private int colorDeep;
  private int colorRing;
  private boolean colorsResolved = false;


  public SomaBreathOrbView (Context context) {
    super(context);
    init();
  }


  public SomaBreathOrbView (Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }


  private void init () {
    ringPaint.setStyle(Paint.Style.STROKE);
    shadowPaint.setStyle(Paint.Style.FILL);
  }


  private void resolveColors () {
    TypedValue accentVal = new TypedValue();
    TypedValue primaryVal = new TypedValue();
    TypedValue pressedVal = new TypedValue();
    getContext().getTheme().resolveAttribute(R.attr.somaAccent, accentVal, true);
    getContext().getTheme().resolveAttribute(R.attr.somaPrimary, primaryVal, true);
    getContext().getTheme().resolveAttribute(R.attr.somaPrimaryPressed, pressedVal, true);
    colorHighlight = accentVal.data;
    colorPrimary = primaryVal.data;
    colorDeep = pressedVal.data;
    colorRing = primaryVal.data;
    colorsResolved = true;
  }


  @Override
  protected void onVisibilityChanged (View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    setAnimating(visibility == VISIBLE && getWindowVisibility() == VISIBLE);
  }


  @Override
  protected void onWindowVisibilityChanged (int visibility) {
    super.onWindowVisibilityChanged(visibility);
    setAnimating(visibility == VISIBLE && getVisibility() == VISIBLE);
  }


  @Override
  protected void onDetachedFromWindow () {
    setAnimating(false);
    super.onDetachedFromWindow();
  }


  /** Starts or pauses the breathing/ripple loop - called on visibility changes and by the
   *  fragment lifecycle (no point animating an off-screen or backgrounded orb). */
  public void setAnimating (boolean animating) {
    if (animating) {
      if (animator == null) {
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(BREATH_DURATION_MS);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
          t = (float) a.getAnimatedValue();
          invalidate();
        });
      }
      if (!animator.isRunning()) {
        animator.start();
      }
    } else if (animator != null) {
      animator.cancel();
    }
  }


  @Override
  protected void onDraw (android.graphics.Canvas canvas) {
    super.onDraw(canvas);
    if (!colorsResolved) {
      resolveColors();
    }

    float w = getWidth();
    float h = getHeight();
    if (w <= 0 || h <= 0) {
      return;
    }

    float cx = w / 2f;
    float cy = h / 2f;
    float baseR = Math.min(w, h) * 0.24f;

    // Smooth breathing scale: 0.95 -> 1.0 -> 0.95 across one full cycle.
    float breath = 0.95f + 0.05f * (float) (0.5 + 0.5 * Math.sin(t * 2 * Math.PI));
    float orbR = baseR * breath;

    // Soft grounding shadow, fixed size so it doesn't pulse with the orb.
    shadowPaint.setColor(Color.argb(40, 0, 0, 0));
    canvas.drawOval(cx - baseR * 0.85f, cy + orbR * 0.72f, cx + baseR * 0.85f, cy + orbR * 0.95f, shadowPaint);

    // Ripple rings, staggered a third of a cycle apart, expanding outward and fading.
    for (int i = 0; i < RING_COUNT; i++) {
      float ringT = (t + i / (float) RING_COUNT) % 1f;
      float ringR = orbR + (baseR * 1.4f) * ringT;
      int alpha = (int) (90 * (1f - ringT));
      if (alpha <= 0) {
        continue;
      }
      ringPaint.setColor(colorRing);
      ringPaint.setAlpha(alpha);
      ringPaint.setStrokeWidth(2.5f * getResources().getDisplayMetrics().density);
      canvas.drawCircle(cx, cy, ringR, ringPaint);
    }

    // Glossy gradient orb - highlight offset toward the upper-left, like a soft light source.
    RadialGradient gradient = new RadialGradient(
        cx - orbR * 0.3f, cy - orbR * 0.35f, orbR * 1.35f,
        new int[]{colorHighlight, colorPrimary, colorDeep},
        new float[]{0f, 0.55f, 1f},
        Shader.TileMode.CLAMP);
    orbPaint.setShader(gradient);
    canvas.drawCircle(cx, cy, orbR, orbPaint);
  }
}
