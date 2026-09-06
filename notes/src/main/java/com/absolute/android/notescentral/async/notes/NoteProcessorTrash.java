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

package com.absolute.android.notescentral.async.notes;

import com.absolute.android.notescentral.OmniNotes;
import com.absolute.android.notescentral.db.DbHelper;
import com.absolute.android.notescentral.models.Note;
import com.absolute.android.notescentral.utils.ReminderHelper;
import com.absolute.android.notescentral.utils.ShortcutHelper;
import java.util.List;


public class NoteProcessorTrash extends NoteProcessor {

  boolean trash;


  public NoteProcessorTrash (List<Note> notes, boolean trash) {
    super(notes);
    this.trash = trash;
  }


  @Override
  protected void processNote (Note note) {
    if (trash) {
      ShortcutHelper.removeshortCut(OmniNotes.getAppContext(), note);
      ReminderHelper.removeReminder(OmniNotes.getAppContext(), note);
    } else {
      ReminderHelper.addReminder(OmniNotes.getAppContext(), note);
    }
    DbHelper.getInstance().trashNote(note, trash);
  }
}
