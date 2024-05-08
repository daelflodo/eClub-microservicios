package com.eclub.product.repository;

import com.eclub.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product,String> {
    List<Product> findByPriceBetween (BigDecimal minPrice, BigDecimal maxPrice);
}
