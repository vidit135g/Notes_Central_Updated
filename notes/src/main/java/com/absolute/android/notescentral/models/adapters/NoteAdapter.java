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
package com.absolute.android.notescentral.models.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.text.Spanned;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nhaarman.listviewanimations.util.Insertable;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.async.TextWorkerTask;
import com.absolute.android.notescentral.helpers.LogDelegate;
import com.absolute.android.notescentral.models.Attachment;
import com.absolute.android.notescentral.models.Note;
import com.absolute.android.notescentral.models.holders.NoteViewHolder;
import com.absolute.android.notescentral.utils.BitmapHelper;
import com.absolute.android.notescentral.utils.Constants;
import com.absolute.android.notescentral.utils.Fonts;
import com.absolute.android.notescentral.utils.Navigation;
import com.absolute.android.notescentral.utils.TextHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;


public class NoteAdapter extends ArrayAdapter<Note> implements Insertable {

  private final Activity mActivity;
  private final int navigation;
  private List<Note> notes = new ArrayList<>();
  private SparseBooleanArray selectedItems = new SparseBooleanArray();
  private boolean expandedView;
  private int layout;
  private LayoutInflater inflater;
  private long closestNoteReminder = Long.parseLong(Constants.TIMESTAMP_UNIX_EPOCH_FAR);
  private int closestNotePosition;
  public interface ArchiveAction { void accept(Note note); }
  private ArchiveAction archiveAction;

  public void setArchiveAction(ArchiveAction action) { archiveAction = action; }

  private android.content.SharedPreferences pinPreferences() {
    return mActivity.getSharedPreferences("soma_pinned_notes", Context.MODE_PRIVATE);
  }

  private boolean isPinned(Note note) {
    return pinPreferences().getBoolean(String.valueOf(note.get_id()), false);
  }


  public NoteAdapter (Activity activity, int layout, List<Note> notes) {
    super(activity, R.layout.note_layout_expanded, notes);
    mActivity = activity;
    this.notes = notes;
    this.layout = layout;
    // Stable sort keeps the user's chosen ordering within each group.
    java.util.Collections.sort(notes, (a, b) -> Boolean.compare(isPinned(b), isPinned(a)));

    expandedView = layout == R.layout.note_layout_expanded;
    inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    navigation = Navigation.getNavigation();
    manageCloserNote(notes, navigation);
  }


