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
package com.absolute.android.notescentral.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import com.absolute.android.notescentral.R;

/**
 * Resolves the active theme: Light, Dark, or Auto (follow the system), which is
 * the default.
 *
 * This used to resolve one of six Ayurvedic dosha periods from the hour of day.
 * That is gone — the app ships one light look and one dark one. The class name,
 * the method signatures and the stored preference key are all kept so the ten
 * Activities that call {@link #themeResId} and the Settings screen did not have
 * to change shape; only what they resolve to is different.
 *
 * Old saved values ("vata-dawn", "pitta-day", …) are still read from prefs by
 * existing installs. {@link #normalise} maps each to LIGHT or DARK by whether
 * that period used to be a dark ground, so an upgrading user lands somewhere
 * sensible rather than on the default.
 */
public class SomaPeriod {

  /** Follow the system light/dark setting. The default. */
  public static final String AUTO = "auto";

  public static final String LIGHT = "light";
  public static final String DARK = "dark";

  /** The order the Settings picker lists them in. */
  public static final String[] ORDER = {AUTO, LIGHT, DARK};

  private SomaPeriod () {}


  /** The theme actually in effect right now — never {@link #AUTO} itself. */
  public static String resolveId (Context context) {
    String saved = getPreference(context);
    if (AUTO.equals(saved)) {
      return followingSystem(context);
    }
    return saved;
  }


  /** LIGHT or DARK, from the system's current night-mode configuration. */
  private static String followingSystem (Context context) {
    int mode = context.getResources().getConfiguration().uiMode
        & Configuration.UI_MODE_NIGHT_MASK;
    return mode == Configuration.UI_MODE_NIGHT_YES ? DARK : LIGHT;
  }


  /**
   * Maps anything that might be sitting in prefs — including the six retired
   * dosha-period ids — onto a value this class still understands.
   */
  private static String normalise (String saved) {
    if (saved == null) {
      return AUTO;
    }
    switch (saved) {
      case LIGHT:
      case DARK:
      case AUTO:
        return saved;
      // The three periods that used to be dark grounds.
      case "vata-dawn":
      case "kapha-pm":
      case "pitta-night":
        return DARK;
      // The three that used to be light.
      case "kapha-am":
      case "pitta-day":
      case "vata-pm":
        return LIGHT;
      default:
        return AUTO;
    }
  }


  /** The saved preference value: LIGHT, DARK, or AUTO. */
  public static String getPreference (Context context) {
    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME,
        Context.MODE_MULTI_PROCESS);
    return normalise(prefs.getString(Constants.PREF_SOMA_PERIOD, AUTO));
  }


  public static void setPreference (Context context, String themeIdOrAuto) {
    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME,
        Context.MODE_MULTI_PROCESS);
    prefs.edit().putString(Constants.PREF_SOMA_PERIOD, normalise(themeIdOrAuto)).apply();
  }


  public static int themeResId (String themeId) {
    return DARK.equals(themeId)
        ? R.style.OmniNotesTheme_Soma_Dark
        : R.style.OmniNotesTheme_Soma_Light;
  }


  /** That theme's screen background, for previewing it outside its theme. */
  public static int bgColorResId (String themeId) {
    return DARK.equals(themeId) ? R.color.soma_dark_bg : R.color.soma_light_bg;
  }


  /** That theme's accent, for previewing it outside its theme. */
  public static int primaryColorResId (String themeId) {
    return DARK.equals(themeId) ? R.color.soma_dark_primary : R.color.soma_light_primary;
  }


  public static int labelResId (String themeId) {
    if (LIGHT.equals(themeId)) {
      return R.string.soma_theme_light;
    }
    if (DARK.equals(themeId)) {
      return R.string.soma_theme_dark;
    }
    return R.string.soma_theme_auto;
  }
}
