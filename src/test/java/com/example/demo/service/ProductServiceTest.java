package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getAllProducts();

        // Assert
        assertThat(actualProducts).isEqualTo(expectedProducts);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        // Act
        Product foundProduct = productService.getProductById("1");

        // Assert
        assertThat(foundProduct).isEqualTo(product);
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void getProductById_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");
        verify(productRepository, times(1)).findById("999");
    }

    @Test
    void saveProduct_ShouldReturnSavedProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product savedProduct = productService.saveProduct(product);

        // Assert
        assertThat(savedProduct).isEqualTo(product);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_WithExistingId_ShouldReturnUpdatedProduct() {
        // Arrange
        Product updatedProduct = Product.builder()
                .id("1")
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("29.99"))
                .category("Updated Category")
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct("1", updatedProduct);

        // Assert
        assertThat(result).isEqualTo(updatedProduct);
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_WithExistingId_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        productService.deleteProduct("1");

        // Assert
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).delete(product);
    }
}
