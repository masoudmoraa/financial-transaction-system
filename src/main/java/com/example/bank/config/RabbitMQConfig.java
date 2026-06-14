package com.example.bank.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

// Configuration class to set up RabbitMQ messaging components.
@Configuration
public class RabbitMQConfig {

    public static final String TRANSACTION_QUEUE = "bank.transaction.queue";
    public static final String BANK_EXCHANGE = "bank.direct.exchange";
    public static final String TRANSACTION_ROUTING_KEY = "bank.transaction.routingKey";

    // Creates a durable queue to store bank transaction messages safely.
    @Bean
    public Queue transactionQueue() {
        return QueueBuilder.durable(TRANSACTION_QUEUE).build();
    }

    // Creates a direct exchange to route messages based on exact keys.
    @Bean
    public DirectExchange bankExchange() {
        return ExchangeBuilder.directExchange(BANK_EXCHANGE).durable(true).build();
    }

    // Binds the transaction queue to the exchange using a specific routing key.
    @Bean
    public Binding binding(Queue transactionQueue, DirectExchange bankExchange) {
        return BindingBuilder.bind(transactionQueue)
                .to(bankExchange)
                .with(TRANSACTION_ROUTING_KEY);
    }

    // Configures RabbitMQ to serialize and deserialize messages as JSON.
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}