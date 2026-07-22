package com.example.doan1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<FilterItem> filterItems;

    public FilterAdapter(List<FilterItem> filterItems) {
        this.filterItems = filterItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterItem item = filterItems.get(position);
        holder.tvName.setText(item.getName());
        holder.cbFilter.setChecked(item.isSelected());
        holder.cbFilter.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));
    }

    @Override
    public int getItemCount() {
        return filterItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CheckBox cbFilter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFilterName);
            cbFilter = itemView.findViewById(R.id.cbFilter);
        }
    }
}