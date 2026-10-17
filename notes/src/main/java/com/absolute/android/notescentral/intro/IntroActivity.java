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

package com.absolute.android.notescentral.intro;

import android.content.Context;
import android.os.Bundle;
import com.github.paolorotolo.appintro.AppIntro2;
import com.absolute.android.notescentral.OmniNotes;
import com.absolute.android.notescentral.utils.Constants;


public class IntroActivity extends AppIntro2 {

  @Override
  public void init (Bundle savedInstanceState) {
    // AppIntro2 owns a final onCreate; this is its supported initialization point.
    setTheme(com.absolute.android.notescentral.utils.SomaPeriod.themeResId(
        com.absolute.android.notescentral.utils.SomaPeriod.resolveId(this)));
    addSlide(new IntroSlide1(), getApplicationContext());
    addSlide(new IntroSlide2(), getApplicationContext());
    addSlide(new IntroSlide3(), getApplicationContext());
  }


  @Override
  public void onDonePressed () {
    OmniNotes.getAppContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).edit()
             .putBoolean(Constants.PREF_TOUR_COMPLETE, true).apply();
    finish();
  }


  public static boolean mustRun () {
    // Runs in debug too: excluding debug builds meant the tour was never
    // exercised during development, which is how it came to be missing from
    // the manifest while MainActivity was still starting it.
    return !OmniNotes.getAppContext().getSharedPreferences(Constants.PREFS_NAME,
        Context.MODE_MULTI_PROCESS).getBoolean(Constants.PREF_TOUR_COMPLETE, false);
  }


  @Override
  public void onBackPressed () {
    // Does nothing, you HAVE TO SEE THE INTRO!
  }
}
