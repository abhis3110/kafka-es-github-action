package com.example.demo.service;

import com.example.demo.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ProductService productService;

    @KafkaListener(
            topics = "${kafka.topic.product}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "productKafkaListenerContainerFactory"
    )
    public void consume(Product product) {
        log.info("Received product from Kafka: {}", product);
        productService.saveProduct(product);
        log.info("Product saved to Elasticsearch");
    }
}
