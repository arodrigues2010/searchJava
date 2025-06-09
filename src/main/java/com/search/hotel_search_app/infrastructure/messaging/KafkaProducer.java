package com.search.hotel_search_app.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.search.hotel_search_app.application.dto.Search;


import java.util.concurrent.CompletableFuture;

@Component
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, Search> kafkaTemplate;

    public KafkaProducer(@Qualifier("searchKafkaTemplate") KafkaTemplate<String, Search> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<Boolean> send(Search search) {
        if (search == null || search.getHotelId() == null) {
            logger.warn("❌ Search o hotelId es NULL. No se enviará el mensaje.");
            return CompletableFuture.completedFuture(false);
        }

        return kafkaTemplate.send("hotel_availability_searches", search.getHotelId(), search)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        logger.error("⚠️ Error al enviar mensaje: {}", exception.getMessage(), exception);
                    }
                })
                .thenApply(result -> {
                    if (result != null) {
                        logger.info("✅ Mensaje enviado correctamente: {}", search.getHotelId());
                        return true;
                    } else {
                        return false;
                    }
                });
    }
}
