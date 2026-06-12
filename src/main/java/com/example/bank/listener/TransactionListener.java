package com.example.bank.listener;

import com.example.bank.config.RabbitMQConfig;
import com.example.bank.dto.TransactionRequestDTO;
import com.example.bank.service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_QUEUE)
    public void listen(TransactionRequestDTO transactionDto) {
        System.out.println("Message dequeued from RabbitMQ. Tracking Code: " + transactionDto.getTrackingCode());

        try {
            transactionService.processTransaction(transactionDto);
            System.out.println("Asynchronous processing successfully completed for: " + transactionDto.getTrackingCode());
        } catch (Exception e) {
            System.err.println("Asynchronous processing failed for [" + transactionDto.getTrackingCode() + "]: " + e.getMessage());
        }
    }
}