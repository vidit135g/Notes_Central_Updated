package com.absolute.android.notescentral.helpers.date;

import android.content.Context;
import android.text.format.Time;
import com.absolute.android.notescentral.OmniNotes;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.helpers.LogDelegate;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrenceFormatter;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.RRule;
import org.apache.commons.lang3.StringUtils;

public class RecurrenceHelper {

    public static String buildRecurrenceRuleByRecurrenceOptionAndRule(SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String string2) {
        if (string2 == null && recurrenceOption != SublimeRecurrencePicker.RecurrenceOption.DOES_NOT_REPEAT) {
            return new RRule(new Recur(Recur.Frequency.valueOf(recurrenceOption.toString()), new DateTime(32519731800000L))).toString().replace("RRULE:", "");
        }
        return string2;
    }

    public static String formatRecurrence(Context context, String string2) {
        if (StringUtils.isEmpty(string2)) {
            return "";
        }
        EventRecurrence eventRecurrence = new EventRecurrence();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(new Date().getTime());
        eventRecurrence.setStartDate(new Time(stringBuilder.toString()));
        eventRecurrence.parse(string2);
        return EventRecurrenceFormatter.getRepeatString(context.getApplicationContext(), context.getResources(), eventRecurrence, true);
    }

    public static String getNoteRecurrentReminderText(long l, String string2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RecurrenceHelper.formatRecurrence(OmniNotes.getAppContext(), string2));
        stringBuilder.append(" ");
        stringBuilder.append(OmniNotes.getAppContext().getString(R.string.at));
        stringBuilder.append(" ");
        stringBuilder.append(DateHelper.getDateTimeShort(OmniNotes.getAppContext(), l));
        return stringBuilder.toString();
    }

    public static String getNoteReminderText(long l) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(OmniNotes.getAppContext().getString(R.string.alarm_set_on));
        stringBuilder.append(" ");
        stringBuilder.append(DateHelper.getDateTimeShort(OmniNotes.getAppContext(), l));
        return stringBuilder.toString();
    }

    public static Long nextReminderFromRecurrenceRule(long l, long l2, String object) {
        try {
            RRule rrule = new RRule();
            rrule.setValue(object);
            long l3 = 60000L + l;
            if (l3 >= l2) {
                l2 = l3;
            }
            Recur recur = rrule.getRecur();
            DateTime dtStart = new DateTime(l);
            DateTime dtAfter = new DateTime(l2);
            Date next = recur.getNextDate(dtStart, dtAfter);
            if (next == null) {
                return 0L;
            }
            return next.getTime();
        } catch (Exception e) {
            LogDelegate.e("Error parsing rrule");
            return 0L;
        }
    }

    public static Long nextReminderFromRecurrenceRule(long l, String string2) {
        return RecurrenceHelper.nextReminderFromRecurrenceRule(l, Calendar.getInstance().getTimeInMillis(), string2);
    }
}
