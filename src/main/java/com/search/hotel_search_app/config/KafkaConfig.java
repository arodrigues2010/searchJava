package com.search.hotel_search_app.config;

import com.search.hotel_search_app.application.dto.Search;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    // ——— PRODUCER (SIN TRANSACCIONES) 
    @Bean
    public ProducerFactory<String, Search> searchProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Cambia si usas otro host
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Search> searchKafkaTemplate() {
        return new KafkaTemplate<>(searchProducerFactory());
    }

    // ——— CONSUMER (SIN TRANSACCIONES) ———
    @Bean
    public ConsumerFactory<String, Search> searchConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Cambia si usas otro host
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "hotel_search_group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<Search> deserializer = new JsonDeserializer<>(Search.class, false);
        deserializer.addTrustedPackages("com.search.hotel_search_app.domain.dto");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Search> searchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Search> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(searchConsumerFactory());
        return factory;
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic("hotel_availability_searches", 1, (short) 1);
    }
}
