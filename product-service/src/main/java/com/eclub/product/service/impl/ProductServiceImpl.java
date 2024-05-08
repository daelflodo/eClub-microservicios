package com.eclub.product.service.impl;

import com.eclub.product.dto.ProductRequest;
import com.eclub.product.dto.ProductResponse;
import com.eclub.product.model.Product;
import com.eclub.product.percistence.IProductDAO;
import com.eclub.product.repository.ProductRepository;
import com.eclub.product.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

        @Autowired
    private IProductDAO productDAO;

    @Override
    public List<ProductResponse> findAll() {
        List<Product> products = productDAO.findAll();
        if (products.isEmpty()) {
            return (List<ProductResponse>) ResponseEntity.notFound().build();// reviasar la respuesta
//            return Collections.emptyList();
        }
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }


    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    @Override
    public ResponseEntity<ProductResponse> findById(String id) {
        Optional<Product> productOptional = productDAO.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();// reviasar la respuesta
        }
        Product product = productOptional.get();
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
        return ResponseEntity.ok(productResponse);
    }

    @Override
    public List<Product> finByPriceInRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productDAO.finByPriceInRange(minPrice, maxPrice);
    }

    @Override
    public ResponseEntity save(ProductRequest ProductRequest) {
//        }
        if (ProductRequest.getName().isBlank() || ProductRequest.getPrice() == null || ProductRequest.getDescription().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Product product = Product.builder()
                .name(ProductRequest.getName())
                .description(ProductRequest.getDescription())
                .price(ProductRequest.getPrice())
                .build();
        productDAO.save(product);
        return ResponseEntity.ok(product);
    }

    @Override
    public ResponseEntity update(ProductRequest productRequest, String id) {
        Optional<Product> productOptional = productDAO.findById(id);
        System.out.println("PRODOPTIONAL"+productOptional);
        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product product = productOptional.get();
        System.out.println("PRODUCTO: "+product);
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        productDAO.update(product);
        return ResponseEntity.ok("Registro Actualizado");
    }

    @Override
    public ResponseEntity deleteById(String id) {
        if (id == null) {
            return ResponseEntity.notFound().build();
        }
        Optional<Product> productOptional = productDAO.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
//        Product product = productOptional.get();
        productDAO.deleteById(id);
        return ResponseEntity.ok("Registro Eliminado");
    }

}
