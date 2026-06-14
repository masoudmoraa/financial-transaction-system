package com.example.bank.listener;

import com.example.bank.config.RabbitMQConfig;
import com.example.bank.dto.TransactionRequestDTO;
import com.example.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class TransactionListener {

    private final TransactionService transactionService;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_QUEUE)
    public void listen(TransactionRequestDTO transactionDto) {
        log.info("Message dequeued from RabbitMQ. Processing started for Tracking Code: {}", transactionDto.getTrackingCode());

        try {
            transactionService.processTransaction(transactionDto);
            log.info("Asynchronous processing successfully completed for Tracking Code: {}", transactionDto.getTrackingCode());

        } catch (Exception e) {
            log.error("Asynchronous processing failed for Tracking Code [{}]. Reason: {}",
                    transactionDto.getTrackingCode(), e.getMessage(), e);
        }
    }
}