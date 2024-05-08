package com.eclub.product.service;


import com.eclub.product.dto.ProductRequest;
import com.eclub.product.dto.ProductResponse;
import com.eclub.product.model.Product;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public interface IProductService {
    List<ProductResponse> findAll();
    ResponseEntity findById(String id);
    List<Product> finByPriceInRange(BigDecimal minPrice, BigDecimal maxPrice);
    ResponseEntity save (ProductRequest productRequest);
    ResponseEntity update (ProductRequest productRequest, String id);
    ResponseEntity deleteById(String id);
}
