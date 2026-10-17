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
package com.absolute.android.notescentral.intro;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.absolute.android.notescentral.R;


/**
 * Shared chrome for the onboarding pages: step label, illustration, title and
 * description, on the fixed purple ground.
 *
 * The preview slot is now only used by the last page, which puts the theme
 * picker there in place of an illustration; the first two pages leave it empty.
 */
public class IntroFragment extends Fragment {

  @BindView(R.id.intro_step)
  TextView step;

  @BindView(R.id.intro_title)
  TextView title;

  @BindView(R.id.intro_description)
  TextView description;

  @BindView(R.id.intro_illustration)
  ImageView illustration;

  @BindView(R.id.intro_preview)
  FrameLayout preview;


  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.intro_slide, container, false);
    ButterKnife.bind(this, v);
    return v;
  }


  /** Fills a page whose visual is a drawn illustration. */
  protected void bindPage (int stepNumber, int titleRes, int descriptionRes, int illustrationRes) {
    bindText(stepNumber, titleRes, descriptionRes);
    illustration.setVisibility(View.VISIBLE);
    illustration.setImageResource(illustrationRes);
  }


  /**
   * Fills a page whose visual is a real piece of interface, inflated into the
   * preview slot in place of the illustration, and returns it for wiring up.
   */
  protected View bindInteractivePage (int stepNumber, int titleRes, int descriptionRes,
      int previewLayout) {
    bindText(stepNumber, titleRes, descriptionRes);
    illustration.setVisibility(View.GONE);
    preview.removeAllViews();
    View child = LayoutInflater.from(getContext()).inflate(previewLayout, preview, false);
    preview.addView(child);
    return child;
  }


  private void bindText (int stepNumber, int titleRes, int descriptionRes) {
    step.setText(getString(R.string.intro_step_format, stepNumber, 3));
    title.setText(titleRes);
    description.setText(descriptionRes);
  }
}
