// Generated code from Butter Knife. Do not modify!
package com.absolute.android.notescentral;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.absolute.android.notescentral.models.views.SketchView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SketchFragment_ViewBinding implements Unbinder {
  private SketchFragment target;

  @UiThread
  public SketchFragment_ViewBinding(SketchFragment target, View source) {
    this.target = target;

    target.stroke = Utils.findRequiredViewAsType(source, R.id.sketch_stroke, "field 'stroke'", ImageView.class);
    target.eraser = Utils.findRequiredViewAsType(source, R.id.sketch_eraser, "field 'eraser'", ImageView.class);
    target.mSketchView = Utils.findRequiredViewAsType(source, R.id.drawing, "field 'mSketchView'", SketchView.class);
    target.undo = Utils.findRequiredViewAsType(source, R.id.sketch_undo, "field 'undo'", ImageView.class);
    target.redo = Utils.findRequiredViewAsType(source, R.id.sketch_redo, "field 'redo'", ImageView.class);
    target.erase = Utils.findRequiredViewAsType(source, R.id.sketch_erase, "field 'erase'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SketchFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.stroke = null;
    target.eraser = null;
    target.mSketchView = null;
    target.undo = null;
    target.redo = null;
    target.erase = null;
  }
}
