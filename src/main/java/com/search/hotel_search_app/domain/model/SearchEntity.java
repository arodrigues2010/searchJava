package com.search.hotel_search_app.domain.model;

import java.time.LocalDate;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("searches")  
public class SearchEntity {

    @Id
    private UUID id;  

    private String hotelId;

    private LocalDate checkIn;

    private LocalDate checkOut;
    
    public SearchEntity() {
    }

    public SearchEntity(String hotelId, LocalDate checkIn, LocalDate checkOut) {
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
    
    public UUID getId() {
        return id;
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

}
