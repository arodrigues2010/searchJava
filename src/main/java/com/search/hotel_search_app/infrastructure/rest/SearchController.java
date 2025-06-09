package com.search.hotel_search_app.infrastructure.rest;

import com.search.hotel_search_app.application.dto.Search;
import com.search.hotel_search_app.application.dto.SearchCountResponse;

import com.search.hotel_search_app.application.dto.SearchRequest;
import com.search.hotel_search_app.application.service.ConfirmationService;

import com.search.hotel_search_app.domain.model.SearchEntity;
import com.search.hotel_search_app.infrastructure.messaging.KafkaProducer;
import com.search.hotel_search_app.infrastructure.repository.SearchAgesRepository;
import com.search.hotel_search_app.infrastructure.repository.SearchRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final KafkaProducer kafkaProducer;
    private final ConfirmationService confirmationService;
    private final SearchRepository searchRepository;
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);


    public SearchController(KafkaProducer kafkaProducer, ConfirmationService confirmationService,
            SearchRepository searchRepository, SearchAgesRepository searchagesRepository) {
        this.kafkaProducer = kafkaProducer;
        this.confirmationService = confirmationService;
        this.searchRepository = searchRepository;

    }

    @PostMapping
    @Operation(summary = "search", description = "Retorna el ID cuando el mensaje ha sido procesado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> search(@RequestBody SearchRequest request) {
        logger.info("📥 Procesando búsqueda para hotelId: {}", request.getHotelId());

        validateAges(request);

        Search search = new Search(request.getHotelId(), request.getCheckIn(), request.getCheckOut(),
                request.getAges());

        try {
            CompletableFuture<Boolean> kafkaResult = kafkaProducer.send(search)
                    .orTimeout(20, TimeUnit.SECONDS);

            boolean sentSuccessfully = kafkaResult.join();
            if (!sentSuccessfully) {
                logger.error("🚨 Error enviando búsqueda a Kafka");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("⚠️ No se pudo enviar la búsqueda a Kafka");
            }

            UUID searchId = confirmationService.waitForConfirmation(search.getHotelId());
            if (searchId == null) {
                logger.warn("⏳ No se recibió confirmación del procesamiento");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("⚠️ No se recibió confirmación del procesamiento.");
            }

            logger.info("✅ Búsqueda procesada con éxito, ID: {}", searchId);

            return ResponseEntity.ok("✅ Búsqueda procesada correctamente con ID: " + searchId);

        } catch (Exception e) {
            logger.error("🚨 Error en la búsqueda: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Se produjo un error interno en la búsqueda.");
        }
    }

    @GetMapping
    @Operation(summary = "count", description = "Retorna el ID cuando el mensaje ha sido procesado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "404", description = "Búsqueda no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SearchCountResponse> getSearchCount(@RequestParam UUID searchId) {
        if (searchId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Optional<SearchEntity> searchOpt = searchRepository.findById(searchId);

        if (searchOpt.isPresent()) {
            SearchEntity search = searchOpt.get();
            List<SearchEntity> count = searchRepository.findByHotelId(search.getHotelId());
            return ResponseEntity.ok(new SearchCountResponse(search.getId(), search, count.size()));
        } else {
            logger.warn("🔍 No se encontró la búsqueda con ID: {}", searchId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SearchCountResponse(null, null, 0));
        }

    }

    private void validateAges(SearchRequest request) {

        // Validación de valores nulos
        Objects.requireNonNull(request, "❌ La solicitud de búsqueda no puede ser nula.");
        Objects.requireNonNull(request.getAges(), "❌ Las edades son obligatorias.");
        Objects.requireNonNull(request.getHotelId(), "❌ El hotelId es obligatorio.");
        Objects.requireNonNull(request.getCheckIn(), "❌ La fecha de check-in es obligatoria.");
        Objects.requireNonNull(request.getCheckOut(), "❌ La fecha de check-out es obligatoria.");

        // Validación de edades
        if (request.getAges().isEmpty() || request.getAges().stream().anyMatch(age -> age < 1 || age > 90)) {
            throw new IllegalArgumentException("❌ Todas las edades deben estar entre 1 y 90 años.");
        }

        LocalDate today = LocalDate.now();

        // Validación de fechas
        if (request.getCheckIn().isBefore(today) || request.getCheckOut().isBefore(today)) {
            throw new IllegalArgumentException("❌ Las fechas de check-in y check-out no pueden ser anteriores a hoy.");
        }

        if (!request.getCheckIn().isBefore(request.getCheckOut())) {
            throw new IllegalArgumentException("❌ La fecha de check-in debe ser antes de la fecha de check-out.");
        }
    }

}
