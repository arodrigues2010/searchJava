package com.search.hotel_search_app.domain.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("search_ages")
public class SearchAges {

    @Id
    private UUID id;  

    private int age;
    private String searchId;

    public SearchAges() {
    }

    public SearchAges(int age, String searchId) {
        this.age = age;
        this.searchId = searchId;
    }

    public String getSearchId() { 
        return searchId;
    }

    public void setSearchId(String searchId) { 
        this.searchId = searchId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
