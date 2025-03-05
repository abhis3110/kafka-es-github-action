package com.example.demo.integration;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.KafkaConsumerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"product-topic"})
@Testcontainers
class ProductIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.9");

    static {
        elasticsearchContainer.start();
        System.setProperty("elasticsearch.container.host", 
                elasticsearchContainer.getHttpHostAddress());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private KafkaTemplate<String, Product> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.product}")
    private String topicName;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .name("Integration Test Product")
                .description("Integration Test Description")
                .price(new BigDecimal("29.99"))
                .category("Integration Test Category")
                .build();
        
        productRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void crudOperations_ShouldWorkCorrectly() throws Exception {
        // Create
        String productJson = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andReturn().getResponse().getContentAsString();

        Product createdProduct = objectMapper.readValue(productJson, Product.class);

        // Read
        mockMvc.perform(get("/api/products/" + createdProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdProduct.getId())))
                .andExpect(jsonPath("$.name", is(product.getName())));

        // Update
        createdProduct.setName("Updated Product Name");
        mockMvc.perform(put("/api/products/" + createdProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product Name")));

        // Delete
        mockMvc.perform(delete("/api/products/" + createdProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify product is deleted
        mockMvc.perform(get("/api/products/" + createdProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void kafkaConsumer_ShouldSaveProductToElasticsearch() throws Exception {
        // Send a product to Kafka
        kafkaTemplate.send(topicName, "test-key", product);

        // Wait for the consumer to process the message and save to Elasticsearch
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            // Search for the product by name
            assertThat(productRepository.findByNameContaining(product.getName())).isNotEmpty();
        });

        // Verify the product was saved correctly
        Product savedProduct = productRepository.findByNameContaining(product.getName()).get(0);
        assertThat(savedProduct.getName()).isEqualTo(product.getName());
        assertThat(savedProduct.getDescription()).isEqualTo(product.getDescription());
        assertThat(savedProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(savedProduct.getCategory()).isEqualTo(product.getCategory());
    }
}
