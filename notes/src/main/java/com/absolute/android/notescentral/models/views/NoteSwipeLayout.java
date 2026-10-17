package com.absolute.android.notescentral.models.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.absolute.android.notescentral.R;

/** Reveals explicit actions without removing the note on a swipe. */
public class NoteSwipeLayout extends FrameLayout {
  private View card;
  private LinearLayout actions;
  private float downX, downY;
  private boolean dragging, open, enabled;
  private final int slop;

  public NoteSwipeLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    slop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  public void bind(boolean enabled, boolean pinned, Runnable pin, Runnable archive) {
    this.enabled = enabled;
    card = findViewById(R.id.card_layout);
    card.animate().cancel();
    card.setTranslationX(0);
    open = false;
    if (actions != null) removeView(actions);
    actions = new LinearLayout(getContext());
    actions.setGravity(Gravity.CENTER);
    actions.setVisibility(GONE);
    actions.addView(button(pinned ? R.string.soma_unpin : R.string.soma_pin, pin));
    actions.addView(button(R.string.archive, archive));
    LayoutParams params = new LayoutParams(dp(144), LayoutParams.MATCH_PARENT, Gravity.RIGHT);
    addView(actions, 0, params);
    // Keep actions reachable to assistive technology without requiring a gesture.
    androidx.core.view.ViewCompat.replaceAccessibilityAction(this,
        androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_EXPAND,
        getContext().getString(R.string.soma_note_actions), (view, args) -> {
          if (!enabled) return false;
          reveal(true);
          return true;
        });
  }

  private TextView button(int label, Runnable action) {
    TextView button = new TextView(getContext());
    button.setText(label);
    button.setGravity(Gravity.CENTER);
    button.setTextSize(13);
    android.util.TypedValue color = new android.util.TypedValue();
    getContext().getTheme().resolveAttribute(R.attr.somaPrimary, color, true);
    button.setTextColor(color.data);
    button.setLayoutParams(new LinearLayout.LayoutParams(dp(72), LayoutParams.MATCH_PARENT));
    button.setOnClickListener(v -> { reveal(false); action.run(); });
    return button;
  }

  private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

  private void reveal(boolean value) {
    open = value;
    actions.setVisibility(value ? VISIBLE : GONE);
    card.animate().translationX(value ? -dp(144) : 0).setDuration(160).start();
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent event) {
    if (!enabled) return false;
    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
      downX = event.getX(); downY = event.getY(); dragging = false;
    } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
      float dx = event.getX() - downX;
      if (Math.abs(dx) > slop && Math.abs(dx) > Math.abs(event.getY() - downY) * 1.5f) {
        dragging = true;
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
      }
    }
    return false;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (!dragging) return super.onTouchEvent(event);
    if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
      actions.setVisibility(VISIBLE);
      card.setTranslationX(Math.max(-dp(144), Math.min(0,
          (open ? -dp(144) : 0) + event.getX() - downX)));
    } else if (event.getActionMasked() == MotionEvent.ACTION_UP
        || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
      reveal(event.getActionMasked() != MotionEvent.ACTION_CANCEL && card.getTranslationX() < -dp(48));
      dragging = false;
      getParent().requestDisallowInterceptTouchEvent(false);
    }
    return true;
  }
}
