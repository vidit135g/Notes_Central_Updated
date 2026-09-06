// Generated code from Butter Knife. Do not modify!
package com.absolute.android.notescentral;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.absolute.android.notescentral.models.views.InterceptorLinearLayout;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.pnikosis.materialishprogress.ProgressWheel;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ListFragment_ViewBinding implements Unbinder {
  private ListFragment target;

  @UiThread
  public ListFragment_ViewBinding(ListFragment target, View source) {
    this.target = target;

    target.listRoot = Utils.findRequiredViewAsType(source, R.id.list_root, "field 'listRoot'", InterceptorLinearLayout.class);
    target.list = Utils.findRequiredViewAsType(source, R.id.list, "field 'list'", DynamicListView.class);
    target.searchLayout = Utils.findRequiredView(source, R.id.search_layout, "field 'searchLayout'");
    target.searchQueryView = Utils.findRequiredViewAsType(source, R.id.search_query, "field 'searchQueryView'", TextView.class);
    target.searchCancel = Utils.findRequiredViewAsType(source, R.id.search_cancel, "field 'searchCancel'", ImageView.class);
    target.empyListItem = Utils.findRequiredViewAsType(source, R.id.empty_list, "field 'empyListItem'", com.neopixl.pixlui.components.textview.TextView.class);
    target.expandedImageView = Utils.findRequiredViewAsType(source, R.id.expanded_image, "field 'expandedImageView'", ImageView.class);
    target.fabView = Utils.findRequiredView(source, R.id.fab, "field 'fabView'");
    target.undoBarView = Utils.findRequiredView(source, R.id.undobar, "field 'undoBarView'");
    target.progress_wheel = Utils.findRequiredViewAsType(source, R.id.progress_wheel, "field 'progress_wheel'", ProgressWheel.class);
    target.snackBarPlaceholder = Utils.findRequiredView(source, R.id.snackbar_placeholder, "field 'snackBarPlaceholder'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.listRoot = null;
    target.list = null;
    target.searchLayout = null;
    target.searchQueryView = null;
    target.searchCancel = null;
    target.empyListItem = null;
    target.expandedImageView = null;
    target.fabView = null;
    target.undoBarView = null;
    target.progress_wheel = null;
    target.snackBarPlaceholder = null;
  }
}
