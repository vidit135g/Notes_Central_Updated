// Generated code from Butter Knife. Do not modify!
package com.absolute.android.notescentral;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.absolute.android.notescentral.models.views.InterceptorFrameLayout;
import it.feio.android.simplegallery.views.GalleryViewPager;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GalleryActivity_ViewBinding implements Unbinder {
  private GalleryActivity target;

  @UiThread
  public GalleryActivity_ViewBinding(GalleryActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public GalleryActivity_ViewBinding(GalleryActivity target, View source) {
    this.target = target;

    target.galleryRootView = Utils.findRequiredViewAsType(source, R.id.gallery_root, "field 'galleryRootView'", InterceptorFrameLayout.class);
    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.fullscreen_content, "field 'mViewPager'", GalleryViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    GalleryActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.galleryRootView = null;
    target.mViewPager = null;
  }
}
