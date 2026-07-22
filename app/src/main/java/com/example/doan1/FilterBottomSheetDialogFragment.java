package com.example.doan1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private String title;
    private List<FilterItem> items;
    private FilterAdapter adapter;
    private OnFilterAppliedListener listener;

    public interface OnFilterAppliedListener {
        void onFilterApplied(String title, List<String> selectedItems);
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    public static FilterBottomSheetDialogFragment newInstance(String title, ArrayList<String> itemNames) {
        FilterBottomSheetDialogFragment fragment = new FilterBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArrayList("items", itemNames);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            ArrayList<String> itemNames = getArguments().getStringArrayList("items");
            items = new ArrayList<>();
            if (itemNames != null) {
                for (String name : itemNames) {
                    items.add(new FilterItem(name, false));
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false);

        TextView tvTitle = view.findViewById(R.id.tvSheetTitle);
        tvTitle.setText(title);

        RecyclerView rvItems = view.findViewById(R.id.rvFilterItems);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FilterAdapter(items);
        rvItems.setAdapter(adapter);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnClear).setOnClickListener(v -> {
            for (FilterItem item : items) item.setSelected(false);
            adapter.notifyDataSetChanged();
        });
        view.findViewById(R.id.btnApply).setOnClickListener(v -> {
            if (listener != null) {
                List<String> selected = new ArrayList<>();
                for (FilterItem item : items) {
                    if (item.isSelected()) selected.add(item.getName());
                }
                listener.onFilterApplied(title, selected);
            }
            dismiss();
        });

        return view;
    }
}