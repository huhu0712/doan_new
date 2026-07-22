package com.example.doan1;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class AirbnbModels {
    public static class AirbnbResponse implements Serializable {
        @SerializedName("data")
        public Data data;
        @SerializedName("status")
        public boolean status;
        @SerializedName("message")
        public String message;
        @SerializedName("errors")
        public Object errors; // Đổi sang Object để tránh lỗi BEGIN_ARRAY
        @SerializedName("error")
        public Object error;
    }

    public static class AutoCompleteResponse implements Serializable {
        @SerializedName("data")
        public List<Suggestion> data;
    }

    public static class Suggestion implements Serializable {
        @SerializedName("id")
        public String id;
        @SerializedName("value")
        public String value;
        @SerializedName("searchId")
        public String searchId;
        @SerializedName("display_name")
        public String displayName;
    }

    public static class Data implements Serializable {
        @SerializedName("searchResults")
        public List<StaySearchResult> searchResults;
    }

    public static class StaySearchResult implements Serializable {
        @SerializedName("listing")
        public Listing listing;
        @SerializedName("title")
        public String title;
        @SerializedName("subtitle")
        public String subtitle;
        @SerializedName("avgRatingLocalized")
        public String avgRatingLocalized;
        @SerializedName("contextualPictures")
        public List<ExplorePicture> contextualPictures;
        @SerializedName("structuredDisplayPrice")
        public StructuredDisplayPrice structuredDisplayPrice;
        @SerializedName("demandStayListing")
        public DemandStayListing demandStayListing;
        @SerializedName("structuredContent")
        public StructuredContent structuredContent;
        @SerializedName("pricingQuote")
        public PricingQuote pricingQuote; // Thêm trường này để lấy giá dự phòng
    }

    public static class PricingQuote implements Serializable {
        @SerializedName("structuredStayDisplayPrice")
        public StructuredDisplayPrice structuredStayDisplayPrice;
    }

    public static class StructuredContent implements Serializable {
        @SerializedName("primaryLine")
        public List<MainSectionMessage> primaryLine;
    }

    public static class MainSectionMessage implements Serializable {
        @SerializedName("body")
        public String body;
        @SerializedName("type")
        public String type; // Ví dụ: "BEDINFO", "BATHROOMINFO"
    }

    // --- Cấu trúc chi tiết mới (stays/detail) ---
    public static class DetailResponse implements Serializable {
        @SerializedName("data")
        public DetailData data;
        @SerializedName("status")
        public boolean status;
    }

    public static class DetailData implements Serializable {
        @SerializedName("sectionContainer")
        public List<SectionContainer> sectionContainer;
    }

    public static class SectionContainer implements Serializable {
        @SerializedName("sectionId")
        public String sectionId;
        @SerializedName("section")
        public SectionContent section;
    }

    public static class SectionContent implements Serializable {
        // Cho AMENITIES_DEFAULT
        @SerializedName("seeAllAmenitiesGroups")
        public List<AmenityGroup> seeAllAmenitiesGroups;
        
        // Cho DESCRIPTION_DEFAULT
        @SerializedName("htmlDescription")
        public HtmlDescription htmlDescription;
    }

    public static class AmenityGroup implements Serializable {
        @SerializedName("amenities")
        public List<AmenityItem> amenities;
    }

    public static class AmenityItem implements Serializable {
        @SerializedName("title")
        public String title;
    }

    public static class HtmlDescription implements Serializable {
        @SerializedName("htmlText")
        public String htmlText;
    }

    // --- Cấu trúc đánh giá (Khôi phục cấu trúc gốc và thêm class thiếu) ---
    public static class ReviewResponse implements Serializable {
        @SerializedName("data")
        public List<ReviewItem> data; // Hoặc ReviewDataRoot tùy vào API thực tế, nhưng Activity đang dùng List
        @SerializedName("status")
        public boolean status;
    }

    public static class ReviewItem implements Serializable {
        @SerializedName("comments")
        public String comments;
        @SerializedName("reviewer")
        public Reviewer reviewer;
    }

    public static class Reviewer implements Serializable {
        @SerializedName("firstName")
        public String firstName;
    }

    public static class ReviewDataRoot implements Serializable {
        @SerializedName("sections")
        public List<ReviewSection> sections;
    }

    public static class ReviewSection implements Serializable {
        @SerializedName("__typename")
        public String typeName;
        @SerializedName("htmlText")
        public HtmlDescription htmlText;
        @SerializedName("userProfile")
        public ReviewAuthor userProfile;
        @SerializedName("bubbleRating")
        public BubbleRating bubbleRating;
    }

    public static class BubbleRating implements Serializable {
        @SerializedName("rating")
        public double rating;
    }

    public static class ReviewAuthor implements Serializable {
        @SerializedName("displayName")
        public String displayName;
        @SerializedName("avatar")
        public AvatarData avatar;
    }

    public static class AvatarData implements Serializable {
        @SerializedName("data")
        public ExplorePicture data;
    }

    public static class Listing implements Serializable {
        @SerializedName("id")
        public String id;
    }

    public static class ExplorePicture implements Serializable {
        @SerializedName("picture")
        public String picture;
    }

    public static class StructuredDisplayPrice implements Serializable {
        @SerializedName("primaryLine")
        public PrimaryLine primaryLine;
    }

    public static class PrimaryLine implements Serializable {
        @SerializedName("price")
        public String price; // Dạng chuỗi như "$513"
    }

    public static class DemandStayListing implements Serializable {
        @SerializedName("location")
        public Location location;
    }

    public static class Location implements Serializable {
        @SerializedName("coordinate")
        public Coordinate coordinate;
        @SerializedName("localizedCityName")
        public String localizedCityName;
    }

    public static class Coordinate implements Serializable {
        @SerializedName("latitude")
        public double latitude;
        @SerializedName("longitude")
        public double longitude;
    }
}
