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

package com.absolute.android.notescentral.models.misc;


import de.greenrobot.event.EventBus;
import com.absolute.android.notescentral.async.bus.DynamicNavigationReadyEvent;
import com.absolute.android.notescentral.async.bus.NotesUpdatedEvent;
import com.absolute.android.notescentral.db.DbHelper;
import com.absolute.android.notescentral.helpers.LogDelegate;
import com.absolute.android.notescentral.models.Note;
import java.util.List;


public class DynamicNavigationLookupTable {

  private static DynamicNavigationLookupTable instance;
  int archived;
  int trashed;
  int uncategorized;
  int reminders;


  private DynamicNavigationLookupTable () {
    EventBus.getDefault().register(this);
    update();
  }


  public static DynamicNavigationLookupTable getInstance () {
    if (instance == null) {
      instance = new DynamicNavigationLookupTable();
    }
    return instance;
  }


  public void update () {
    ((Runnable) () -> {
      archived = trashed = uncategorized = reminders = 0;
      List<Note> notes = DbHelper.getInstance().getAllNotes(false);
      for (int i = 0; i < notes.size(); i++) {
        if (notes.get(i).isTrashed()) {
          trashed++;
        } else if (notes.get(i).isArchived()) {
          archived++;
        } else if (notes.get(i).getAlarm() != null) {
          reminders++;
        }
        if (notes.get(i).getCategory() == null || notes.get(i).getCategory().getId().equals(0L)) {
          uncategorized++;
        }
      }
      EventBus.getDefault().post(new DynamicNavigationReadyEvent());
      LogDelegate.d("Dynamic menu finished counting items");
    }).run();
  }


  public void onEventAsync (NotesUpdatedEvent event) {
    update();
  }


  public int getArchived () {
    return archived;
  }


  public int getTrashed () {
    return trashed;
  }


  public int getReminders () {
    return reminders;
  }


  public int getUncategorized () {
    return uncategorized;
  }

}
