package com.search.hotel_search_app.application.dto;

import java.time.LocalDate;
import java.util.List;

public class SearchRequest {

    private String hotelId;

    private LocalDate checkIn;

    private LocalDate checkOut;
    
    private List<Integer> ages;

    // Constructor vacío
    public SearchRequest() {}

    // Constructor con parámetros
    public SearchRequest(String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages) {
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = ages;
    }

    // Métodos Getters
    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public List<Integer> getAges() {
        return ages;
    }
}