  @Override
  public View getView (int position, View convertView, ViewGroup parent) {
    Note note = notes.get(position);
    NoteViewHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(layout, parent, false);
      holder = buildHolder(convertView, parent);
      convertView.setTag(holder);
    } else {
      holder = (NoteViewHolder) convertView.getTag();
    }
    boolean checklistPreviewShown = initChecklistPreview(note, holder);
    initText(note, holder, checklistPreviewShown);
    initIcons(note, holder);
    initCategoryBadge(note, holder);
    initReminderBadge(note, holder);
    initDates(note, holder);
    initThumbnail(note, holder);
    manageSelectionColor(position, note, holder);
    if (convertView instanceof com.absolute.android.notescentral.models.views.NoteSwipeLayout) {
      boolean enabled = archiveAction != null && !note.isLocked()
          && selectedItems.size() == 0;
      ((com.absolute.android.notescentral.models.views.NoteSwipeLayout) convertView).bind(
          enabled, isPinned(note), () -> {
            pinPreferences().edit().putBoolean(String.valueOf(note.get_id()), !isPinned(note)).apply();
            java.util.Collections.sort(notes, (a, b) -> Boolean.compare(isPinned(b), isPinned(a)));
            notifyDataSetChanged();
          }, () -> archiveAction.accept(note));
    }
    return convertView;
  }


  /**
   * Soma-style reminder pill (muted bg, primary text) with the alarm's date/time, replacing
   * the plain bell icon - matches the mockup's reminder-badge treatment on note cards.
   */
  private void initReminderBadge (Note note, NoteViewHolder holder) {
    if (holder.reminderBadge == null) {
      return;
    }
    if (note.getAlarm() == null) {
      holder.reminderBadge.setVisibility(View.GONE);
      return;
    }
    long alarmMillis;
    try {
      alarmMillis = Long.parseLong(note.getAlarm());
    } catch (NumberFormatException e) {
      holder.reminderBadge.setVisibility(View.GONE);
      return;
    }

    android.util.TypedValue mutedVal = new android.util.TypedValue();
    android.util.TypedValue primaryVal = new android.util.TypedValue();
    mActivity.getTheme().resolveAttribute(R.attr.somaMuted, mutedVal, true);
    mActivity.getTheme().resolveAttribute(R.attr.somaPrimary, primaryVal, true);

    GradientDrawable bg = new GradientDrawable();
    bg.setShape(GradientDrawable.RECTANGLE);
    bg.setCornerRadius(999f);
    bg.setColor(mutedVal.data);

    String reminderText = android.text.format.DateUtils.getRelativeDateTimeString(mActivity, alarmMillis,
        android.text.format.DateUtils.MINUTE_IN_MILLIS, android.text.format.DateUtils.WEEK_IN_MILLIS,
        android.text.format.DateUtils.FORMAT_SHOW_TIME | android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE)
        .toString();

    holder.reminderBadge.setText(reminderText);
    holder.reminderBadge.setTextColor(primaryVal.data);
    holder.reminderBadge.setBackground(bg);
    holder.reminderBadge.setVisibility(View.VISIBLE);
  }


  /**
   * Inline checkbox rows for checklist notes (up to 3), replacing the plain preview text -
   * matches the Soma mockup's checklist card instead of the flat "[x] item" symbol text
   * TextWorkerTask renders into note_content. Parses the same "[x] "/"[ ] " line format the
   * checklistview library writes into Note.content (see it.feio.android.checklistview
   * .interfaces.Constants), so it stays in sync with whatever the editor's checklist saves.
   */
  private boolean initChecklistPreview (Note note, NoteViewHolder holder) {
    if (holder.checklistPreview == null) {
      return false;
    }
    // The card root is wrap_content now (not a fixed height), so all 3 rows fit safely -
    // matches the mockup's 3-item checklist card example.
    View[] rows = {holder.checklistRow0, holder.checklistRow1, holder.checklistRow2};
    View[] boxes = {holder.checklistBox0, holder.checklistBox1, holder.checklistBox2};
    ImageView[] marks = {holder.checklistMark0, holder.checklistMark1, holder.checklistMark2};
    android.widget.TextView[] texts = {holder.checklistText0, holder.checklistText1, holder.checklistText2};

    if (!note.isChecklist() || note.isLocked() || TextUtils.isEmpty(note.getContent())) {
      holder.checklistPreview.setVisibility(View.GONE);
      return false;
    }

    android.util.TypedValue doneColorVal = new android.util.TypedValue();
    android.util.TypedValue pendingColorVal = new android.util.TypedValue();
    mActivity.getTheme().resolveAttribute(R.attr.somaTextM, doneColorVal, true);
    mActivity.getTheme().resolveAttribute(R.attr.somaTextS, pendingColorVal, true);

    int shown = 0;
    String[] checklistLines = note.getContent().split("\\r?\\n", -1);
    for (int lineIndex = 0; lineIndex < checklistLines.length; lineIndex++) {
      String rawLine = checklistLines[lineIndex];
      if (shown >= rows.length) {
        break;
      }
      String line = rawLine.trim();
      boolean checked;
      String label;
      if (line.startsWith("[x] ") || line.startsWith("[X] ")) {
        checked = true;
        label = line.substring(4).trim();
      } else if (line.startsWith("[ ] ")) {
        checked = false;
        label = line.substring(4).trim();
      } else {
        continue;
      }
      if (label.isEmpty()) {
        continue;
      }

      boxes[shown].setBackgroundResource(
          checked ? R.drawable.bg_soma_checkbox_checked : R.drawable.bg_soma_checkbox_unchecked);
      marks[shown].setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
      texts[shown].setText(label);
      texts[shown].setTextColor(checked ? doneColorVal.data : pendingColorVal.data);
      if (checked) {
        texts[shown].setPaintFlags(texts[shown].getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
      } else {
        texts[shown].setPaintFlags(texts[shown].getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
      }
      rows[shown].setVisibility(View.VISIBLE);
      final int contentLineIndex = lineIndex;
      rows[shown].setEnabled(!note.isLocked() && selectedItems.size() == 0);
      rows[shown].setContentDescription(label + (checked ? ", checked" : ", unchecked"));
      rows[shown].setOnClickListener(v -> {
        String[] currentLines = note.getContent().split("\\r?\\n", -1);
        if (contentLineIndex >= currentLines.length) return;
        String current = currentLines[contentLineIndex];
        int marker = current.indexOf('[');
        if (marker < 0 || marker + 2 >= current.length()) return;
        char state = current.charAt(marker + 1);
        currentLines[contentLineIndex] = current.substring(0, marker + 1)
            + (state == ' ' ? "x" : " ") + current.substring(marker + 2);
        note.setContent(TextUtils.join("\n", currentLines));
        new com.absolute.android.notescentral.async.notes.SaveNoteTask(saved -> notifyDataSetChanged(), true)
            .execute(note);
        notifyDataSetChanged();
      });
      shown++;
    }

    for (int i = shown; i < rows.length; i++) {
      rows[i].setVisibility(View.GONE);
    }

    holder.checklistPreview.setVisibility(shown > 0 ? View.VISIBLE : View.GONE);
    return shown > 0;
  }


  /**
   * Soma-style colored pill showing the note's category name, mirroring the
   * side-strip category_marker's color but as a readable label on the card.
   */
  private void initCategoryBadge (Note note, NoteViewHolder holder) {
    if (holder.categoryBadge == null) {
      return;
    }
    android.util.TypedValue primaryVal = new android.util.TypedValue();
    android.util.TypedValue onFillVal = new android.util.TypedValue();
    mActivity.getTheme().resolveAttribute(R.attr.somaPrimary, primaryVal, true);
    mActivity.getTheme().resolveAttribute(R.attr.somaOnFill, onFillVal, true);

    if (note.getCategory() != null && note.getCategory().getName() != null
        && !note.getCategory().getName().isEmpty()) {
      int color = primaryVal.data;
      if (note.getCategory().getColor() != null) {
        try {
          color = Integer.parseInt(note.getCategory().getColor());
        } catch (NumberFormatException ignored) {
        }
      }
      GradientDrawable bg = new GradientDrawable();
      bg.setShape(GradientDrawable.RECTANGLE);
      bg.setCornerRadius(999f);
      bg.setColor(color);
      holder.categoryBadge.setVisibility(View.VISIBLE);
      holder.categoryBadge.setText(note.getCategory().getName());
      holder.categoryBadge.setBackground(bg);
      holder.categoryBadge.setTextColor(onFillVal.data);
    } else {
      holder.categoryBadge.setVisibility(View.GONE);
    }
  }


  /**
   * Highlighted if is part of multiselection of notes. Remember to search for child with card ui
   */
  private void manageSelectionColor (int position, Note note, NoteViewHolder holder) {
    if (selectedItems.get(position)) {
      holder.cardLayout.setBackgroundColor(mActivity.getResources().getColor(
          R.color.list_bg_selected));
    } else {
      restoreDrawable(note, holder.cardLayout, holder);
    }
  }


  private void initThumbnail (Note note, NoteViewHolder holder) {

    if (expandedView && holder.attachmentThumbnail != null) {
      // If note is locked or without attachments nothing is shown
      if ((note.isLocked() && !mActivity.getSharedPreferences(Constants.PREFS_NAME,
          Context.MODE_MULTI_PROCESS).getBoolean("settings_password_access", false))
          || note.getAttachmentsList().size() == 0) {
        holder.attachmentThumbnail.setVisibility(View.GONE);
      } else {
        holder.attachmentThumbnail.setVisibility(View.VISIBLE);
        Attachment mAttachment = note.getAttachmentsList().get(0);
        Uri thumbnailUri = BitmapHelper.getThumbnailUri(mActivity, mAttachment);

        Glide.with(mActivity)
             .load(thumbnailUri)
             .apply(new RequestOptions().centerCrop())
             .transition(withCrossFade())
             .into(holder.attachmentThumbnail);
      }
    }
  }


  public List<Note> getNotes () {
    return notes;
  }


  private void initDates (Note note, NoteViewHolder holder) {
    String dateText = TextHelper.getDateText(mActivity, note, navigation);
    holder.date.setText(dateText);
    if (isPinned(note)) {
      holder.date.setText(mActivity.getString(R.string.soma_pinned_date, dateText));
    }
  }


  private void initIcons (Note note, NoteViewHolder holder) {
    // Evaluates the archived state...
    holder.archiveIcon.setVisibility(note.isArchived() ? View.VISIBLE : View.GONE);
    // ...the location
    holder.locationIcon.setVisibility(note.getLongitude() != null && note.getLongitude() != 0 ? View.VISIBLE :
        View.GONE);

    // ...the presence of an alarm - the reminder_badge pill (where the layout has one)
    // replaces this plain icon, matching the Soma mockup's reminder badge.
    boolean showAlarmIcon = note.getAlarm() != null && holder.reminderBadge == null;
    holder.alarmIcon.setVisibility(showAlarmIcon ? View.VISIBLE : View.GONE);
    // ...the locked with password state
    holder.lockedIcon.setVisibility(note.isLocked() ? View.VISIBLE : View.GONE);
    // ...the attachment icon for contracted view
    if (!expandedView) {
      holder.attachmentIcon.setVisibility(note.getAttachmentsList().size() > 0 ? View.VISIBLE : View.GONE);
    }

    // Status icons are hardcoded black assets - tint them to the active
    // Soma period's muted-text color so they stay legible on dark cards too.
    android.util.TypedValue tv = new android.util.TypedValue();
    mActivity.getTheme().resolveAttribute(R.attr.somaTextM, tv, true);
    int tint = tv.data;
    holder.archiveIcon.setColorFilter(tint);
    holder.locationIcon.setColorFilter(tint);
    holder.alarmIcon.setColorFilter(tint);
    holder.lockedIcon.setColorFilter(tint);
  }


  private void initText (Note note, NoteViewHolder holder, boolean checklistPreviewShown) {
    try {
      if (note.isChecklist()) {
        TextWorkerTask task = new TextWorkerTask(mActivity, holder.title, holder.content, expandedView,
            checklistPreviewShown);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, note);
      } else {
        Spanned[] titleAndContent = TextHelper.parseTitleAndContent(mActivity, note);
        holder.title.setText(titleAndContent[0]);
        holder.content.setText(titleAndContent[1]);
        holder.title.setText(titleAndContent[0]);
        if (titleAndContent[1].length() > 0) {
          holder.content.setText(titleAndContent[1]);
          holder.content.setVisibility(View.VISIBLE);
        } else {
          // GONE, not INVISIBLE: the cards are wrap_content now, so an
          // invisible preview still reserves its line and leaves a band of
          // dead space between the title and the timestamp. A locked note has
          // no preview by definition, so it hit this every time.
          holder.content.setVisibility(View.GONE);
        }
      }
    } catch (RejectedExecutionException e) {
      LogDelegate.w("Oversized tasks pool to load texts!", e);
    }
  }


  /**
   * Saves the position of the closest note to align list scrolling with it on start
   */
  private void manageCloserNote (List<Note> notes, int navigation) {
    if (navigation == Navigation.REMINDERS) {
      for (int i = 0; i < notes.size(); i++) {
        long now = Calendar.getInstance().getTimeInMillis();
        long reminder = Long.parseLong(notes.get(i).getAlarm());
        if (now < reminder && reminder < closestNoteReminder) {
          closestNotePosition = i;
          closestNoteReminder = reminder;
        }
      }
    }

  }


  /**
   * Returns the note with the nearest reminder in the future
   */
  public int getClosestNotePosition () {
    return closestNotePosition;
  }


  public SparseBooleanArray getSelectedItems () {
    return selectedItems;
  }


  public void addSelectedItem (Integer selectedItem) {
    selectedItems.put(selectedItem, true);
  }


  public void removeSelectedItem (Integer selectedItem) {
    selectedItems.delete(selectedItem);
  }


  public void clearSelectedItems () {
    selectedItems.clear();
  }


  public void restoreDrawable (Note note, View v) {
    restoreDrawable(note, v, null);
  }


  public void restoreDrawable (Note note, View v, NoteViewHolder holder) {
    final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
    final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
    v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    // The reference uses a consistent themed surface.  Categories are expressed by the
    // compact pill rather than the legacy full/partial colour overlays.
    v.setBackgroundResource(R.drawable.bg_card_soma);
    if (holder != null && holder.categoryMarker != null) {
      holder.categoryMarker.setBackgroundColor(Color.TRANSPARENT);
    }
  }


  @SuppressWarnings("unused")
  private void colorNote (Note note, View v) {
    colorNote(note, v, null);
  }


  /**
   * Color of category marker if note is categorized a function is active in preferences
   */
  private void colorNote (Note note, View v, NoteViewHolder holder) {
    // Retained for callers outside the adapter.  Soma cards deliberately do not adopt the
    // legacy category-strip/full-card colour modes; the badge is the sole category cue.
    v.setBackgroundResource(R.drawable.bg_card_soma);
    View marker = holder != null ? holder.categoryMarker : v.findViewById(R.id.category_marker);
    if (marker != null) marker.setBackgroundColor(Color.TRANSPARENT);
  }


  /**
   * Replaces notes
   */
  public void replace (Note note, int index) {
    if (notes.indexOf(note) != -1) {
      notes.remove(index);
    } else {
      index = notes.size();
    }
    notes.add(index, note);
  }


  @Override
  public void add (int i, @NonNull Object o) {
    insert((Note) o, i);
  }


  public void remove (List<Note> notes) {
    for (Note note : notes) {
      remove(note);
    }
  }


  private NoteViewHolder buildHolder (View convertView, ViewGroup parent) {
    // Overrides font sizes with the one selected from user
    Fonts.overrideTextSize(mActivity, mActivity.getSharedPreferences(Constants.PREFS_NAME,
        Context.MODE_MULTI_PROCESS), convertView);
    return new NoteViewHolder(convertView);
  }

}
