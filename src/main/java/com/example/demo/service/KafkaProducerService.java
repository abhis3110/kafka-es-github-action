package com.example.demo.service;

import com.example.demo.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Product> kafkaTemplate;

    @Value("${kafka.topic.product}")
    private String topicName;

    public void sendProduct(Product product) {
        ListenableFuture<SendResult<String, Product>> future = 
                kafkaTemplate.send(topicName, product.getId(), product);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Product>>() {
            @Override
            public void onSuccess(SendResult<String, Product> result) {
                log.info("Sent product=[{}] with offset=[{}]", 
                        product, 
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send product=[{}] due to : {}", 
                        product, 
                        ex.getMessage());
            }
        });
    }
}
