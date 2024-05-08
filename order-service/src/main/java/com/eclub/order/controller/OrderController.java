package com.eclub.order.controller;

import com.eclub.order.dto.OrderRequest;
import com.eclub.order.entity.Order;
import com.eclub.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallBackMethod")
//    @TimeLimiter(name = "inventory")
//    @Retry(name = "inventory")
    public CompletableFuture<Order> MakeOrder(@RequestBody OrderRequest orderRequest) {
        System.out.println("ORDER: " + orderRequest);
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
//        orderService.placeOrder(orderRequest);
//        return "Pedido realizado con exito";
    }

        public CompletableFuture<String> fallBackMethod (OrderRequest orderRequest, RuntimeException runtimeException){
            return CompletableFuture.supplyAsync(() -> "Oops! Ha ocurrido un error al realizar el pedido");
        }

    }
