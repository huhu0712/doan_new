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
import java.util.Locale;

public class CarRentalAdapter extends RecyclerView.Adapter<CarRentalAdapter.ViewHolder> {

    private List<CarRentalModels.CarRental> cars;

    public CarRentalAdapter(List<CarRentalModels.CarRental> cars) {
        this.cars = cars;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_rental, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarRentalModels.CarRental car = cars.get(position);
        
        if (car.vehicleInfo != null) {
            holder.tvName.setText(car.vehicleInfo.vName);
            holder.tvDetails.setText(String.format("%s · %s Chỗ", car.vehicleInfo.transmission, car.vehicleInfo.seats));
            Glide.with(holder.itemView.getContext())
                    .load(car.vehicleInfo.imageUrl)
                    .placeholder(R.drawable.logo)
                    .into(holder.ivImage);
        }

        if (car.supplierInfo != null) {
            holder.tvSupplier.setText("Hãng: " + car.supplierInfo.name);
        }

        if (car.pricingInfo != null) {
            holder.tvPrice.setText(String.format(Locale.getDefault(), "%,.0f %s", car.pricingInfo.price, car.pricingInfo.currency));
        }
    }

    @Override
    public int getItemCount() {
        return cars != null ? cars.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvSupplier, tvDetails, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCarImage);
            tvName = itemView.findViewById(R.id.tvCarName);
            tvSupplier = itemView.findViewById(R.id.tvCarSupplier);
            tvDetails = itemView.findViewById(R.id.tvCarDetails);
            tvPrice = itemView.findViewById(R.id.tvCarPrice);
        }
    }
}
