package com.search.hotel_search_app.application.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class ConfirmationService {
    private final ConcurrentHashMap<String, UUID> confirmations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CountDownLatch> latches = new ConcurrentHashMap<>();

    public void confirmProcessing(String hotelId, UUID searchId) {
        confirmations.put(hotelId, searchId);
        latches.putIfAbsent(hotelId, new CountDownLatch(1)); // Asegurar que haya un latch
        CountDownLatch latch = latches.get(hotelId);
        if (latch != null) {
            latch.countDown(); // Libera la espera
        }
    }

    public UUID waitForConfirmation(String hotelId) {
        CountDownLatch latch = latches.computeIfAbsent(hotelId, k -> new CountDownLatch(1));

        for (int attempt = 0; attempt < 3; attempt++) { // 3 intentos
            try {
                if (latch.getCount() == 0 || latch.await(5, TimeUnit.SECONDS)) {
                    return confirmations.remove(hotelId); // Se obtiene y elimina la confirmación
                }
                Thread.sleep(500); // Espera 500 ms antes de reintentar
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Si el hilo es interrumpido, salimos del bucle
            }
        }

        latches.remove(hotelId); // Limpia la referencia de espera si falla
        return null;
    }

}
