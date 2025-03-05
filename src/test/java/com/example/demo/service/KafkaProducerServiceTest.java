package com.example.demo.service;

import com.example.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Product> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private Product product;
    private CompletableFuture<SendResult<String, Product>> completableFuture;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaProducerService, "topicName", "product-topic");
        
        product = Product.builder()
                .id("1")
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("19.99"))
                .category("Test Category")
                .build();
                
        completableFuture = new CompletableFuture<>();
    }

    @Test
    void sendProduct_ShouldSendProductToKafka() {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString(), any(Product.class)))
            .thenReturn(completableFuture);

        // Act
        kafkaProducerService.sendProduct(product);

        // Assert
        verify(kafkaTemplate, times(1)).send("product-topic", product.getId(), product);
    }

    @Test
    void sendProduct_WhenSendFails_ShouldHandleFailure() {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString(), any(Product.class)))
            .thenReturn(completableFuture);

        // Act
        kafkaProducerService.sendProduct(product);

        // Assert
        verify(kafkaTemplate, times(1)).send("product-topic", product.getId(), product);
    }
}
