package io.demo.repositories;

import io.demo.repositories.entities.FulfilOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FFOrderItemRepository extends JpaRepository<FulfilOrderItem, Long> {
    List<FulfilOrderItem> findByFulfilOrderId(String ffOrderId);
}
