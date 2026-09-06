package com.absolute.android.notescentral.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.absolute.android.notescentral.MainActivity;
import com.absolute.android.notescentral.R;
import com.absolute.android.notescentral.models.Category;
import com.absolute.android.notescentral.models.adapters.category.CategoryViewHolder;
import java.util.List;

public class CategoryBaseAdapter extends BaseAdapter {

    private final Activity mActivity;
    private final int layout;
    private final List<Category> categories;
    private final LayoutInflater inflater;
    private final String navigationTmp;

    public CategoryBaseAdapter(Activity mActivity, List<Category> categories) {
        this(mActivity, categories, null);
    }

    public CategoryBaseAdapter(Activity mActivity, List<Category> categories, String navigationTmp) {
        this.mActivity = mActivity;
        this.layout = R.layout.drawer_list_item;
        this.categories = categories;
        this.navigationTmp = navigationTmp;
        this.inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryViewHolder holder;
        Category category = categories.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
            holder = new CategoryViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CategoryViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(category.getName());

        if (isSelected(position)) {
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
            try {
                holder.txtTitle.setTextColor(Integer.parseInt(category.getColor()));
            } catch (Exception e) {
                // fallback
            }
        } else {
            holder.txtTitle.setTypeface(null, Typeface.NORMAL);
            holder.txtTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.drawer_text));
        }

        if (category.getColor() != null && category.getColor().length() > 0) {
            Drawable img = ContextCompat.getDrawable(mActivity, R.drawable.ic_folder_special_black_24dp);
            if (img != null) {
                try {
                    LightingColorFilter cf = new LightingColorFilter(Color.parseColor("#000000"), Integer.parseInt(category.getColor()));
                    img.mutate().setColorFilter(cf);
                    holder.imgIcon.setImageDrawable(img);
                } catch (Exception e) {
                    holder.imgIcon.setImageDrawable(img);
                }
            }
            int padding = 4;
            holder.imgIcon.setPadding(padding, padding, padding, padding);
        }

        if (mActivity.getSharedPreferences("com.absolute.android.notescentral_preferences", Context.MODE_PRIVATE).getBoolean("settings_show_category_count", true)) {
            holder.count.setText(String.valueOf(category.getCount()));
            holder.count.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private boolean isSelected(int position) {
        String[] navigationListCodes = mActivity.getResources().getStringArray(R.array.navigation_list_codes);
        String navigationTmpLocal = MainActivity.class.isAssignableFrom(mActivity.getClass()) ? ((MainActivity) mActivity).getNavigationTmp() : null;
        navigationTmpLocal = this.navigationTmp != null ? this.navigationTmp : navigationTmpLocal;
        String navigation = this.navigationTmp != null ? navigationTmpLocal : mActivity.getSharedPreferences("com.absolute.android.notescentral_preferences", Context.MODE_PRIVATE).getString("navigation", navigationListCodes[0]);
        return navigation.equals(String.valueOf(categories.get(position).getId()));
    }
}
