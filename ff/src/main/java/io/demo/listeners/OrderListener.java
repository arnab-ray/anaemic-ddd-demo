package io.demo.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.demo.models.Order;
import io.demo.services.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
    private static final Logger logger = LoggerFactory.getLogger(OrderListener.class);
    private final IOrderService orderService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderListener(IOrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${topic.order.request}",
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consumeOrder(@Payload String message, Acknowledgment acknowledgment) {
        try {
            var order = objectMapper.readValue(message, Order.class);
            orderService.acceptOrder(order);
        } catch (Exception e) {
            logger.error("Error while processing message", e);
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${topic.ff.order.request}",
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void consumeFFOrder(@Payload String message, Acknowledgment acknowledgment) {
        try {
            var ffOrderId = objectMapper.readValue(message, String.class);
            orderService.fulfillOrder(Long.getLong(ffOrderId));
        } catch (Exception e) {
            logger.error("Error while processing message", e);
        }
        acknowledgment.acknowledge();
    }
}
