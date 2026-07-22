package com.example.doan1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<BookingModels.HotelItem> hotels;

    public BookingAdapter(List<BookingModels.HotelItem> hotels) {
        this.hotels = hotels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apartment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingModels.HotelItem hotel = hotels.get(position);
        
        holder.tvName.setText(hotel.hotelName);
        holder.tvRating.setText(String.valueOf(hotel.reviewScore));
        
        if (hotel.priceBreakdown != null && hotel.priceBreakdown.grossAmount != null) {
            holder.tvPrice.setText(hotel.priceBreakdown.grossAmount.amountRounded);
        } else {
            holder.tvPrice.setText("Hết phòng");
        }

        Glide.with(holder.itemView.getContext())
                .load(hotel.mainPhotoUrl)
                .placeholder(R.drawable.logo)
                .into(holder.ivImage);
                
        // Simple logic for rating word based on score
        if (hotel.reviewScore >= 9.0) holder.tvRatingWord.setText("Xuất sắc");
        else if (hotel.reviewScore >= 8.0) holder.tvRatingWord.setText("Tuyệt vời");
        else holder.tvRatingWord.setText("Rất tốt");
    }

    @Override
    public int getItemCount() {
        return hotels.size();
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