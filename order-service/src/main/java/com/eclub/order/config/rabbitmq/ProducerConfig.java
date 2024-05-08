package com.eclub.order.config.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {

    @Value("${eclub.queue.name}")
    private String name;

    @Bean
    public Queue queue(){
        return new Queue(name,true);
    }
}

