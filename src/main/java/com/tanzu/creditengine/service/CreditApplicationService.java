package com.tanzu.creditengine.service;

import com.tanzu.creditengine.messaging.CreditApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for submitting credit applications to the processing queue.
 */
@Service
public class CreditApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(CreditApplicationService.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${credit-engine.queue.name:application-requests}")
    private String queueName;

    public CreditApplicationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Submits a credit application to the RabbitMQ queue for asynchronous
     * processing.
     * 
     * @param message The credit application to submit
     */
    public void submitApplication(CreditApplicationMessage message) {
        logger.info("Submitting credit application to queue for SSN: {}", message.getSsn());

        rabbitTemplate.convertAndSend(queueName, message);

        logger.debug("Credit application submitted successfully to queue: {}", queueName);
    }
}
