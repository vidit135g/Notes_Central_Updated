/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.text.format.DateUtils
 *  com.absolute.android.notescentral.OmniNotes
 *  com.absolute.android.notescentral.utils.date.DateUtils
 */
package com.absolute.android.notescentral.helpers.date;

import android.content.Context;
import android.text.format.DateUtils;
import com.absolute.android.notescentral.OmniNotes;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHelper {
    private DateHelper() {
    }

    public static String getSortableDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String onDateSet(int year, int month, int day, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(1, year);
        cal.set(2, month);
        cal.set(5, day);
        return sdf.format(cal.getTime());
    }

    public static String onTimeSet(int hour, int minute, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(11, hour);
        cal.set(12, minute);
        return sdf.format(cal.getTime());
    }

    public static String getDateTimeShort(Context mContext, Long date) {
        int flags = 98322;
        return date == null ? "" : DateUtils.formatDateTime((Context)mContext, (long)date, (int)flags) + " " + DateUtils.formatDateTime((Context)mContext, (long)date, (int)1);
    }

    public static String getTimeShort(Context mContext, Long time) {
        if (time == null) {
            return "";
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return DateUtils.formatDateTime((Context)mContext, (long)time, (int)1);
    }

    public static String getTimeShort(Context mContext, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(11, hourOfDay);
        c.set(12, minute);
        return DateUtils.formatDateTime((Context)mContext, (long)c.getTimeInMillis(), (int)1);
    }

    public static String formatShortTime(Context mContext, long time) {
        String m = String.valueOf(time / 1000L / 60L);
        String s = String.format("%02d", time / 1000L % 60L);
        return m + ":" + s;
    }

    public static String getFormattedDate(Long timestamp, boolean prettified) {
        if (prettified) {
            return com.absolute.android.notescentral.utils.date.DateUtils.prettyTime((Long)timestamp);
        }
        return DateHelper.getDateTimeShort(OmniNotes.getAppContext(), timestamp);
    }

    public static Long nextReminderFromRecurrenceRule(long reminder, String recurrenceRule) {
        return RecurrenceHelper.nextReminderFromRecurrenceRule(reminder, recurrenceRule);
    }

    public static String getNoteReminderText(long reminder) {
        return RecurrenceHelper.getNoteReminderText(reminder);
    }

    public static String getNoteRecurrentReminderText(long reminder, String rrule) {
        return RecurrenceHelper.getNoteRecurrentReminderText(reminder, rrule);
    }

}
