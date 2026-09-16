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
import com.absolute.android.notescentral.R;
import java.util.Calendar;

/**
 * Resolves the active Soma dosha-period theme: either the one the user
 * manually picked in Settings, or the one matching the current hour of day
 * ("auto", the default) — same six windows as the source Soma app:
 *
 * vata-dawn 2-6am · kapha-am 6-10am · pitta-day 10am-2pm ·
 * vata-pm 2-6pm · kapha-pm 6-10pm · pitta-night 10pm-2am
 */
public class SomaPeriod {

  public static final String AUTO = "auto";

  public static final String VATA_DAWN = "vata-dawn";
  public static final String KAPHA_AM = "kapha-am";
  public static final String PITTA_DAY = "pitta-day";
  public static final String VATA_PM = "vata-pm";
  public static final String KAPHA_PM = "kapha-pm";
  public static final String PITTA_NIGHT = "pitta-night";

  public static final String[] ORDER = {VATA_DAWN, KAPHA_AM, PITTA_DAY, VATA_PM, KAPHA_PM, PITTA_NIGHT};

  private SomaPeriod () {}


  /** The period id actually in effect right now (never "auto" itself). */
  public static String resolveId (Context context) {
    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    String saved = prefs.getString(Constants.PREF_SOMA_PERIOD, AUTO);
    if (AUTO.equals(saved)) {
      return byHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }
    return saved;
  }


  public static String byHour (int hour) {
    if (hour >= 2 && hour < 6) return VATA_DAWN;
    if (hour >= 6 && hour < 10) return KAPHA_AM;
    if (hour >= 10 && hour < 14) return PITTA_DAY;
    if (hour >= 14 && hour < 18) return VATA_PM;
    if (hour >= 18 && hour < 22) return KAPHA_PM;
    return PITTA_NIGHT; // 22-24 and 0-2
  }


  /** The saved preference value: a period id, or AUTO. */
  public static String getPreference (Context context) {
    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    return prefs.getString(Constants.PREF_SOMA_PERIOD, AUTO);
  }


  public static void setPreference (Context context, String periodIdOrAuto) {
    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    prefs.edit().putString(Constants.PREF_SOMA_PERIOD, periodIdOrAuto).apply();
  }


  public static int themeResId (String periodId) {
    switch (periodId) {
      case KAPHA_AM: return R.style.OmniNotesTheme_Soma_KaphaAm;
      case PITTA_DAY: return R.style.OmniNotesTheme_Soma_PittaDay;
      case VATA_PM: return R.style.OmniNotesTheme_Soma_VataPm;
      case KAPHA_PM: return R.style.OmniNotesTheme_Soma_KaphaPm;
      case PITTA_NIGHT: return R.style.OmniNotesTheme_Soma_PittaNight;
      case VATA_DAWN:
      default: return R.style.OmniNotesTheme_Soma_VataDawn;
    }
  }


  public static int labelResId (String periodId) {
    switch (periodId) {
      case KAPHA_AM: return R.string.soma_period_kapha_am;
      case PITTA_DAY: return R.string.soma_period_pitta_day;
      case VATA_PM: return R.string.soma_period_vata_pm;
      case KAPHA_PM: return R.string.soma_period_kapha_pm;
      case PITTA_NIGHT: return R.string.soma_period_pitta_night;
      case VATA_DAWN:
      default: return R.string.soma_period_vata_dawn;
    }
  }
}
