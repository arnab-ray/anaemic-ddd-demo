package io.demo.services;

import io.demo.exceptions.NotFoundException;
import io.demo.repositories.InventoryRepository;
import io.demo.repositories.ReservationRepository;
import io.demo.repositories.entities.Inventory;
import io.demo.repositories.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WarehouseService implements IWarehouseService {
    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public WarehouseService(InventoryRepository inventoryRepository, ReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Inventory> getAvailableInventories(String listingId) {
        return inventoryRepository.findByListingId(listingId)
                .stream()
                .filter(it -> it.getTotalQuantity() > it.getAvailableQuantity())
                .toList();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void reserveInventory(String listingId, String warehouse, Integer quantity) {
        var whInventoryMap = getAvailableInventories(listingId)
                .stream()
                .collect(Collectors.toMap(Inventory::getWarehouseId, Function.identity()));

        var inventory = whInventoryMap.get(warehouse);
        if (inventory == null) {
            // TODO: How to avoid such awkward exceptions
            throw new NotFoundException("inventory is absent!");
        }

        // TODO: How could we prevent such manipulation in client code?
        var quantityAvailable = inventory.getAvailableQuantity();
        quantityAvailable -= quantity;
        inventory.setAvailableQuantity(quantityAvailable);

        var reservation = Reservation.builder()
                .warehouseId(warehouse)
                .quantity(quantity)
                .listingId(listingId)
                .build();

        inventoryRepository.save(inventory);
        reservationRepository.save(reservation);
    }
}
