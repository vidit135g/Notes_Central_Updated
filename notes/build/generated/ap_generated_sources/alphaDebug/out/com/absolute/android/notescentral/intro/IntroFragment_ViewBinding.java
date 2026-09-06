// Generated code from Butter Knife. Do not modify!
package com.absolute.android.notescentral.intro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.absolute.android.notescentral.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class IntroFragment_ViewBinding implements Unbinder {
  private IntroFragment target;

  @UiThread
  public IntroFragment_ViewBinding(IntroFragment target, View source) {
    this.target = target;

    target.background = Utils.findRequiredView(source, R.id.intro_background, "field 'background'");
    target.title = Utils.findRequiredViewAsType(source, R.id.intro_title, "field 'title'", TextView.class);
    target.image = Utils.findRequiredViewAsType(source, R.id.intro_image, "field 'image'", ImageView.class);
    target.image_small = Utils.findRequiredViewAsType(source, R.id.intro_image_small, "field 'image_small'", ImageView.class);
    target.description = Utils.findRequiredViewAsType(source, R.id.intro_description, "field 'description'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    IntroFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.background = null;
    target.title = null;
    target.image = null;
    target.image_small = null;
    target.description = null;
  }
}
