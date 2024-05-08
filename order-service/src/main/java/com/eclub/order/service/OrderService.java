package com.eclub.order.service;

import com.eclub.order.config.rabbitmq.Producer;
import com.eclub.order.dto.InventoryResponse;
import com.eclub.order.dto.OrderLineItemsDTO;
import com.eclub.order.dto.OrderRequest;
import com.eclub.order.entity.Order;
import com.eclub.order.entity.OrderLineItems;
import com.eclub.order.event.OrderPlacedEvent;
import com.eclub.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.sleuth.Span;
//import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderService {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Producer producer;

//    @Autowired
//    private Tracer tracer;

    @Transactional
    public Order placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLineItems);

        List<String> skuCode = order.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode)//traigo todos los codigos sku de todos los elementos
                .collect(Collectors.toList());

        System.out.println("SKUs" + skuCode);
        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCode).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::isInStock);

        if (!allProductInStock)
            throw new IllegalArgumentException("El producto no esta en stock");
        Order orderSave = orderRepository.save(order);
        sendMessageRMQ("Notificacion con RabbitMQ Pedido Ordenado con Exito");
        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
        return orderSave;
//        return "Successful order";

//        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
//        try (Tracer.SpanInScope isLookup = tracer.withSpan(inventoryServiceLookup.start())) {
//            inventoryServiceLookup.tag("call", "inventory-service");
//            InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
//                    .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCode).build())
//                    .retrieve()
//                    .bodyToMono(InventoryResponse[].class)
//                    .block();
//
//            boolean allProductInStock = Arrays.stream(inventoryResponsesArray)
//                    .allMatch(InventoryResponse::isInStock);
//
//            if (!allProductInStock)
//                throw new IllegalArgumentException("El producto no esta en stock");
//
//            orderRepository.save(order);
//            return "Successful order";
//        } finally {
//            inventoryServiceLookup.end();
//        }
    }

    private void sendMessageRMQ(String message){
        log.info("!El emnsage '{}' ha sido enviado con exito", message);
        producer.send(message);
    }
    private OrderLineItems mapToDto(OrderLineItemsDTO orderLineItemsDTO) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDTO.getPrice());
        orderLineItems.setQuantity(orderLineItemsDTO.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDTO.getSkuCode());
        return orderLineItems;
    }
}
