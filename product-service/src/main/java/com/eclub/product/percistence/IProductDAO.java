package com.eclub.product.percistence;

import com.eclub.product.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProductDAO {
    List<Product> findAll();
    Optional<Product> findById(String id);
    List<Product> finByPriceInRange(BigDecimal minPrice, BigDecimal maxPrice);
    void save (Product product);
    void update (Product product);
    void deleteById(String id);
}
