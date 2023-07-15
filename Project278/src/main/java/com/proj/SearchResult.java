package com.proj;

public class SearchResult {
    private String imageUrl;
    private String itemName;
    private String trimmedUrl;

    public SearchResult(String imageUrl, String itemName, String trimmedUrl) {
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.trimmedUrl = trimmedUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getItemName() {
        return itemName;
    }
    public String getTrimmedUrl() { return trimmedUrl; }
}