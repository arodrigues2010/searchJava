package com.search.hotel_search_app.infrastructure.messaging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.search.hotel_search_app.application.dto.Search;
import com.search.hotel_search_app.application.service.ConfirmationService;
import com.search.hotel_search_app.domain.model.SearchAges;
import com.search.hotel_search_app.domain.model.SearchEntity;
import com.search.hotel_search_app.infrastructure.repository.SearchAgesRepository;
import com.search.hotel_search_app.infrastructure.repository.SearchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

@Component
@KafkaListener(topics = "hotel_availability_searches", groupId = "hotel_search_group", containerFactory = "searchKafkaListenerContainerFactory")
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final SearchRepository searchRepository;
    private final ConfirmationService confirmationService;

    @Autowired
    private DataSource dataSource;

    public KafkaConsumer(SearchRepository searchRepository,
            ConfirmationService confirmationService,
            SearchAgesRepository searchagesRepository) {
        this.searchRepository = searchRepository;
        this.confirmationService = confirmationService;

    }

    @KafkaHandler
    @Transactional
    public void consume(Search search) {
        if (search == null || search.getHotelId() == null) {
            logger.warn("❌ Mensaje recibido es NULL o el hotelId es inválido.");
            return;
        }

        try {

            SearchEntity searchEntity = mapToEntity(search);

            SearchEntity savedSearch = searchRepository.save(searchEntity);

            logger.info("✅ ID generado: {}", savedSearch.getId());

            String sql = "INSERT INTO search_ages (age, search_id) VALUES (?, ?)";
            
            if (search.getHotelId() != null && savedSearch.getId() != null) {

                try (Connection conn = dataSource.getConnection();

                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    if (search.getAges() != null && !search.getAges().isEmpty()) {
                        for (Integer age : search.getAges()) {
                            pstmt.setInt(1, age);
                            pstmt.setString(2, savedSearch.getId().toString());

                            pstmt.addBatch(); // Agregar la sentencia al batch
                        }
                        pstmt.executeBatch(); // Ejecutar todas las inserciones a la vez
                    }
                } catch (SQLException e) {
                    e.printStackTrace(); // Manejo de errores
                }

                // Llamar a confirmProcessing
                confirmationService.confirmProcessing(search.getHotelId(), savedSearch.getId());
            }

        } catch (Exception e) {
            logger.error("⚠️ Error al guardar en la base de datos: {}", e.getMessage(), e);
        }

    }

    public static SearchEntity mapToEntity(Search search) {
        return new SearchEntity(
                search.getHotelId(),
                search.getCheckIn(),
                search.getCheckOut());
    }

}
