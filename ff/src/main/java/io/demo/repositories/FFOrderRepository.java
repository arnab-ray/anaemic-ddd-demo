package io.demo.repositories;

import io.demo.repositories.entities.FulfilOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FFOrderRepository extends JpaRepository<FulfilOrder, Long> {
    Optional<FulfilOrder> findByOrderId(String orderId);

    Optional<FulfilOrder> findByFulfilOrderId(String fulfilOrderId);
}
