package io.demo.services;

import io.demo.models.Order;

public interface IOrderService {
    void acceptOrder(Order order);

    void fulfillOrder(Long fulfilOrderId);
}
