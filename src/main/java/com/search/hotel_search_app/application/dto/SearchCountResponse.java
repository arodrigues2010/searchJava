package com.search.hotel_search_app.application.dto;

import java.util.UUID;

import com.search.hotel_search_app.domain.model.SearchEntity;

public class SearchCountResponse {
    private final UUID searchId;
    private final SearchEntity searchEntity;
    private final int count;

    public SearchCountResponse(UUID searchId, SearchEntity search, int count) {
        this.searchId = searchId;
        this.searchEntity = search;
        this.count = count; 
    }

    public UUID getSearchId() {
        return searchId;
    }

    public SearchEntity getSearch() {
        return searchEntity;
    }

    public int getCount() {
        return count;
    }
}

