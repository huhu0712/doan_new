package com.example.doan1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TourDetailActivity extends AppCompatActivity {

    private static final String API_KEY = "3058d9105emsh5289cdf3f4c04f4p1a2dd6jsn4927b6844d1c";
    private static final String API_HOST = "tripadvisor-com1.p.rapidapi.com";

    // TODO: Thay thế bằng API Key MapTiler của bạn
    private static final String MAPTILER_KEY = "2kaUl5OF1QGLqFs9H1WI";

    private TravelApiService apiService;
    private MapView mapDetail;
    private TextView tvName, tvLocation, tvDesc, tvRating;
    private ImageView ivCover;
    private LinearLayout containerReviews;
    private double lat = 0, lon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        ivCover = findViewById(R.id.ivTourDetailCover);
        tvName = findViewById(R.id.tvTourDetailName);
        tvLocation = findViewById(R.id.tvTourDetailLocation);
        tvDesc = findViewById(R.id.tvTourDetailDesc);
        tvRating = findViewById(R.id.tvTourDetailRating);
        mapDetail = findViewById(R.id.mapTourDetail);
        
        // Cấu hình MapTiler cho bản đồ tour (Streets-v2 + Scaled DPI)
        XYTileSource mapTilerSource = new XYTileSource(
                "MapTiler", 1, 20, 256, ".png?key=" + MAPTILER_KEY,
                new String[] { "https://api.maptiler.com/maps/streets-v2/256/" }
        );
        mapDetail.setTileSource(mapTilerSource);
        mapDetail.setTilesScaledToDpi(true); // Giúp bản đồ sắc nét
        mapDetail.setMultiTouchControls(true);

        containerReviews = findViewById(R.id.containerTourReviews);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        String contentId = getIntent().getStringExtra("contentId");

        setupRetrofit();
        if (contentId != null) {
            fetchDetails(contentId);
            fetchReviews(contentId);
        }

        findViewById(R.id.btnTourDirection).setOnClickListener(v -> {
            if (lat != 0 && lon != 0) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tripadvisor-com1.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(TravelApiService.class);
    }

    private void fetchDetails(String contentId) {
        apiService.getAttractionDetails(API_KEY, API_HOST, contentId, "miles").enqueue(new Callback<TravelModels.AttractionDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<TravelModels.AttractionDetailResponse> call, @NonNull Response<TravelModels.AttractionDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    TravelModels.AttractionDetailData data = response.body().data;
                    if (data.container != null && data.container.navTitle != null) {
                        tvName.setText(data.container.navTitle);
                        TranslationHelper.translate(data.container.navTitle, new TranslationHelper.OnTranslationListener() {
                            @Override
                            public void onTranslationSuccess(String translatedText) {
                                tvName.setText(translatedText);
                            }
                            @Override public void onTranslationFailure(Exception e) {}
                        });
                    }
                    for (TravelModels.Section section : data.sections) {
                        if ("AppPresentation_PoiHeroStandard".equals(section.typeName) && section.heroContent != null && !section.heroContent.isEmpty()) {
                            TravelModels.MediaItem media = section.heroContent.get(0);
                            if (media.data != null && media.data.photoSizeDynamic != null) {
                                String url = media.data.photoSizeDynamic.urlTemplate.replace("{width}", "1200").replace("{height}", "800");
                                Glide.with(TourDetailActivity.this).load(url).into(ivCover);
                            }
                        }
                        if ("AppPresentation_PoiOverview".equals(section.typeName)) {
                            String ratingInfo = (section.rating != null ? section.rating : "0") + " ★";
                            if (section.numberReviews != null) ratingInfo += " (" + section.numberReviews + " đánh giá)";
                            tvRating.setText(ratingInfo);
                        }
                        if ("AppPresentation_PoiLocationV2".equals(section.typeName) && section.address != null) {
                            tvLocation.setText(section.address.address);
                            if (section.address.geoPoint != null) {
                                lat = section.address.geoPoint.latitude;
                                lon = section.address.geoPoint.longitude;
                                setupMiniMap(lat, lon);
                            }
                        }
                        if ("AppPresentation_PoiAbout".equals(section.typeName) && section.htmlText != null) {
                            String descStr = section.htmlText.htmlString != null ? section.htmlText.htmlString : section.htmlText.text;
                            if (descStr != null) {
                                String cleanDesc = descStr.replaceAll("<[^>]*>", "");
                                tvDesc.setText(cleanDesc);
                                TranslationHelper.translateMultiLine(cleanDesc, new TranslationHelper.OnTranslationListener() {
                                    @Override
                                    public void onTranslationSuccess(String translatedText) {
                                        tvDesc.setText(translatedText);
                                    }
                                    @Override public void onTranslationFailure(Exception e) {}
                                });
                                setupExpandableText(tvDesc, findViewById(R.id.btnExpandTourDesc));
                            }
                        }
                    }
                }
            }
            @Override public void onFailure(@NonNull Call<TravelModels.AttractionDetailResponse> call, @NonNull Throwable t) {}
        });
    }

    private void fetchReviews(String contentId) {
        apiService.getAttractionReviews(API_KEY, API_HOST, contentId, "miles").enqueue(new Callback<TravelModels.ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<TravelModels.ReviewResponse> call, @NonNull Response<TravelModels.ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    containerReviews.removeAllViews();
                    for (TravelModels.ReviewSection s : response.body().data.sections) {
                        if ("AppPresentation_UserReviewSection".equals(s.typeName)) addReviewItem(s);
                    }
                }
            }
            @Override public void onFailure(@NonNull Call<TravelModels.ReviewResponse> call, @NonNull Throwable t) {}
        });
    }

    private void addReviewItem(TravelModels.ReviewSection review) {
        View view = getLayoutInflater().inflate(R.layout.item_tour_review, containerReviews, false);
        TextView tvUName = view.findViewById(R.id.tvReviewerName);
        TextView tvUComment = view.findViewById(R.id.tvReviewComment);
        TextView tvURating = view.findViewById(R.id.tvReviewRating);
        ImageView ivUAvatar = view.findViewById(R.id.ivReviewerAvatar);
        if (review.userProfile != null) tvUName.setText(review.userProfile.displayName);
        if (review.htmlText != null) {
            String comment = review.htmlText.htmlString != null ? review.htmlText.htmlString.replaceAll("<[^>]*>", "") : "";
            tvUComment.setText(comment);
            TranslationHelper.translate(comment, new TranslationHelper.OnTranslationListener() {
                @Override
                public void onTranslationSuccess(String translatedText) {
                    tvUComment.setText(translatedText);
                }
                @Override public void onTranslationFailure(Exception e) {}
            });
        }
        if (review.bubbleRating != null) tvURating.setText(review.bubbleRating.rating + " ★");
        if (review.userProfile != null && review.userProfile.avatar != null && review.userProfile.avatar.data != null) {
            String avatarUrl = review.userProfile.avatar.data.photoSizeDynamic.urlTemplate.replace("{width}", "100").replace("{height}", "100");
            Glide.with(this).load(avatarUrl).circleCrop().into(ivUAvatar);
        }
        containerReviews.addView(view);
    }

    private void setupMiniMap(double lat, double lon) {
        GeoPoint startPoint = new GeoPoint(lat, lon);
        mapDetail.getController().setZoom(15.0);
        mapDetail.getController().animateTo(startPoint);
        Marker marker = new Marker(mapDetail);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapDetail.getOverlays().add(marker);
    }

    private void setupExpandableText(TextView textView, TextView btn) {
        textView.setMaxLines(4);
        textView.post(() -> {
            if (textView.getLineCount() > 4) {
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(v -> {
                    if (textView.getMaxLines() == 4) {
                        textView.setMaxLines(Integer.MAX_VALUE);
                        btn.setText("Thu gọn");
                    } else {
                        textView.setMaxLines(4);
                        btn.setText("Xem thêm");
                    }
                });
            }
        });
    }

    @Override protected void onResume() { super.onResume(); if (mapDetail != null) mapDetail.onResume(); }
    @Override protected void onPause() { super.onPause(); if (mapDetail != null) mapDetail.onPause(); }
}
