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

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.utils.SomaPeriod;
import java.util.ArrayList;
import java.util.List;


/**
 * Closing onboarding page: match the system, or pin light or dark.
 *
 * Was a 2x3 grid of dosha-period swatches. The periods are gone, so this is a
 * single row of three. The choice is recorded immediately but does not recreate
 * the Activity - AppIntro2 owns this one, and re-theming it mid-tour would drop
 * the user back on page one.
 *
 * Every colour here is a literal rather than a theme attr: the page sits on the
 * fixed purple onboarding ground, which is neither of the themes being offered.
 */
public class IntroSlide3 extends IntroFragment {

  private final List<View> swatches = new ArrayList<>();
  private String selected;


  @Override
  public void onActivityCreated (Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    View preview = bindInteractivePage(3, R.string.intro_personalise_title,
        R.string.intro_personalise_detail, R.layout.intro_preview_theme);

    LinearLayout grid = preview.findViewById(R.id.theme_grid);
    selected = SomaPeriod.getPreference(requireContext());

    LinearLayout row = new LinearLayout(getContext());
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    LayoutInflater inflater = LayoutInflater.from(getContext());
    for (int i = 0; i < SomaPeriod.ORDER.length; i++) {
      final String themeId = SomaPeriod.ORDER[i];
      View tile = inflater.inflate(R.layout.intro_theme_swatch, row, false);
      LinearLayout.LayoutParams lp =
          new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
      lp.setMarginStart(i == 0 ? 0 : dp(10));
      tile.setLayoutParams(lp);
      ((TextView) tile.findViewById(R.id.swatch_label)).setText(SomaPeriod.labelResId(themeId));
      tile.setTag(themeId);
      tile.setOnClickListener(v -> select(themeId));
      row.addView(tile);
      swatches.add(tile);
    }
    grid.addView(row);

    paint();
  }


  private void select (String themeIdOrAuto) {
    selected = themeIdOrAuto;
    SomaPeriod.setPreference(requireContext(), themeIdOrAuto);
    paint();
  }


  /** Redraws every tile so exactly one selection ring is visible. */
  private void paint () {
    int ink = ContextCompat.getColor(requireContext(), R.color.soma_intro_ink);
    int ring = ContextCompat.getColor(requireContext(), R.color.soma_light_elev);

    for (View tile : swatches) {
      String themeId = (String) tile.getTag();
      boolean isSelected = themeId.equals(selected);

      // "Auto" has no ground of its own, so it previews as the light one with
      // the dark one showing through the dot.
      int bg = ContextCompat.getColor(requireContext(),
          SomaPeriod.DARK.equals(themeId) ? R.color.soma_dark_bg : R.color.soma_light_bg);
      int dotColor = ContextCompat.getColor(requireContext(),
          SomaPeriod.AUTO.equals(themeId) ? R.color.soma_dark_bg : R.color.soma_light_primary);

      GradientDrawable ground = new GradientDrawable();
      ground.setCornerRadius(dp(16));
      ground.setColor(bg);
      ground.setStroke(isSelected ? dp(3) : dp(1),
          isSelected ? ring : ContextCompat.getColor(requireContext(), R.color.soma_light_border));
      tile.findViewById(R.id.swatch_ground).setBackground(ground);

      GradientDrawable dot = new GradientDrawable();
      dot.setShape(GradientDrawable.OVAL);
      dot.setColor(dotColor);
      tile.findViewById(R.id.swatch_dot).setBackground(dot);

      TextView label = (TextView) tile.findViewById(R.id.swatch_label);
      label.setTextColor(ring);
      label.setAlpha(isSelected ? 1f : 0.7f);
    }
  }


  private int dp (int value) {
    return Math.round(value * getResources().getDisplayMetrics().density);
  }
}
