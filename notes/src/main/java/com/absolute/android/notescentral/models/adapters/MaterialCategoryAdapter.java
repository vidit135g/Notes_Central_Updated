package com.absolute.android.notescentral.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.neopixl.pixlui.components.textview.TextView;
import com.absolute.android.notescentral.MainActivity;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.models.Category;
import com.absolute.android.notescentral.utils.Constants;
import com.absolute.android.notescentral.utils.Fonts;
import java.util.List;

public class MaterialCategoryAdapter extends RecyclerView.Adapter<MaterialCategoryAdapter.ViewHolder> {

  public interface OnItemClickListener {
    void onItemClick(Category category);
  }

  private final Activity mActivity;
  private final List<Category> categories;
  private final String navigationTmp;
  private final OnItemClickListener listener;

  public MaterialCategoryAdapter(Activity mActivity, List<Category> categories, OnItemClickListener listener) {
    this(mActivity, categories, null, listener);
  }

  public MaterialCategoryAdapter(Activity mActivity, List<Category> categories, String navigationTmp, OnItemClickListener listener) {
    this.mActivity = mActivity;
    this.categories = categories;
    this.navigationTmp = navigationTmp;
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mActivity).inflate(R.layout.drawer_list_item, parent, false);
    Fonts.overrideTextSize(mActivity, mActivity.getSharedPreferences(Constants.PREFS_NAME,
        Context.MODE_MULTI_PROCESS), view);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Category category = categories.get(position);
    holder.txtTitle.setText(category.getName());

    if (isSelected(position)) {
      holder.txtTitle.setTypeface(null, Typeface.BOLD);
      try {
        holder.txtTitle.setTextColor(Integer.parseInt(category.getColor()));
      } catch (Exception ignored) {
      }
    } else {
      holder.txtTitle.setTypeface(null, Typeface.NORMAL);
      holder.txtTitle.setTextColor(mActivity.getResources().getColor(R.color.drawer_text));
    }

    if (category.getColor() != null && category.getColor().length() > 0) {
      try {
        Drawable img = mActivity.getResources().getDrawable(R.drawable.ic_folder_special_black_24dp);
        ColorFilter cf = new LightingColorFilter(Color.parseColor("#000000"), Integer.parseInt(category.getColor()));
        if (Build.VERSION.SDK_INT >= 16) {
          img.mutate().setColorFilter(cf);
        } else {
          img.setColorFilter(cf);
        }
        holder.imgIcon.setImageDrawable(img);
        int padding = 4;
        holder.imgIcon.setPadding(padding, padding, padding, padding);
      } catch (Exception ignored) {
      }
    }

    if (mActivity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS).getBoolean(
        "settings_show_category_count", true)) {
      holder.count.setText(String.valueOf(category.getCount()));
      holder.count.setVisibility(View.VISIBLE);
    } else {
      holder.count.setVisibility(View.GONE);
    }

    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClick(category);
      }
    });
  }

  @Override
  public int getItemCount() {
    return categories != null ? categories.size() : 0;
  }

  private boolean isSelected(int position) {
    if (position < 0 || position >= categories.size()) return false;
    String[] navigationListCodes = mActivity.getResources().getStringArray(R.array.navigation_list_codes);
    String navigationTmpLocal = MainActivity.class.isAssignableFrom(mActivity.getClass())
        ? ((MainActivity) mActivity).getNavigationTmp()
        : null;
    navigationTmpLocal = this.navigationTmp != null ? this.navigationTmp : navigationTmpLocal;

    String navigation = navigationTmp != null
        ? navigationTmpLocal
        : mActivity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS)
            .getString(Constants.PREF_NAVIGATION, navigationListCodes[0]);

    return navigation != null && navigation.equals(String.valueOf(categories.get(position).getId()));
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView imgIcon;
    TextView txtTitle;
    android.widget.TextView count;

    ViewHolder(View itemView) {
      super(itemView);
      imgIcon = itemView.findViewById(R.id.icon);
      txtTitle = itemView.findViewById(R.id.title);
      count = itemView.findViewById(R.id.count);
    }
  }
}
