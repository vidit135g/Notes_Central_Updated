package com.absolute.android.notescentral.models.adapters.category;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.absolute.android.notescentral.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgIcon;
    public TextView txtTitle;
    public TextView count;

    public CategoryViewHolder(View view) {
        super(view);
        imgIcon = view.findViewById(R.id.icon);
        txtTitle = view.findViewById(R.id.title);
        count = view.findViewById(R.id.count);
    }
}
