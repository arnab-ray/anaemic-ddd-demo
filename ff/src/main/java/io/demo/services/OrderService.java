package io.demo.services;

import io.demo.catalog.CatalogClient;
import io.demo.exceptions.NotFoundException;
import io.demo.mappers.OrderMapper;
import io.demo.models.Order;
import io.demo.models.OrderItemState;
import io.demo.publishers.KafkaProducer;
import io.demo.repositories.FFOrderItemRepository;
import io.demo.repositories.FFOrderRepository;
import io.demo.repositories.entities.FulfilOrder;
import io.demo.repositories.entities.FulfilOrderItem;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.demo.models.Constants.FF_TOPIC;

@Service
public class OrderService implements IOrderService {
    private final FFOrderRepository ffOrderRepository;
    private final FFOrderItemRepository ffOrderItemRepository;
    private final KafkaProducer kafkaProducer;
    private final IWarehouseService warehouseService;
    private final CatalogClient catalogClient;

    @Autowired
    public OrderService(
            FFOrderRepository ffOrderRepository,
            FFOrderItemRepository ffOrderItemRepository,
            KafkaProducer kafkaProducer,
            IWarehouseService warehouseService,
            CatalogClient catalogClient) {
        this.ffOrderRepository = ffOrderRepository;
        this.ffOrderItemRepository = ffOrderItemRepository;
        this.kafkaProducer = kafkaProducer;
        this.warehouseService = warehouseService;
        this.catalogClient = catalogClient;
    }

    @Override
    @Transactional
    public void acceptOrder(Order order) {
        Optional<FulfilOrder> maybeFulfilOrder = ffOrderRepository.findByOrderId(order.getOrderId());

        if (maybeFulfilOrder.isEmpty()) {
            // TODO: Why this awkward mapper is required in domain service?
            var fulfilOrder = OrderMapper.toFulfilOrder(order);
            // TODO: Why the client has to pass the fulfil order ID?
            var fulfilOrderItems = OrderMapper.toFulfilOrderItem(order, fulfilOrder.getId());

            ffOrderRepository.save(fulfilOrder);
            ffOrderItemRepository.saveAll(fulfilOrderItems);

            // TODO: Why should a domain service bother about the means of event?
            kafkaProducer.sendMessage(FF_TOPIC, fulfilOrder.getOrderId(), fulfilOrder.getOrderId());
        }
    }

    @Override
    @Transactional
    public void fulfillOrder(Long fulfilOrderId) {
        Optional<FulfilOrder> maybeFulfilOrder = ffOrderRepository.findById(fulfilOrderId);

        // TODO: Does the method signature reveal this side-effect of calling the function?
        if (maybeFulfilOrder.isEmpty()) {
            throw new NotFoundException("fulfil order is absent!");
        }

        var fulfilOrderItems = ffOrderItemRepository.findByFulfilOrderId(fulfilOrderId.toString());

        if (fulfilOrderItems.isEmpty()) {
            throw new NotFoundException("fulfil order items not found!");
        }

        for (FulfilOrderItem fulfilOrderItem : fulfilOrderItems) {
            // TODO: Why the client should know the exact state of order for comparison?
            if (OrderItemState.INITIATED.equals(fulfilOrderItem.getStatus())
                    && isValidCatalog(fulfilOrderItem.getListingId())) {
                try {
                    var inventories = warehouseService.getAvailableInventories(fulfilOrderItem.getListingId());
                    var matchedWhInventory = inventories.stream()
                            .filter(it -> it.getWarehouseId().equals(fulfilOrderItem.getWarehouse()) &&
                                    it.getAvailableQuantity() >= fulfilOrderItem.getQuantity())
                            .findFirst();

                    if (matchedWhInventory.isPresent()) {
                        // TODO: There is always a chance to reorder these strings and cause bugs
                        warehouseService.reserveInventory(
                                fulfilOrderItem.getListingId(), fulfilOrderItem.getWarehouse(),
                                fulfilOrderItem.getQuantity());
                        reserveFFOrderItem(fulfilOrderItem);
                    } else {
                        var otherWhInventory =
                                inventories.stream()
                                        .filter(it -> it.getAvailableQuantity() >= fulfilOrderItem.getQuantity())
                                        .findFirst();

                        if (otherWhInventory.isEmpty()) {
                            throw new NotFoundException("inventory is absent!");
                        }

                        warehouseService.reserveInventory(
                                fulfilOrderItem.getListingId(), otherWhInventory.get().getWarehouseId(),
                                fulfilOrderItem.getQuantity());
                        reserveFFOrderItem(fulfilOrderItem);
                    }
                } catch (Exception e) {
                    // Do Nothing
                    // TODO: How could we avoid this awkward exception better?
                }
            }
        }
    }

    private void reserveFFOrderItem(FulfilOrderItem fulfilOrderItem) {
        fulfilOrderItem.setStatus(OrderItemState.RESERVED);
        ffOrderItemRepository.save(fulfilOrderItem);
    }

    private boolean isValidCatalog(String listingId) {
        var response = catalogClient.getBookDetailsByListingId(listingId);
        return response != null;
    }
}
