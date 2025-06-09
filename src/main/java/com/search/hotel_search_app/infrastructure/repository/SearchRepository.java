package com.search.hotel_search_app.infrastructure.repository;

import com.search.hotel_search_app.domain.model.SearchEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends CrudRepository<SearchEntity, UUID> {
    List<SearchEntity> findByHotelId(String hotelId);
}
