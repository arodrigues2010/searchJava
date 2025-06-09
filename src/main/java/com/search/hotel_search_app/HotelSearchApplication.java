package com.search.hotel_search_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Hotel Search API", version = "1.0", description = "API de búsqueda de hoteles"))
@ComponentScan("com.search.hotel_search_app")
public class HotelSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelSearchApplication.class, args);
    }
}
