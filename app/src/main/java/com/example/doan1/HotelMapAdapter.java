package com.example.doan1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

import android.widget.Button;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class HotelMapAdapter extends RecyclerView.Adapter<HotelMapAdapter.HotelViewHolder> {

    private List<Hotel> hotels;
    private OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public HotelMapAdapter(List<Hotel> hotels, OnHotelClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel_map, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.tvName.setText(hotel.getName());
        holder.tvAddress.setText(hotel.getAddress());
        
        // Định dạng VND chuyên nghiệp
        if (hotel.getPrice() > 0) {
            java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
            holder.tvPrice.setText(formatter.format(hotel.getPrice()) + " VND");
        } else {
            holder.tvPrice.setText("Liên hệ");
        }
        
        holder.rbRating.setRating((float) hotel.getRating()); 

        Glide.with(holder.itemView.getContext())
                .load(hotel.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivHotel);

        holder.itemView.setOnClickListener(v -> listener.onHotelClick(hotel));

        holder.btnDirections.setOnClickListener(v -> {
            String label = hotel.getName();
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", 
                hotel.getLatitude(), hotel.getLongitude(), 
                hotel.getLatitude(), hotel.getLongitude(), label);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            try {
                holder.itemView.getContext().startActivity(intent);
            } catch (Exception e) {
                String webUri = "https://www.google.com/maps/search/?api=1&query=" + hotel.getLatitude() + "," + hotel.getLongitude();
                holder.itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUri)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotels == null ? 0 : hotels.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotel;
        TextView tvName, tvAddress, tvPrice;
        RatingBar rbRating;
        Button btnDirections;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHotel = itemView.findViewById(R.id.ivHotelMap);
            tvName = itemView.findViewById(R.id.tvHotelNameMap);
            tvAddress = itemView.findViewById(R.id.tvHotelAddressMap);
            tvPrice = itemView.findViewById(R.id.tvHotelPriceMap);
            rbRating = itemView.findViewById(R.id.rbHotelMap);
            btnDirections = itemView.findViewById(R.id.btnDirectionsMap);
        }
    }
}
