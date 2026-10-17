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

package com.absolute.android.notescentral.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.absolute.android.notescentral.OmniNotes;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.helpers.date.DateHelper;
import com.absolute.android.notescentral.models.Note;
import com.absolute.android.notescentral.receiver.AlarmReceiver;
import com.absolute.android.notescentral.utils.date.DateUtils;
import java.util.Calendar;


public class ReminderHelper {

  public static void addReminder (Context context, Note note) {
    if (note.getAlarm() != null) {
      addReminder(context, note, Long.parseLong(note.getAlarm()));
    }
  }


  public static void addReminder (Context context, Note note, long reminder) {
    if (DateUtils.isFuture(reminder)) {
      Intent intent = new Intent(context, AlarmReceiver.class);
      intent.putExtra(Constants.INTENT_NOTE, ParcelableUtil.marshall(note));
      PendingIntent sender = PendingIntent.getBroadcast(context, getRequestCode(note), intent,
          PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      // Inexact-but-Doze-aware scheduling: a note reminder firing within a few
      // minutes of the requested time is fine (unlike an alarm clock or a
      // calendar event), and this needs no special permission - unlike
      // setExact()/setExactAndAllowWhileIdle(), which require
      // SCHEDULE_EXACT_ALARM/USE_EXACT_ALARM and gate the app on Play's
      // "core functionality is an alarm clock or calendar" eligibility check.
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder, sender);
      } else {
        am.set(AlarmManager.RTC_WAKEUP, reminder, sender);
      }
    }
  }


  /**
   * Checks if exists any reminder for given note
   */
  public static boolean checkReminder (Context context, Note note) {
    return PendingIntent.getBroadcast(context, getRequestCode(note), new Intent(context, AlarmReceiver
        .class), PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null;
  }


  static int getRequestCode (Note note) {
    Long longCode = note.getCreation() != null ? note.getCreation() : Calendar.getInstance().getTimeInMillis() / 1000L;
    return longCode.intValue();
  }


  public static void removeReminder (Context context, Note note) {
    if (!TextUtils.isEmpty(note.getAlarm())) {
      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      Intent intent = new Intent(context, AlarmReceiver.class);
      PendingIntent p = PendingIntent.getBroadcast(context, getRequestCode(note), intent, PendingIntent.FLAG_IMMUTABLE);
      am.cancel(p);
      p.cancel();
    }
  }


  public static void showReminderMessage (String reminderString) {
    if (reminderString != null) {
      long reminder = Long.parseLong(reminderString);
      if (reminder > Calendar.getInstance().getTimeInMillis()) {
        new Handler(OmniNotes.getAppContext().getMainLooper()).post(() -> Toast.makeText(OmniNotes
                .getAppContext(),
            OmniNotes.getAppContext().getString(R.string.alarm_set_on) + " " + DateHelper.getDateTimeShort
                (OmniNotes.getAppContext(), reminder), Toast.LENGTH_LONG).show());
      }
    }
  }
}
