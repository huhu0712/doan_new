package com.example.doan1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.LinearLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import androidx.core.content.ContextCompat;

public class ApartmentDetailActivity extends AppCompatActivity {

    private static final String API_KEY = "3058d9105emsh5289cdf3f4c04f4p1a2dd6jsn4927b6844d1c";
    private static final String API_HOST = "airbnb-search.p.rapidapi.com";

    // TODO: Thay thế bằng API Key MapTiler của bạn
    private static final String MAPTILER_KEY = "2kaUl5OF1QGLqFs9H1WI";

    private TextView tvAmenities;
    private TextView tvDesc;
    private TextView tvBeds, tvBaths, tvRooms;
    private LinearLayout containerReviews;
    private AirbnbApiService apiService;
    private MapView mapSmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment_detail);

        // Khởi tạo apiService dùng chung
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://airbnb-search.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(AirbnbApiService.class);

        AirbnbModels.StaySearchResult item = (AirbnbModels.StaySearchResult) getIntent().getSerializableExtra("listing");
        if (item == null) {
            finish();
            return;
        }

        ViewPager2 viewPager = findViewById(R.id.viewPagerImages);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        TextView tvRating = findViewById(R.id.tvDetailRating);
        TextView tvReviews = findViewById(R.id.tvDetailReviewCount);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        tvRooms = findViewById(R.id.tvDetailRooms);
        tvBeds = findViewById(R.id.tvDetailBeds);
        tvBaths = findViewById(R.id.tvDetailBaths);
        tvDesc = findViewById(R.id.tvDetailDesc);
        tvAmenities = findViewById(R.id.tvDetailAmenities);
        containerReviews = findViewById(R.id.containerReviews);
        mapSmall = findViewById(R.id.mapDetail);
        
        // Cấu hình MapTiler cho bản đồ nhỏ (Streets-v2 + Scaled DPI cho độ nét cao)
        XYTileSource mapTilerSource = new XYTileSource(
                "MapTiler", 1, 20, 256, ".png?key=" + MAPTILER_KEY,
                new String[] { "https://api.maptiler.com/maps/streets-v2/256/" }
        );
        mapSmall.setTileSource(mapTilerSource);
        mapSmall.setTilesScaledToDpi(true); // Giúp bản đồ sắc nét
        mapSmall.setMultiTouchControls(false);

        Button btnBook = findViewById(R.id.btnBookNow);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvName.setText(item.title != null ? item.title : "Căn hộ không tên");
        if (item.title != null) {
            TranslationHelper.translate(item.title, new TranslationHelper.OnTranslationListener() {
                @Override
                public void onTranslationSuccess(String translatedText) {
                    tvName.setText(translatedText);
                }
                @Override public void onTranslationFailure(Exception e) {}
            });
        }
        tvLocation.setText(item.demandStayListing != null && item.demandStayListing.location != null ? 
                item.demandStayListing.location.localizedCityName : "Địa điểm linh hoạt");
        
        tvRating.setText(item.avgRatingLocalized != null ? item.avgRatingLocalized : "Mới");
        tvReviews.setText(""); 
        
        if (item.structuredDisplayPrice != null && item.structuredDisplayPrice.primaryLine != null && item.structuredDisplayPrice.primaryLine.price != null) {
            tvPrice.setText(formatVndPrice(item.structuredDisplayPrice.primaryLine.price));
        } else {
            tvPrice.setText("Liên hệ");
        }

        String subtitle = item.subtitle != null ? item.subtitle : "";
        tvDesc.setText(subtitle);
        TranslationHelper.translateMultiLine(subtitle, new TranslationHelper.OnTranslationListener() {
            @Override
            public void onTranslationSuccess(String translatedText) {
                tvDesc.setText(translatedText);
            }
            @Override public void onTranslationFailure(Exception e) {}
        });
        
        TextView btnExpandDesc = findViewById(R.id.btnExpandDesc);
        
        View.OnClickListener expandAction = v -> {
            if (tvDesc.getMaxLines() == 4) {
                tvDesc.setMaxLines(Integer.MAX_VALUE);
                btnExpandDesc.setText("Thu gọn");
            } else {
                tvDesc.setMaxLines(4);
                btnExpandDesc.setText("Xem thêm");
            }
        };
        tvDesc.setOnClickListener(expandAction);
        btnExpandDesc.setOnClickListener(expandAction);
        tvDesc.post(() -> {
            if (tvDesc.getLineCount() > 4) btnExpandDesc.setVisibility(View.VISIBLE);
        });

        if (item.structuredContent != null && item.structuredContent.primaryLine != null) {
            for (AirbnbModels.MainSectionMessage msg : item.structuredContent.primaryLine) {
                if (msg != null && msg.type != null && msg.body != null) {
                    if (msg.type.contains("BED")) tvBeds.setText(msg.body);
                    else if (msg.type.contains("BATH")) tvBaths.setText(msg.body);
                    else if (msg.type.contains("BEDROOM")) tvRooms.setText(msg.body);
                }
            }
        }

        if (item.contextualPictures != null && !item.contextualPictures.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (AirbnbModels.ExplorePicture p : item.contextualPictures) {
                if (p != null && p.picture != null) imageUrls.add(p.picture);
            }
            ImageSliderAdapter adapter = new ImageSliderAdapter(imageUrls);
            viewPager.setAdapter(adapter);
        }

        if (item.demandStayListing != null && item.demandStayListing.location != null && item.demandStayListing.location.coordinate != null) {
            double lat = item.demandStayListing.location.coordinate.latitude;
            double lng = item.demandStayListing.location.coordinate.longitude;
            GeoPoint point = new GeoPoint(lat, lng);
            mapSmall.getController().setZoom(15.5);
            mapSmall.getController().animateTo(point);
            
            Marker marker = new Marker(mapSmall);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_myplaces));
            mapSmall.getOverlays().add(marker);
        }

        if (item.listing != null && item.listing.id != null) {
            btnBook.setOnClickListener(v -> {
                String url = "https://www.airbnb.com/rooms/" + item.listing.id;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            });

            findViewById(R.id.btnViewOnMap).setOnClickListener(v -> {
                if (item.demandStayListing != null && item.demandStayListing.location != null && item.demandStayListing.location.coordinate != null) {
                    double lat = item.demandStayListing.location.coordinate.latitude;
                    double lng = item.demandStayListing.location.coordinate.longitude;
                    String label = item.title;
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", lat, lng, lat, lng, label);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    mapIntent.setPackage("com.google.android.apps.maps");
                    try {
                        startActivity(mapIntent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Không tìm thấy ứng dụng Bản đồ", Toast.LENGTH_SHORT).show();
                        String webUri = "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng;
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUri)));
                    }
                } else {
                    Toast.makeText(this, "Tọa độ không khả dụng", Toast.LENGTH_SHORT).show();
                }
            });

            fetchDetailsAndReviews(item.listing.id);
        } else {
            btnBook.setEnabled(false);
            btnBook.setText("Không khả dụng");
            tvAmenities.setText("Không có dữ liệu chi tiết.");
            containerReviews.removeAllViews();
            addNoReviewText();
        }
    }

    private String formatVndPrice(String rawPrice) {
        try {
            String clean = rawPrice.replaceAll("[^0-9]", "");
            if (clean.isEmpty()) return rawPrice;
            double value = Double.parseDouble(clean);
            if (value < 10000) value = value * 25400; 
            java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
            return formatter.format(value) + " VND";
        } catch (Exception e) {
            return rawPrice;
        }
    }

    private void fetchDetailsAndReviews(String listingId) {
        apiService.getPropertyDetail(API_KEY, API_HOST, listingId, "vi-VN").enqueue(new Callback<AirbnbModels.DetailResponse>() {
            @Override
            public void onResponse(Call<AirbnbModels.DetailResponse> call, Response<AirbnbModels.DetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null && response.body().data.sectionContainer != null) {
                    List<AirbnbModels.SectionContainer> sections = response.body().data.sectionContainer;
                    StringBuilder amenitiesText = new StringBuilder();
                    String fullDesc = "";
                    for (AirbnbModels.SectionContainer section : sections) {
                        if ("AMENITIES_DEFAULT".equals(section.sectionId) && section.section != null && section.section.seeAllAmenitiesGroups != null) {
                            for (AirbnbModels.AmenityGroup group : section.section.seeAllAmenitiesGroups) {
                                if (group.amenities != null) {
                                    for (AirbnbModels.AmenityItem amenity : group.amenities) {
                                        amenitiesText.append("• ").append(amenity.title).append("\n");
                                    }
                                }
                            }
                        } else if ("DESCRIPTION_DEFAULT".equals(section.sectionId) && section.section != null && section.section.htmlDescription != null) {
                            fullDesc = section.section.htmlDescription.htmlText;
                        }
                    }
                    if (amenitiesText.length() > 0) {
                        tvAmenities.setText(amenitiesText.toString());
                        TranslationHelper.translateMultiLine(amenitiesText.toString(), new TranslationHelper.OnTranslationListener() {
                            @Override
                            public void onTranslationSuccess(String translatedText) {
                                tvAmenities.setText(translatedText);
                                setupExpandableAmenities(tvAmenities, findViewById(R.id.btnExpandAmenities));
                            }
                            @Override public void onTranslationFailure(Exception e) {}
                        });
                        setupExpandableAmenities(tvAmenities, findViewById(R.id.btnExpandAmenities));
                    }
                    else tvAmenities.setText("Không có thông tin tiện nghi.");
                    if (!fullDesc.isEmpty()) {
                        String cleanDesc = android.text.Html.fromHtml(fullDesc, android.text.Html.FROM_HTML_MODE_COMPACT).toString();
                        tvDesc.setText(cleanDesc);
                        TranslationHelper.translateMultiLine(cleanDesc, new TranslationHelper.OnTranslationListener() {
                            @Override
                            public void onTranslationSuccess(String translatedText) {
                                tvDesc.setText(translatedText);
                                checkExpandableDescription();
                            }
                            @Override public void onTranslationFailure(Exception e) {}
                        });
                        checkExpandableDescription();
                    }
                }
            }
            @Override public void onFailure(Call<AirbnbModels.DetailResponse> call, Throwable t) {}
        });

        apiService.getPropertyReviews(API_KEY, API_HOST, listingId, "vi-VN").enqueue(new Callback<AirbnbModels.ReviewResponse>() {
            @Override
            public void onResponse(Call<AirbnbModels.ReviewResponse> call, Response<AirbnbModels.ReviewResponse> response) {
                containerReviews.removeAllViews();
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<AirbnbModels.ReviewItem> reviews = response.body().data;
                    int count = 0;
                    for (AirbnbModels.ReviewItem review : reviews) {
                        if (review != null && review.comments != null) {
                            TextView tv = new TextView(ApartmentDetailActivity.this);
                            String authorName = (review.reviewer != null && review.reviewer.firstName != null) ? review.reviewer.firstName : "Khách";
                            
                            // Tách riêng tên và nội dung để dịch chính xác hơn
                            tv.setText(authorName + ": " + review.comments + "\n---");
                            tv.setPadding(0, 10, 0, 20);
                            tv.setTextColor(0xFF555555);
                            if (count >= 2) tv.setVisibility(View.GONE);
                            containerReviews.addView(tv);
                            
                            final String reviewerName = authorName;
                            TranslationHelper.translate(review.comments, new TranslationHelper.OnTranslationListener() {
                                @Override
                                public void onTranslationSuccess(String translatedText) {
                                    tv.setText(reviewerName + ": " + translatedText + "\n---");
                                }
                                @Override public void onTranslationFailure(Exception e) {}
                            });
                            
                            count++;
                        }
                    }
                    if (count > 2) {
                        TextView btnExpandReviews = findViewById(R.id.btnExpandReviews);
                        btnExpandReviews.setVisibility(View.VISIBLE);
                        btnExpandReviews.setOnClickListener(v -> {
                            boolean isExpanding = btnExpandReviews.getText().equals("Xem thêm đánh giá");
                            for (int i = 2; i < containerReviews.getChildCount(); i++) {
                                containerReviews.getChildAt(i).setVisibility(isExpanding ? View.VISIBLE : View.GONE);
                            }
                            btnExpandReviews.setText(isExpanding ? "Thu gọn đánh giá" : "Xem thêm đánh giá");
                        });
                    }
                    if (reviews.isEmpty()) addNoReviewText();
                } else addNoReviewText();
            }
            @Override public void onFailure(Call<AirbnbModels.ReviewResponse> call, Throwable t) { addNoReviewText(); }
        });
    }

    private void addNoReviewText() {
        TextView tv = new TextView(this);
        tv.setText("Chưa có đánh giá nào cho căn hộ này.");
        containerReviews.addView(tv);
    }

    private void checkExpandableDescription() {
        TextView btnExpandDesc = findViewById(R.id.btnExpandDesc);
        tvDesc.post(() -> {
            if (tvDesc.getLineCount() > 4) {
                btnExpandDesc.setVisibility(View.VISIBLE);
            } else {
                btnExpandDesc.setVisibility(View.GONE);
            }
        });
    }

    private void setupExpandableAmenities(TextView textView, TextView btn) {
        textView.setMaxLines(6);
        View.OnClickListener action = v -> {
            if (textView.getMaxLines() == 6) {
                textView.setMaxLines(Integer.MAX_VALUE);
                btn.setText("Thu gọn");
            } else {
                textView.setMaxLines(6);
                btn.setText("Xem tất cả");
            }
        };
        textView.setOnClickListener(action);
        btn.setOnClickListener(action);
        textView.post(() -> {
            if (textView.getLineCount() > 6) {
                btn.setVisibility(View.VISIBLE);
            } else {
                btn.setVisibility(View.GONE);
            }
        });
    }

    @Override protected void onResume() { super.onResume(); if (mapSmall != null) mapSmall.onResume(); }
    @Override protected void onPause() { super.onPause(); if (mapSmall != null) mapSmall.onPause(); }
}
