package com.eclub.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;

@Document("product")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    @MongoId
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}
