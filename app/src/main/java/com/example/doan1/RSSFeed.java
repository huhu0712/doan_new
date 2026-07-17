package com.example.doan1;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rss", strict = false)
public class RSSFeed {
    @Element(name = "channel")
    private Channel channel;

    public List<RSSItem> getItems() {
        return channel != null ? channel.getItems() : null;
    }

    @Root(name = "channel", strict = false)
    public static class Channel {
        @ElementList(inline = true, name = "item", required = false)
        private List<RSSItem> items;

        public List<RSSItem> getItems() {
            return items;
        }
    }

    @Root(name = "item", strict = false)
    public static class RSSItem {
        @Element(name = "title", required = false)
        private String title;

        @Element(name = "description", required = false)
        private String description;

        @Element(name = "link", required = false)
        private String link;

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getLink() { return link; }
        
        public String getImageUrl() {
            if (description == null) return "";
            try {
                // Tìm link ảnh trong thẻ img src="..."
                if (description.contains("src=\"")) {
                    int start = description.indexOf("src=\"") + 5;
                    int end = description.indexOf("\"", start);
                    String url = description.substring(start, end);
                    if (url.startsWith("//")) {
                        url = "https:" + url;
                    }
                    return url;
                }
            } catch (Exception e) {
                return "";
            }
            return "";
        }
        
        public String getCleanDescription() {
            if (description == null) return "";
            try {
                // Xóa các thẻ HTML và thực thể &nbsp; vv
                String clean = description.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
                if (clean.length() > 100) {
                    clean = clean.substring(0, 100) + "...";
                }
                return clean;
            } catch (Exception e) {
                return description;
            }
        }
    }
}
