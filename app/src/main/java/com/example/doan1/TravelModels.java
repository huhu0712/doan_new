package com.example.doan1;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class TravelModels {

    // --- 1. Auto Complete (Lấy geoId) ---
    public static class AutoCompleteResponse implements Serializable {
        @SerializedName("data")
        public List<TypeaheadResult> data;
        @SerializedName("status")
        public boolean status;
    }

    public static class TypeaheadResult implements Serializable {
        @SerializedName("geoId")
        public String geoId;
        @SerializedName("heading")
        public HtmlString heading;
        @SerializedName("secondaryTextLineOne")
        public LocalizedString secondaryText;
        @SerializedName("trackingItems")
        public TrackingItems trackingItems;
    }

    // --- 2. Attraction Search (Danh sách Tour) ---
    public static class AttractionSearchResponse implements Serializable {
        @SerializedName("data")
        public AttractionSearchData data;
    }

    public static class AttractionSearchData implements Serializable {
        @SerializedName("attractions")
        public List<AttractionCard> attractions;
    }

    public static class AttractionCard implements Serializable {
        @SerializedName("cardTitle")
        public LocalizedString cardTitle;
        @SerializedName("cardPhoto")
        public PhotoItem cardPhoto;
        @SerializedName("bubbleRating")
        public BubbleRating bubbleRating;
        @SerializedName("merchandisingText")
        public HtmlString merchandisingText; // Chứa giá tiền (e.g. Tickets from $91)
        @SerializedName("saveId")
        public SaveId saveId;
    }

    // --- 3. Attraction Details (Chi tiết Tour) ---
    public static class AttractionDetailResponse implements Serializable {
        @SerializedName("data")
        public AttractionDetailData data;
    }

    public static class AttractionDetailData implements Serializable {
        @SerializedName("sections")
        public List<Section> sections;
        @SerializedName("container")
        public DetailContainer container;
    }

    public static class Section implements Serializable {
        @SerializedName("__typename")
        public String typeName;
        @SerializedName("name")
        public String name;
        @SerializedName("rating")
        public Double rating;
        @SerializedName("numberReviews")
        public Integer numberReviews;
        @SerializedName("htmlText")
        public HtmlString htmlText;
        @SerializedName("address")
        public PoiAddress address;
        @SerializedName("heroContent")
        public List<MediaItem> heroContent;
    }

    // --- 4. Reviews ---
    public static class ReviewResponse implements Serializable {
        @SerializedName("data")
        public ReviewDataRoot data;
    }

    public static class ReviewDataRoot implements Serializable {
        @SerializedName("sections")
        public List<ReviewSection> sections;
    }

    public static class ReviewSection implements Serializable {
        @SerializedName("__typename")
        public String typeName;
        @SerializedName("htmlText")
        public HtmlString htmlText;
        @SerializedName("userProfile")
        public MemberProfile userProfile;
        @SerializedName("bubbleRating")
        public BubbleRating bubbleRating;
    }

    // --- Common Components ---
    public static class HtmlString implements Serializable {
        @SerializedName("htmlString")
        public String htmlString;
        @SerializedName("text")
        public String text;
    }

    public static class LocalizedString implements Serializable {
        @SerializedName("string")
        public String string;
    }

    public static class PhotoItem implements Serializable {
        @SerializedName("sizes")
        public PhotoSizes sizes;
    }

    public static class PhotoSizes implements Serializable {
        @SerializedName("urlTemplate")
        public String urlTemplate; // "https://...caption.jpg?w={width}&h={height}&s=1"
    }

    public static class BubbleRating implements Serializable {
        @SerializedName("rating")
        public double rating;
        @SerializedName("numberReviews")
        public LocalizedString numberReviews;
    }

    public static class SaveId implements Serializable {
        @SerializedName("id")
        public String id;
    }

    public static class TrackingItems implements Serializable {
        @SerializedName("locationId")
        public String locationId;
    }

    public static class PoiAddress implements Serializable {
        @SerializedName("address")
        public String address;
        @SerializedName("geoPoint")
        public GeoPoint geoPoint;
    }

    public static class GeoPoint implements Serializable {
        @SerializedName("latitude")
        public double latitude;
        @SerializedName("longitude")
        public double longitude;
    }

    public static class MediaItem implements Serializable {
        @SerializedName("data")
        public MediaData data;
    }

    public static class MediaData implements Serializable {
        @SerializedName("photoSizeDynamic")
        public PhotoSizes photoSizeDynamic;
    }

    public static class DetailContainer implements Serializable {
        @SerializedName("navTitle")
        public String navTitle;
    }

    public static class MemberProfile implements Serializable {
        @SerializedName("displayName")
        public String displayName;
        @SerializedName("avatar")
        public Avatar avatar;
    }

    public static class Avatar implements Serializable {
        @SerializedName("data")
        public AvatarData data;
    }

    public static class AvatarData implements Serializable {
        @SerializedName("photoSizeDynamic")
        public PhotoSizes photoSizeDynamic;
    }
}
