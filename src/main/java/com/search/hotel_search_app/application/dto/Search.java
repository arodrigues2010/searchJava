package com.search.hotel_search_app.application.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Search {

    private String hotelId;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private List<Integer> ages;
    
    public Search() {
    }

    public Search(String hotelId, LocalDate checkIn, LocalDate checkOut,List<Integer> ages) {
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = ages;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

     public List<Integer> getAges() {
        return ages;
    }

    public void setAges(List<Integer> ages) {
        this.ages = ages != null ? ages : new ArrayList<>();
    }
}
