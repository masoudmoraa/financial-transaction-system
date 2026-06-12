package com.example.bank.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String TRANSACTION_QUEUE = "bank.transaction.queue";
    public static final String BANK_EXCHANGE = "bank.direct.exchange";
    public static final String TRANSACTION_ROUTING_KEY = "bank.transaction.routingKey";

    @Bean
    public Queue transactionQueue() {
        return QueueBuilder.durable(TRANSACTION_QUEUE).build();
    }

    @Bean
    public DirectExchange bankExchange() {
        return ExchangeBuilder.directExchange(BANK_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding binding(Queue transactionQueue, DirectExchange bankExchange) {
        return BindingBuilder.bind(transactionQueue)
                .to(bankExchange)
                .with(TRANSACTION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}