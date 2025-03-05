package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContaining(String name);
}
