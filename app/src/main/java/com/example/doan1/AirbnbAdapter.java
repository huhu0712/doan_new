package com.example.doan1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

public class AirbnbAdapter extends RecyclerView.Adapter<AirbnbAdapter.ViewHolder> {

    private List<AirbnbModels.StaySearchResult> listings;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AirbnbModels.StaySearchResult listing);
    }

    public AirbnbAdapter(List<AirbnbModels.StaySearchResult> listings, OnItemClickListener listener) {
        this.listings = listings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apartment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AirbnbModels.StaySearchResult item = listings.get(position);
        
        holder.tvName.setText(item.title);
        holder.tvRating.setText(item.avgRatingLocalized != null ? item.avgRatingLocalized : "N/A");
        holder.tvRatingWord.setText(item.subtitle);

        if (item.structuredDisplayPrice != null && item.structuredDisplayPrice.primaryLine != null && item.structuredDisplayPrice.primaryLine.price != null) {
            holder.tvPrice.setText(formatVndPrice(item.structuredDisplayPrice.primaryLine.price));
        } else {
            holder.tvPrice.setText("Hết phòng");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        if (item.contextualPictures != null && !item.contextualPictures.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.contextualPictures.get(0).picture)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .centerCrop()
                    .placeholder(R.drawable.logo)
                    .into(holder.ivImage);
        }
    }

    private String formatVndPrice(String rawPrice) {
        try {
            String clean = rawPrice.replaceAll("[^0-9]", "");
            if (clean.isEmpty()) return rawPrice;
            double value = Double.parseDouble(clean);
            if (value < 10000) value = value * 25400;
            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
            return formatter.format(value).replace(",", ".") + " VND";
        } catch (Exception e) {
            return rawPrice;
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvRating, tvRatingWord, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivHotelImage);
            tvName = itemView.findViewById(R.id.tvHotelName);
            tvRating = itemView.findViewById(R.id.tvHotelRating);
            tvRatingWord = itemView.findViewById(R.id.tvRatingWord);
            tvPrice = itemView.findViewById(R.id.tvHotelPrice);
        }
    }
}
