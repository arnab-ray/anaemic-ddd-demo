package io.demo.services;

import io.demo.repositories.entities.Inventory;

import java.util.List;

public interface IWarehouseService {
    List<Inventory> getAvailableInventories(String listingId);

    void reserveInventory(String listingId, String warehouse, Integer quantity);
}
