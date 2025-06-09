package com.search.hotel_search_app.infrastructure.repository;

import com.search.hotel_search_app.domain.model.SearchAges;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchAgesRepository extends CrudRepository<SearchAges, UUID> {

}
