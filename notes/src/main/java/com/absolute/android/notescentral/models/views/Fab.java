/*
 * Copyright (C) 2013-2019 Federico Iosue (federico@iosue.it)
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

import static androidx.core.view.ViewCompat.animate;

import androidx.core.view.ViewPropertyAnimatorListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.absolute.android.notescentral.OmniNotes;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.models.listeners.AbsListViewScrollDetector;
import com.absolute.android.notescentral.models.listeners.OnFabItemClickedListener;
import com.absolute.android.notescentral.utils.Constants;

/**
 * The redesigned bottom-app-bar layout replaced the old expandable
 * {@code FloatingActionsMenu} speed-dial with a single always-visible
 * Material {@code FloatingActionButton} (id {@code fab1}). This class keeps
 * the same public API the rest of {@code ListFragment} already relies on
 * (showFab/hideFab/isExpanded/etc.) but drives a plain View instead of a
 * speed-dial menu — there is no expand/collapse state anymore, so
 * isExpanded() always reports false and performToggle() is a no-op.
 */
public class Fab {

  private View fab;
  private boolean fabAllowed;
  private boolean fabHidden;
  private final ListView listView;
  private View overlay;

  OnFabItemClickedListener onFabItemClickedListener;


  public Fab (View fabView, ListView listView, boolean expandOnLongClick) {
    this.fab = fabView;
    this.listView = listView;
    init();
  }


  private void init () {
    this.fabHidden = true;

    fab.setOnClickListener(v -> onFabItemClickedListener.onFabItemClick(R.id.fab_note));

    listView.setOnScrollListener(
        new AbsListViewScrollDetector() {
          public void onScrollUp () {
            if (fab != null) {
              hideFab();
            }
          }

          public void onScrollDown () {
            if (fab != null) {
              showFab();
            }
          }
        });
  }


  public void performToggle () {
    // No-op: the redesigned FAB is a single always-visible button, nothing to expand/collapse.
  }


  public void showFab () {
    if (fab != null && fabAllowed && fabHidden) {
      animateFab(0, View.VISIBLE, View.VISIBLE);
      fabHidden = false;
    }
  }


  public void hideFab () {
    if (fab != null && !fabHidden) {
      animateFab(fab.getHeight() + getMarginBottom(fab), View.VISIBLE, View.INVISIBLE);
      fabHidden = true;
    }
  }


  public void animateFab (int translationY, final int visibilityBefore, final int visibilityAfter) {
    animate(fab).setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(Constants.FAB_ANIMATION_TIME)
                .translationY(translationY)
                .setListener(new ViewPropertyAnimatorListener() {
                  @Override
                  public void onAnimationStart (View view) {
                    fab.setVisibility(visibilityBefore);
                  }

                  @Override
                  public void onAnimationEnd (View view) {
                    fab.setVisibility(visibilityAfter);
                  }

                  @Override
                  public void onAnimationCancel (View view) {
                  }
                });
  }


  public void setAllowed (boolean allowed) {
    fabAllowed = allowed;
  }


  private int getMarginBottom (View view) {
    int marginBottom = 0;
    final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
      marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
    }
    return marginBottom;
  }

  public void setOnFabItemClickedListener (OnFabItemClickedListener onFabItemClickedListener) {
    this.onFabItemClickedListener = onFabItemClickedListener;
  }


  public void setOverlay (View overlay) {
    this.overlay = overlay;
    this.overlay.setOnClickListener(v -> performToggle());
  }


  public void setOverlay (int colorResurce) {
    View overlayView = new View(OmniNotes.getAppContext());
    overlayView.setBackgroundResource(colorResurce);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
        .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    overlayView.setLayoutParams(params);
    overlayView.setVisibility(View.GONE);
    overlayView.setOnClickListener(v -> performToggle());
    ViewGroup parent = ((ViewGroup) fab.getParent());
    parent.addView(overlayView, parent.indexOfChild(fab));
    this.overlay = overlayView;
  }

  public boolean isExpanded () {
    return false;
  }
}
