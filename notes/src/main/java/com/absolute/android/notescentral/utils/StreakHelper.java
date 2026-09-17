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
import java.util.concurrent.TimeUnit;

/**
 * Tracks a simple day-streak: how many consecutive days (including today)
 * the user has saved at least one note. Self-tracked, no permissions - just
 * two SharedPreferences values (last active day-number, current streak).
 */
public class StreakHelper {

  private static final String PREF_STREAK_COUNT = "soma_streak_count";
  private static final String PREF_STREAK_LAST_DAY = "soma_streak_last_day";

  private StreakHelper () {}


  private static long dayNumber () {
    return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
  }


  /** Call whenever a note finishes saving. */
  public static void recordActivityToday (Context context) {
    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    long today = dayNumber();
    long lastDay = prefs.getLong(PREF_STREAK_LAST_DAY, -1);

    if (lastDay == today) {
      return; // Already counted today
    }

    int streak = prefs.getInt(PREF_STREAK_COUNT, 0);
    streak = (lastDay == today - 1) ? streak + 1 : 1;

    prefs.edit()
        .putInt(PREF_STREAK_COUNT, streak)
        .putLong(PREF_STREAK_LAST_DAY, today)
        .apply();
  }


  /** Current streak, or 0 if the last active day wasn't today or yesterday. */
  public static int getCurrentStreak (Context context) {
    SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    long lastDay = prefs.getLong(PREF_STREAK_LAST_DAY, -1);
    long today = dayNumber();
    if (lastDay != today && lastDay != today - 1) {
      return 0;
    }
    return prefs.getInt(PREF_STREAK_COUNT, 0);
  }
}
