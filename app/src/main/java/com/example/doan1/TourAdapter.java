package com.example.doan1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.ViewHolder> {

    private List<TravelModels.AttractionCard> tours;

    public TourAdapter(List<TravelModels.AttractionCard> tours) {
        this.tours = tours;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelModels.AttractionCard tour = tours.get(position);
        
        holder.tvTitle.setText(tour.cardTitle != null ? tour.cardTitle.string : "N/A");
        
        if (tour.bubbleRating != null) {
            holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f ★ %s", 
                tour.bubbleRating.rating, 
                tour.bubbleRating.numberReviews != null ? tour.bubbleRating.numberReviews.string : ""));
        } else {
            holder.tvRating.setText("N/A");
        }

        // Lấy giá và định dạng (Tripadvisor trả về text như "Tickets from $91")
        if (tour.merchandisingText != null && tour.merchandisingText.htmlString != null) {
            String priceText = tour.merchandisingText.htmlString.replaceAll("<[^>]*>", "");
            holder.tvPrice.setText(priceText);
        } else {
            holder.tvPrice.setText("Contact for price");
        }

        if (tour.cardPhoto != null && tour.cardPhoto.sizes != null && tour.cardPhoto.sizes.urlTemplate != null) {
            String imgUrl = tour.cardPhoto.sizes.urlTemplate.replace("{width}", "500").replace("{height}", "300");
            Glide.with(holder.itemView.getContext()).load(imgUrl).into(holder.ivImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), TourDetailActivity.class);
            intent.putExtra("contentId", tour.saveId != null ? tour.saveId.id : "");
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRating, tvPrice;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTourTitle);
            tvRating = itemView.findViewById(R.id.tvTourRating);
            tvPrice = itemView.findViewById(R.id.tvTourPrice);
            ivImage = itemView.findViewById(R.id.ivTourImage);
        }
    }
}
