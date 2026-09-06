// Generated code from Butter Knife. Do not modify!
package com.absolute.android.notescentral;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CategoryActivity_ViewBinding implements Unbinder {
  private CategoryActivity target;

  private View view7f0a00d3;

  private View view7f0a0297;

  private View view7f0a00ae;

  @UiThread
  public CategoryActivity_ViewBinding(CategoryActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CategoryActivity_ViewBinding(final CategoryActivity target, View source) {
    this.target = target;

    View view;
    target.title = Utils.findRequiredViewAsType(source, R.id.category_title, "field 'title'", EditText.class);
    target.description = Utils.findRequiredViewAsType(source, R.id.category_description, "field 'description'", EditText.class);
    view = Utils.findRequiredView(source, R.id.delete, "field 'deleteBtn' and method 'deleteCategory'");
    target.deleteBtn = Utils.castView(view, R.id.delete, "field 'deleteBtn'", Button.class);
    view7f0a00d3 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.deleteCategory();
      }
    });
    view = Utils.findRequiredView(source, R.id.save, "field 'saveBtn' and method 'saveCategory'");
    target.saveBtn = Utils.castView(view, R.id.save, "field 'saveBtn'", Button.class);
    view7f0a0297 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.saveCategory();
      }
    });
    view = Utils.findRequiredView(source, R.id.color_chooser, "field 'colorChooser' and method 'showColorChooserCustomColors'");
    target.colorChooser = Utils.castView(view, R.id.color_chooser, "field 'colorChooser'", ImageView.class);
    view7f0a00ae = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.showColorChooserCustomColors();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    CategoryActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.title = null;
    target.description = null;
    target.deleteBtn = null;
    target.saveBtn = null;
    target.colorChooser = null;

    view7f0a00d3.setOnClickListener(null);
    view7f0a00d3 = null;
    view7f0a0297.setOnClickListener(null);
    view7f0a0297 = null;
    view7f0a00ae.setOnClickListener(null);
    view7f0a00ae = null;
  }
}
