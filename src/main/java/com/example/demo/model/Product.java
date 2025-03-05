package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
public class Product {
    
    @Id
    private String id;
    
    @NotBlank(message = "Product name is required")
    @Field(type = FieldType.Text, name = "name")
    private String name;
    
    @Field(type = FieldType.Text, name = "description")
    private String description;
    
    @Positive(message = "Price must be positive")
    @Field(type = FieldType.Double, name = "price")
    private BigDecimal price;
    
    @Field(type = FieldType.Keyword, name = "category")
    private String category;
}
