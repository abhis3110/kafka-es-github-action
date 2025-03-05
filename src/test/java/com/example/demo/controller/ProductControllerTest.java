package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.KafkaProducerService;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("1")
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("19.99"))
                .category("Test Category")
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(product.getId())))
                .andExpect(jsonPath("$[0].name", is(product.getName())));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById("1")).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId())))
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productService, times(1)).getProductById("1");
    }

    @Test
    void createProduct_WithValidProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        when(productService.saveProduct(any(Product.class))).thenReturn(product);
        doNothing().when(kafkaProducerService).sendProduct(any(Product.class));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(product.getId())))
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productService, times(1)).saveProduct(any(Product.class));
        verify(kafkaProducerService, times(1)).sendProduct(any(Product.class));
    }

    @Test
    void updateProduct_WithExistingIdAndValidProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        when(productService.updateProduct(eq("1"), any(Product.class))).thenReturn(product);
        doNothing().when(kafkaProducerService).sendProduct(any(Product.class));

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId())))
                .andExpect(jsonPath("$.name", is(product.getName())));

        verify(productService, times(1)).updateProduct(eq("1"), any(Product.class));
        verify(kafkaProducerService, times(1)).sendProduct(any(Product.class));
    }

    @Test
    void deleteProduct_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct("1");

        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct("1");
    }
}
