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

import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * The toolbar's menu icons (attach/category/tag/share/overflow etc.) are
 * hardcoded "_white_24dp" assets, drawn for the old solid colorPrimary
 * Material bar. The flat Soma header now sits directly on somaBg, which is
 * light in day periods - so those white icons need to be re-tinted to a
 * theme-appropriate color on every menu (re)build, or they disappear.
 */
public class MenuTintHelper {

  private MenuTintHelper () {}

  public static void tintMenuIcons (Menu menu, int color) {
    for (int i = 0; i < menu.size(); i++) {
      MenuItem item = menu.getItem(i);
      Drawable icon = item.getIcon();
      if (icon != null) {
        Drawable wrapped = DrawableCompat.wrap(icon.mutate());
        DrawableCompat.setTint(wrapped, color);
        item.setIcon(wrapped);
      }
    }
  }
}
