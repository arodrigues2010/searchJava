package com.search.hotel_search_app.application.dto;


public class SearchResponse {
    private final String searchId;

    public SearchResponse(String searchId) {
        this.searchId = searchId;
    }

    public String getSearchId() {
        return searchId;
    }
}

