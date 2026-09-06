// Generated code from Butter Knife. Do not modify!
package com.absolute.android.notescentral.models.holders;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.models.views.SquareImageView;
import com.neopixl.pixlui.components.textview.TextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NoteViewHolder_ViewBinding implements Unbinder {
  private NoteViewHolder target;

  @UiThread
  public NoteViewHolder_ViewBinding(NoteViewHolder target, View source) {
    this.target = target;

    target.root = Utils.findRequiredView(source, R.id.root, "field 'root'");
    target.cardLayout = Utils.findRequiredView(source, R.id.card_layout, "field 'cardLayout'");
    target.categoryMarker = Utils.findRequiredView(source, R.id.category_marker, "field 'categoryMarker'");
    target.title = Utils.findRequiredViewAsType(source, R.id.note_title, "field 'title'", TextView.class);
    target.content = Utils.findRequiredViewAsType(source, R.id.note_content, "field 'content'", TextView.class);
    target.date = Utils.findRequiredViewAsType(source, R.id.note_date, "field 'date'", TextView.class);
    target.archiveIcon = Utils.findRequiredViewAsType(source, R.id.archivedIcon, "field 'archiveIcon'", ImageView.class);
    target.locationIcon = Utils.findRequiredViewAsType(source, R.id.locationIcon, "field 'locationIcon'", ImageView.class);
    target.alarmIcon = Utils.findRequiredViewAsType(source, R.id.alarmIcon, "field 'alarmIcon'", ImageView.class);
    target.lockedIcon = Utils.findRequiredViewAsType(source, R.id.lockedIcon, "field 'lockedIcon'", ImageView.class);
    target.attachmentIcon = Utils.findOptionalViewAsType(source, R.id.attachmentIcon, "field 'attachmentIcon'", ImageView.class);
    target.attachmentThumbnail = Utils.findOptionalViewAsType(source, R.id.attachmentThumbnail, "field 'attachmentThumbnail'", SquareImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NoteViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.root = null;
    target.cardLayout = null;
    target.categoryMarker = null;
    target.title = null;
    target.content = null;
    target.date = null;
    target.archiveIcon = null;
    target.locationIcon = null;
    target.alarmIcon = null;
    target.lockedIcon = null;
    target.attachmentIcon = null;
    target.attachmentThumbnail = null;
  }
}
