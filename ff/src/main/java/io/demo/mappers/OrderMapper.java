package io.demo.mappers;

import io.demo.models.Order;
import io.demo.models.OrderItemState;
import io.demo.repositories.entities.FulfilOrder;
import io.demo.repositories.entities.FulfilOrderItem;

import java.util.List;

public class OrderMapper {
    public static FulfilOrder toFulfilOrder(Order order) {
        return FulfilOrder.builder()
                .orderId(order.getOrderId())
                .addressId(order.getAddressId())
                .build();
    }

    public static List<FulfilOrderItem> toFulfilOrderItem(Order order, Long fulfilOrderId) {
        return order.getOrderItems()
                .stream()
                .map(
                        orderItem ->
                                FulfilOrderItem.builder()
                                        .fulfilOrderId(fulfilOrderId == null ? "" : fulfilOrderId.toString())
                                        .orderItemId(orderItem.getOrderItemId())
                                        .listingId(orderItem.getListingId())
                                        .priceInPaise(orderItem.getPriceInPaise())
                                        .quantity(orderItem.getQuantity())
                                        .warehouse(orderItem.getWarehouseId())
                                        .status(OrderItemState.INITIATED)
                                        .build()
                )
                .toList();
    }
}
